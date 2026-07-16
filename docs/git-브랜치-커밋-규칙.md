# Git 브랜치 · 커밋 · Push 규칙

> 이 문서는 **항상** 이 순서를 따른다.  
> develop에 바로 작업·커밋하지 않는다.

---

## 1. 공통 원칙

1. **작업은 feature 브랜치에서만** 한다.
2. **develop에 직접 커밋하지 않는다.**
3. 커밋 메시지는 기존 팀 스타일을 따른다.  
   예: `IH2-22 1. 메뉴 목록 API GET /api/menus 추가 기능추가`
4. Push는 아래 **Front / Back 각각의 순서**를 지킨다.

---

## 2. Front (`hisfrontend`)

### 브랜치 예

- `feature/front-management/ih2-22-menu`
- `feature/front-management/ih2-28-front-create`

### 작업 순서

```
1) feature 브랜치 생성/체크아웃
2) 기능 구현
3) feature 브랜치에 커밋
4) feature → develop 반영 후 develop push
```

### 명령 예시

```bash
# 1. develop 최신화 후 feature 브랜치
git switch develop
git pull origin develop
git switch -c feature/front-management/ih2-XX-작업명

# 2. 작업 후 커밋 (feature 브랜치에서)
git add .
git commit -m "IH2-XX 1. ... 기능추가"

# 3. feature push (선택, PR용)
git push -u origin HEAD

# 4. develop에 합친 뒤 develop push
git switch develop
git pull origin develop
git merge feature/front-management/ih2-XX-작업명
git push origin develop
```

### Front 한 줄 요약

**feature에서 작업·커밋 → develop에 머지 → develop push**

---

## 3. Back (`admin-service`)

### 도메인 브랜치 (중간 집결)

| 담당 영역 | 도메인 브랜치 | feature 예 |
|-----------|---------------|------------|
| Auth | `IH2-1-auth` 또는 auth 계열 | `feature/auth/IH2-5-login` |
| Employee | `IH2-2-employee` 또는 employee 계열 | `feature/employee/IH2-12-employee-info` |
| System | `IH2-3-system` 또는 system 계열 | `feature/system/IH2-22-menu` |

> 실제로 쓰는 도메인 브랜치 이름이 `IH2-1-auth` / `IH2-2-employee` / `IH2-3-system` 이면 그걸 쓴다.  
> `feature/auth/...`, `feature/employee/...`, `feature/system/...` 는 **기능 작업 브랜치**다.

### 작업 순서 (중요)

```
1) feature 브랜치에서 작업·커밋
2) 해당 도메인 브랜치에 선 반영 (auth / employee / system)
3) 그 다음 develop에 반영 후 develop push
```

**Back은 develop으로 바로 가지 않는다.  
반드시 auth면 auth, employee면 employee, system이면 system에 먼저 넣은 뒤 develop.**

### 명령 예시 (system / 메뉴 작업)

```bash
# 1. feature에서 작업
git switch develop
git pull origin develop
git switch feature/system/IH2-22-menu
# ... 코딩 ...
git add .
git commit -m "IH2-22 1. 메뉴 목록 API GET /api/menus 추가 기능추가"

# 2. 도메인(system)에 선 커밋/머지
git switch IH2-3-system
git pull origin IH2-3-system
git merge feature/system/IH2-22-menu
git push origin IH2-3-system

# 3. 그 다음 develop
git switch develop
git pull origin develop
git merge IH2-3-system
git push origin develop
```

### Auth / Employee 도 동일

```
feature/auth/...     → IH2-1-auth (또는 auth 도메인)     → develop push
feature/employee/... → IH2-2-employee (또는 employee 도메인) → develop push
feature/system/...   → IH2-3-system (또는 system 도메인)   → develop push
```

### Back 한 줄 요약

**feature 작업 → 도메인(auth/employee/system) 선 반영 → develop push**

---

## 4. Front + Back 같이 칠 때

메뉴처럼 FE/BE를 같이 하면:

| 순서 | 저장소 | 할 일 |
|------|--------|--------|
| 1 | admin-service | `feature/system/...` → system 도메인 → develop |
| 2 | hisfrontend | `feature/front-management/...` → develop |

가능하면 **Back develop 반영 후 Front develop** 순으로 맞춘다. (API가 먼저 있어야 FE 연동 확인이 쉽다.)

---

## 5. 금지 / 주의

| 하면 안 됨 | 이유 |
|------------|------|
| develop에서 바로 코딩·커밋 | 브랜치 규칙 위반 |
| Back feature → develop 직행 (도메인 생략) | auth/employee/system 선 커밋 규칙 위반 |
| 서버 기동을 에이전트에게 맡기기 | 로컬에서 직접 기동 |
| docs / 가이드 무단 대량 커밋 | 필요할 때만, 비밀정보 없이 |

---

## 6. 체크리스트 (커밋 전)

- [ ] 지금 브랜치가 `develop`이 아닌가?
- [ ] Front면: feature → develop 순서인가?
- [ ] Back면: feature → **도메인(auth/employee/system)** → develop 순서인가?
- [ ] 커밋 메시지에 티켓번호(IH2-XX)와 기능추가/기능수정이 들어갔나?
- [ ] Push 대상이 규칙상 맞는 브랜치인가?

---

## 7. 현재 메뉴 작업 예시 (참고)

| 저장소 | feature | 다음 단계 |
|--------|---------|-----------|
| admin-service | `feature/system/IH2-22-menu` | → system 도메인 선 반영 → develop push |
| hisfrontend | `feature/front-management/ih2-22-menu` | → develop merge 후 develop push |
