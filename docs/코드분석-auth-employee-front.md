# 코드 분석 — Auth / Employee / System + Frontend 구조

> 목적: 리뷰 지적 목록이 아니라 **현재 코드가 어떻게 흐르는지**를 파악하기 위한 분석 문서  
> 범위: `admin-service`(auth, employee, system, common) + `hisfrontend` 전체 구조  
> 기준 시점: develop (직원 수정 API/모달 반영 후)

---

## 1. 전체 그림

```
[ Browser :3000 ]
      │
      │  RootLayout → AppFrame (세션 가드)
      │                 ├─ 비로그인 → /login → LoginForm
      │                 └─ 로그인   → AppShell(Sidebar + Header) + page
      │
      │  features/*/api.ts  →  lib/axios (baseURL)
      ▼
[ admin-service :8080/8081 ]
      │
      ├─ /api/auth/**      → auth 패키지
      ├─ /api/employees/** → employee 패키지 (+ auth.Account 저장)
      └─ system            → (패키지 미구현)
```

### 담당 경계

| 영역 | Backend | Frontend |
|------|---------|----------|
| Auth | `auth/` + `common`(BCrypt, CORS) | `features/auth`, `components/auth`, `AppFrame` 세션 |
| Employee | `employee/` (+ Account 생성 시 auth repo 사용) | `features/admin`, `components/admin`, `app/admin/user` |
| System | 없음 | Sidebar `area: system` → `/admin/*` 메뉴, `app/system` 홈만 |

---

## 2. Backend 패키지 구조

```
kr.co.seoulit.his.adminservice
├── AdminserviceApplication.java
├── auth/
│   ├── controller/AuthController.java      POST /api/auth/login
│   ├── dto/LoginRequest.java, LoginResponse.java
│   ├── entity/Account.java                 TABLE ACCOUNT
│   ├── repository/AccountRepository.java
│   └── service/AuthService.java
│       └── impl/AuthServiceImpl.java
├── employee/
│   ├── controller/EmployeeController.java  POST/PUT/GET /api/employees
│   ├── dto/CreateEmployeeRequest.java
│   │       UpdateEmployeeRequest.java
│   │       EmployeeResponse.java
│   ├── entity/Emp.java                     TABLE EMPLOYEE
│   ├── repository/EmpRepository.java
│   └── service/EmployeeService.java
│       └── impl/EmployeeServiceImpl.java
├── common/
│   ├── config/AppConfig.java               PasswordEncoder, CORS
│   ├── dto/ApiResponse.java
│   └── exception/
│       ├── BusinessException.java
│       ├── ErrorCode.java                  ADM001~ADM007
│       └── GlobalExceptionHandler.java
└── system/                                 ← 디렉터리/코드 없음
```

### 레이어 규칙 (현재 구현)

| 레이어 | 역할 |
|--------|------|
| Controller | HTTP 매핑, `@Valid`, `ApiResponse` 래핑 |
| Service 인터페이스 | 메서드 계약 |
| ServiceImpl | 비즈니스 + `@Transactional` + Entity 조립 |
| Repository | Spring Data JPA |
| Entity | 기존 Oracle 테이블 매핑 (PK는 시퀀스를 서비스에서 할당) |
| DTO | 요청/응답 계약 (FE types와 맞춤) |

---

## 3. 데이터 모델

### 3.1 EMPLOYEE (`Emp`)

| 컬럼 | 필드 | 비고 |
|------|------|------|
| EMP_ID | empId | PK, `EMP_SEQ.NEXTVAL` |
| EMP_NO | empNo | unique |
| NAME | name | |
| EMAIL / PHONE | email / phone | nullable |
| HIRE_DATE / RETIRE_DATE | hireDate / retireDate | `LocalDate` |
| EMP_STATUS | empStatus | 문서상 `ACTIVE \| LEAVE \| RETIRED` (String) |
| DEPT_CODE | deptCode | |
| CREATED_AT / UPDATED_AT | createdAt / updatedAt | `@PrePersist` / `@PreUpdate` |

### 3.2 ACCOUNT (`Account`)

| 컬럼 | 필드 | 비고 |
|------|------|------|
| ACCOUNT_ID | accountId | PK, `ACCOUNT_SEQ.NEXTVAL` |
| EMP_ID | empId | unique FK 성격 (1:1) |
| LOGIN_ID | loginId | unique |
| PW_HASH | pwHash | BCrypt만 저장 |
| ACCOUNT_STATUS | accountStatus | `ACTIVE \| LOCKED \| DISABLED` |
| CREATED_AT / UPDATED_AT | | |

### 3.3 관계

```
EMPLOYEE 1 ─── 1 ACCOUNT
   empId  ◄──  empId
```

- 로그인: `ACCOUNT.loginId` → `ACCOUNT.empId` → `EMPLOYEE`
- 직원등록: 한 트랜잭션에서 EMP 저장 후 ACCOUNT 저장
- 직원수정: EMP만 갱신. ACCOUNT의 loginId/pwHash는 건드리지 않음

---

## 4. 공통 계약 (ApiResponse / ErrorCode)

### 4.1 응답 포맷

```json
{ "code": 200, "message": "SUCCESS", "data": { } }
```

- 성공: `ApiResponse.ok(data)` → `code=200`, `message="SUCCESS"`
- 비즈니스 실패: `message`에 **에러 코드 문자열** (예: `"ADM005"`), `data=null`
- FE는 `error.response.data.message`를 받아 `resolveAuthMessage` / `resolveAdmMessage`로 한글 변환

### 4.2 ErrorCode (ADM)

| 코드 | HTTP | 사용처 |
|------|------|--------|
| ADM001 | 409 | 사번 중복 |
| ADM002 | 409 | 로그인 ID 중복 |
| ADM003 | 404 | 직원 없음 |
| ADM004 | 400 | 잘못된 요청 (정의만, validation은 다른 경로) |
| ADM005 | 401 | 로그인 실패 (ID 없음 / 비번 틀림 / 기타 상태) |
| ADM006 | 403 | 계정 LOCKED |
| ADM007 | 403 | 계정 DISABLED |

### 4.3 예외 처리 흐름

```
Controller / Service
    │ throw BusinessException(ErrorCode)
    ▼
GlobalExceptionHandler.handleBusiness
    → HTTP = ErrorCode.httpStatus
    → body.message = ErrorCode.code  (예: ADM005)

@Valid 실패
    ▼
handleValidation
    → HTTP 400
    → body.message = FieldError 한글 메시지 연결 (코드가 아님)

그 외 Exception
    ▼
handleUnexpected
    → HTTP 500
    → body.message = 예외클래스:메시지
```

---

## 5. Auth 흐름 상세

### 5.1 API

| Method | Path | Request | Response data |
|--------|------|---------|---------------|
| POST | `/api/auth/login` | `{ loginId, password }` | `LoginResponse` |

**LoginResponse 필드:** `accountId`, `empId`, `empNo`, `name`, `loginId`, `accountStatus`

### 5.2 서버 시퀀스

```
AuthController.login(@Valid LoginRequest)
  → AuthServiceImpl.login
       1. loginId trim
       2. AccountRepository.findByLoginId
            없으면 → ADM005 LOGIN_FAILED
       3. PasswordEncoder.matches(plain, pwHash)
            실패 → ADM005
       4. accountStatus 분기
            LOCKED   → ADM006
            DISABLED → ADM007
            null이 아니고 ACTIVE 아님 → ADM005
       5. EmpRepository.findById(account.empId)
            없으면 → ADM003
       6. LoginResponse 빌드 후 반환
```

포인트:
- 토큰/세션을 서버가 발급하지 않음. 응답 DTO만 반환
- `spring-security-crypto`의 BCrypt만 사용. SecurityFilterChain 없음
- ID 없음과 비번 오류를 같은 ADM005로 처리 (계정 열거 완화)

### 5.3 프론트 로그인 시퀀스

```
app/login/page.tsx
  → LoginForm
       1. (optional) localStorage his.auth.savedLoginId 복원
       2. submit → features/auth/api.login
            → axios POST /api/auth/login
       3. 성공: saveSession(LoginResponse) → sessionStorage "his.auth.session"
       4. router.replace("/")
       5. 실패: resolveAuthMessage(message) 표시
```

### 5.4 세션 가드 (클라이언트)

```
RootLayout
  → AppFrame
       useEffect:
         session = getSession()
         없으면 && path ≠ /login  → replace("/login")
         있으면 && path = /login  → replace("/")
         준비되면:
           비로그인 또는 /login → children만 (셸 없음)
           로그인               → AppShell로 children 감쌈
```

- `sessionStorage` 존재 여부만 검사. 서버 검증 없음
- axios 요청에 Authorization 헤더를 붙이지 않음
- 로그아웃: `clearSession()` + `/login` 이동

---

## 6. Employee 흐름 상세

### 6.1 API 목록

| Method | Path | Body | 설명 |
|--------|------|------|------|
| POST | `/api/employees` | CreateEmployeeRequest | EMP + ACCOUNT 동시 등록 |
| PUT | `/api/employees/{empId}` | UpdateEmployeeRequest | EMP만 수정 |
| GET | `/api/employees` | — | 전체 목록 (Account join 배치) |

단건 GET / 삭제 / 비밀번호 변경 API는 없음.

### 6.2 Create 요청·응답 계약

**CreateEmployeeRequest**

| 필드 | 필수 | 비고 |
|------|------|------|
| empNo | Y | max 20 |
| name | Y | max 100 |
| email, phone | N | |
| hireDate | N | `yyyy-MM-dd` 문자열 |
| empStatus | N | 기본 ACTIVE |
| deptCode | N | |
| loginId | Y | |
| password | Y | min 4 (평문 → 서버에서 해시) |

**UpdateEmployeeRequest**

| 필드 | 필수 | 비고 |
|------|------|------|
| name | Y | |
| email, phone | N | |
| hireDate, retireDate | N | |
| empStatus, deptCode | N | |
| ~~empNo / loginId / password~~ | — | 수정 범위 밖 |

**EmployeeResponse** = FE `Employee` 타입과 동일 계약  
(`empId`, `empNo`, `name`, `email`, `phone`, `hireDate`, `retireDate`, `empStatus`, `deptCode`, `createdAt`, `updatedAt`, `loginId`, `accountStatus`)

### 6.3 Create 서버 시퀀스

```
EmployeeController.create
  → EmployeeServiceImpl.create (@Transactional)
       1. existsByEmpNo → 있으면 ADM001
       2. existsByLoginId → 있으면 ADM002
       3. Emp 조립
            empId = EMP_SEQ.NEXTVAL (native)
            trim / blankToNull / hireDate parse
            empStatus 없으면 "ACTIVE"
            retireDate = null
            saveAndFlush
       4. Account 조립
            accountId = ACCOUNT_SEQ.NEXTVAL
            empId = savedEmp.empId
            pwHash = passwordEncoder.encode(password)
            accountStatus = "ACTIVE"
            saveAndFlush
       5. toResponse(emp, account)
```

### 6.4 Update 서버 시퀀스

```
EmployeeController.update(empId, request)
  → EmployeeServiceImpl.update
       1. findById(empId) 없으면 ADM003
       2. name/email/phone/hireDate/retireDate/empStatus/deptCode 세팅
            empStatus는 값이 있을 때만 변경
       3. saveAndFlush(emp)
       4. Account는 findByEmpId (없으면 null)
            → loginId/pw 변경 없음
       5. toResponse
```

### 6.5 List 서버 시퀀스

```
findAll (readOnly)
  1. empRepository.findAll()
  2. empIds 수집 → accountRepository.findByEmpIdIn(empIds)  // N+1 회피
  3. Map<empId, Account>로 toResponse
```

### 6.6 프론트 직원 화면 흐름

```
app/admin/user/page.tsx
  → EmployeePage (client)
       mount: getEmployees() → setEmployees
       [직원등록] → EmployeeRegisterModal
            onSubmit → createEmployee → 목록 맨 앞 추가
       [수정] → EmployeeEditModal
            onSubmit → updateEmployee(empId, payload)
                     → 목록에서 해당 행 교체
```

**RegisterModal**
- 필수: empNo, name, loginId, password(+confirm)
- 사번 입력 시 loginId 비어 있으면/동기화 중이면 loginId 따라감
- password는 API로만 전송, 응답에 없음

**EditModal**
- empNo, loginId는 readOnly 표시
- 수정 가능: name, email, phone, hireDate, retireDate, empStatus, deptCode
- open + employee 변경 시 form 재세팅 (`useEffect`)

### 6.7 FE API 매핑

| 함수 | HTTP |
|------|------|
| `getEmployees()` | GET `/api/employees` |
| `createEmployee(payload)` | POST `/api/employees` |
| `updateEmployee(empId, payload)` | PUT `/api/employees/{empId}` |

메시지: `features/admin/messages.ts`의 `ADM_MESSAGES` + `resolveAdmMessage`

---

## 7. Frontend 전체 구조

### 7.1 디렉터리 역할

```
src/
├── app/                 App Router (URL = 화면 진입점)
├── components/          UI (페이지/모달/레이아웃)
│   ├── admin/           직원 화면 (구현)
│   ├── auth/            로그인 폼 (구현)
│   ├── layout/          AppFrame, AppShell, Header
│   └── sidebar/         Sidebar + 메뉴 데이터 인라인
├── features/            API·타입·메시지·(예정) slice/saga
│   ├── admin/           직원 API/types/messages (구현)
│   ├── auth/            login API, session, messages (구현)
│   └── patient|reception|labimaging/...  스텁
├── lib/axios.ts         공통 HTTP 클라이언트
├── store/               Redux 자리 (현재 빈 파일, Provider 미연결)
├── constants/           (비어 있음 / 메뉴는 Sidebar로 이전됨)
├── types/, utils/       자리표시
```

### 7.2 앱 부트 흐름

```
app/layout.tsx (Server Component)
  → <AppFrame>{children}</AppFrame>

AppFrame (Client)
  → 세션 분기 → (로그인 시) AppShell
       AppShell
         → Sidebar | Header(title) | <main>{children}</main>
```

Header 제목:
- `findChildMenuByPath(pathname)?.label`
- 없으면 `findWorkAreaMenuByPath`의 area label
- 없으면 `"HIS"`
- 메뉴 데이터·헬퍼는 `Sidebar.tsx`에 정의 후 export

### 7.3 라우트 vs 구현 상태

| 경로 | 상태 |
|------|------|
| `/login` | LoginForm 구현 |
| `/` | 홈 더미 |
| `/admin/user` | EmployeePage 구현 |
| `/admin`, `/system`, 업무영역 홈 | 안내 placeholder |
| `/patient/**`, `/reception/**`, `/labimaging/**` | Page placeholder |
| Sidebar의 billing/pharmacy/surgery/outpatient 등 | **app 라우트 없음** (링크만 존재) |

### 7.4 데이터 접근 패턴 (현재 실제)

가이드 문서상: Presentation → Redux action → Saga → api  
**실제 구현:**

```
Component (useState)
  → features/{service}/api.ts
      → lib/axios
```

- `store/store.ts`, `rootReducer`, `rootSaga` 빈 파일
- `features/admin/slice.ts`, `saga.ts` 빈 파일
- auth는 slice/saga 파일 자체 없음

### 7.5 axios

```
baseURL = NEXT_PUBLIC_ADMIN_API_BASE_URL ?? http://192.168.1.128:8080
Content-Type: application/json
timeout: 15000

response interceptor:
  실패 시 error.response.data.message 를 Error(message)로 reject
  → 화면에서 resolveXxxMessage로 코드→한글 변환
```

요청 인터셉터(토큰 첨부) 없음.

### 7.6 Sidebar 메뉴 IA

L0 업무영역 (토글):
- 원무 `frontOffice` → `/frontoffice` + serviceRoots patient/reception/billing
- 진료 `clinical` → outpatient/emergency/inpatient
- 진료지원 `ancillary` → labimaging/pharmacy/surgery
- 시스템 `system` → `/system`, serviceRoots `/admin`

L1 시스템 children (ADM):
- `/admin/user` 직원 ← **유일 실구현**
- `/admin/permission`, `commoncode`, `document`, `hospital` ← 라우트 미생성

---

## 8. System 현황

Backend:
- `system` 패키지(controller/service/entity 등) **코드 없음**
- package-info placeholder도 제거된 상태

Frontend:
- 워크영역 홈 `app/system/page.tsx` (문구만)
- 실제 기능 URL은 `/admin/*` (서비스 코드 ADM)
- “시스템(UI 업무영역)” ≠ “system 패키지(백엔드)” — 이름만 비슷하고 구현 경계는 **Admin(ADM)**

향후 착수 후보 (가이드 기준):
- 공통코드, 메뉴, 권한, 문서양식, 병원설정 → admin-service + `app/admin/...`

---

## 9. End-to-End 시나리오

### 시나리오 A — 로그인 후 직원 목록

```
1. 브라우저 → /login
2. LoginForm → POST /api/auth/login { loginId, password }
3. AuthServiceImpl: Account 조회 → BCrypt → status → Emp 조회
4. FE: sessionStorage 저장 → /
5. AppFrame: session 있음 → AppShell
6. Sidebar → 시스템 → 직원 → /admin/user
7. EmployeePage mount → GET /api/employees
8. EmployeeServiceImpl.findAll → Emp 전체 + Account IN 조회 → 테이블 렌더
```

### 시나리오 B — 직원 등록

```
1. 직원등록 모달 → CreateEmployeeRequest
2. POST /api/employees
3. 서버 트랜잭션: EMP_SEQ → EMPLOYEE insert → ACCOUNT_SEQ → ACCOUNT insert (BCrypt)
4. EmployeeResponse 반환 → 목록 unshift
5. 이후 해당 loginId로 시나리오 A 로그인 가능
```

### 시나리오 C — 직원 수정

```
1. 행 [수정] → EditModal (empNo/loginId 고정 표시)
2. PUT /api/employees/{empId} + UpdateEmployeeRequest
3. Emp 필드만 갱신, Account 조회만 (pw/loginId 변경 없음)
4. 목록 해당 행 교체
```

---

## 10. 파일 인덱스 (분석용)

### Backend — Auth
- `auth/controller/AuthController.java`
- `auth/service/impl/AuthServiceImpl.java`
- `auth/entity/Account.java`
- `auth/repository/AccountRepository.java`
- `auth/dto/LoginRequest.java`, `LoginResponse.java`

### Backend — Employee
- `employee/controller/EmployeeController.java`
- `employee/service/impl/EmployeeServiceImpl.java`
- `employee/entity/Emp.java`
- `employee/repository/EmpRepository.java`
- `employee/dto/CreateEmployeeRequest.java`
- `employee/dto/UpdateEmployeeRequest.java`
- `employee/dto/EmployeeResponse.java`

### Backend — Common
- `common/dto/ApiResponse.java`
- `common/exception/ErrorCode.java`
- `common/exception/GlobalExceptionHandler.java`
- `common/config/AppConfig.java`

### Frontend — Shell / Auth
- `app/layout.tsx`
- `components/layout/AppFrame.tsx`, `AppShell.tsx`, `Header.tsx`
- `components/sidebar/Sidebar.tsx`
- `components/auth/LoginForm.tsx`
- `features/auth/api.ts`, `session.ts`, `types.ts`, `messages.ts`
- `lib/axios.ts`

### Frontend — Employee
- `app/admin/user/page.tsx`
- `components/admin/EmployeePage.tsx`
- `components/admin/EmployeeRegisterModal.tsx`
- `components/admin/EmployeeEditModal.tsx`
- `features/admin/api.ts`, `types.ts`, `messages.ts`

---

## 11. 현재 구현된 것 / 아직 없는 것 (사실 정리)

| 항목 | 있음 | 없음 |
|------|------|------|
| 로그인 검증 (BCrypt) | O | |
| 로그인 토큰·서버 세션 | | X |
| API 인증 필터 | | X |
| FE sessionStorage 가드 | O | |
| FE Authorization 헤더 | | X |
| 직원 CRUD 중 C/U/List | O | 단건 GET, Delete |
| 비밀번호 변경 API | | X (주석만 존재) |
| EMP+ACCOUNT 트랜잭션 등록 | O | |
| System(공통코드/메뉴/권한) BE | | X |
| Redux Provider / Saga 연결 | | X (파일 자리만) |
| 직원 FE 등록·수정 UI | O | 페이징/검색 |

---

## 12. 다음에 코드를 따라갈 때 추천 순서

1. **로그인 E2E**: `LoginForm` → `auth/api` → `AuthServiceImpl` → `session` → `AppFrame`
2. **직원 등록 E2E**: `EmployeeRegisterModal` → `createEmployee` → `EmployeeServiceImpl.create` → ACCOUNT/EMP 테이블
3. **직원 수정 E2E**: `EmployeeEditModal` → `update` → Emp만 변경되는 지점 확인
4. **공통 에러**: `BusinessException` → `GlobalExceptionHandler` → FE `messages.ts`
5. **System 착수 전**: Sidebar `/admin/*` 경로와 `app/admin` 폴더를 먼저 맞춰 두기

이 문서는 흐름·구조·계약 파악용이다. 이슈 우선순위 논의는 별도 리뷰 보드/이슈로 이어가면 된다.
