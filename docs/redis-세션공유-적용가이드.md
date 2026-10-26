# Redis 세션 공유 적용 가이드

| 항목 | 내용 |
|---|---|
| 대상 | HIS 전체 MSA 서비스 담당자 |
| 목적 | 한 번 로그인하면 모든 서비스가 같은 로그인 상태를 인식하게 한다 |
| 작성 기준 | admin-service (Spring Boot 4.1.0 / Java 17) 에서 검증 완료 |
| 소요 시간 | 서비스당 20분 내외 |

---

## 1. 왜 하는가

지금까지 각 서비스는 로그인 세션을 **자기 서버 메모리**에 들고 있었다.
그래서 두 가지 문제가 있었다.

1. 서버를 껐다 켜면 로그인이 전부 풀린다.
2. admin-service 에서 로그인해도 billing-service 는 그 사람이 누구인지 모른다.
   그래서 각 서비스의 API 는 사실상 아무나 부를 수 있는 상태였다.

세션을 **공용 Redis** 에 저장하면 두 문제가 함께 풀린다.
어느 서비스가 요청을 받든 같은 세션을 보게 되고, 서버를 재기동해도 로그인이 유지된다.

로그인 화면과 로그인 처리는 지금처럼 admin-service 가 담당한다.
각 서비스가 할 일은 **그 세션을 읽을 수 있게 설정하고, 로그인 안 된 요청을 막는 것** 뿐이다.

---

## 2. 시작 전에 — Spring Boot 버전 확인

`build.gradle` 을 열어 버전을 확인한다.

```gradle
id 'org.springframework.boot' version '4.1.0'   // ← 이 숫자
```

**이 가이드는 Boot 4.x 기준이다.** 3.x 를 쓰고 있다면 5장(부록)을 먼저 볼 것.
의존성 이름이 다르고, 그대로 따라 하면 **오류 없이 조용히 동작하지 않는다.**

---

## 3. 적용 절차

### 3-1. 의존성 추가 (`build.gradle`)

```gradle
implementation 'org.springframework.boot:spring-boot-starter-session-data-redis'
```

> **주의.** 인터넷 예제에 흔히 나오는 아래 조합은 Boot 4 에서 쓰면 안 된다.
> 빌드는 성공하는데 세션이 Redis 에 저장되지 않고, **에러 메시지조차 나오지 않는다.**
> ```gradle
> // Boot 3 용 — Boot 4 에서는 조용히 무시된다
> implementation 'org.springframework.boot:spring-boot-starter-data-redis'
> implementation 'org.springframework.session:spring-session-data-redis'
> ```

### 3-2. 설정 추가 (`application.properties`)

```properties
spring.data.redis.host=192.168.1.128
spring.data.redis.port=6379
spring.data.redis.password=${REDIS_PASSWORD:}
```

`192.168.1.128` 은 Redis 가 도는 메인 서버 PC 주소다.

**비밀번호는 파일에 직접 적지 않는다.** 이 파일은 GitHub 에 그대로 올라간다.
각자 PC 의 환경변수에 넣어두면 위 자리에 채워진다.

```
setx REDIS_PASSWORD 전달받은비밀번호
```

실행 후 **터미널과 IntelliJ 를 새로 켜야** 적용된다.
IntelliJ 실행 구성(Run Configuration)의 `Environment variables` 에 넣어도 된다.

비밀번호 값은 GitHub · 공개 채널이 아닌 곳으로 따로 전달한다.

### 3-3. `SessionUser.java` 복사

아래 경로에 **똑같이** 만든다.

```
src/main/java/kr/co/seoulit/his/common/session/SessionUser.java
```

> **패키지 경로를 바꾸면 안 된다.**
> Redis 에 저장되는 JSON 에 이 클래스의 경로가 문자열로 그대로 적힌다.
> 경로가 다르면 그 서비스만 세션을 못 읽는다.
> 서비스 이름이 안 들어가는 `common` 아래에 둔 이유가 이것이다.

파일 내용은 admin-service 저장소의 같은 경로에서 그대로 복사한다.
담기는 값은 다음과 같다.

| 필드 | 설명 |
|---|---|
| `empId` | 직원 PK. 다른 서비스가 "작성자" 등으로 저장할 때 쓰는 키 |
| `empNo` | 사번 (예: `E202608001`) |
| `empName` | 직원 이름 |
| `deptCode` | 부서 공통코드 (`DEPT_CD`) |
| `loginId` | 로그인 아이디 |
| `roleCodes` | 역할 코드. 여러 개면 쉼표로 이어짐 (예: `"01"`, `"01,02"`) |
| `menuCodes` | 조회 가능한 메뉴 코드. 쉼표로 이어짐. 없으면 빈 문자열 |
| `accountId`, `accountStatus` | 프론트 응답 형태 유지용. 다른 서비스는 쓸 일 없음 |

`roleCodes` 와 `menuCodes` 가 목록이 아니라 문자열인 데는 이유가 있다.
목록으로 두면 저장된 JSON 이 `["java.util.ArrayList", ["01"]]` 처럼 감싸져서 읽기 나빠진다.

실제로 담기는 값은 이런 모양이다.

```
roleCodes : "01"
menuCodes : "FRONT_OFFICE,CLINICAL,ANCILLARY,SYSTEM,RCP_RECEPTION,...,EMG_TRIAGE"
```

#### 권한을 확인할 때 — `contains` 를 그냥 쓰면 안 된다

```java
// ❌ 이렇게 하면 안 된다
if (user.getMenuCodes().contains("LAB_GROUP")) { ... }
```

문자열 부분 일치라서 **`LAB_GROUP` 권한이 없어도 `LAB_GROUP_ADMIN` 이 목록에 있으면 통과한다.**
에러가 나지 않고 조용히 틀린 판정이 나므로 알아채기 어렵다.

```java
// ⭕ 쉼표로 나눈 뒤 정확히 비교한다
List<String> menuCodes = Arrays.asList(user.getMenuCodes().split(","));

if (!menuCodes.contains("BIL_PAYMENT_PROCESS")) {
    // 권한 없음 처리
}
```

역할도 같은 방식으로 나눠서 비교한다.

```java
List<String> roleCodes = Arrays.asList(user.getRoleCodes().split(","));
```

> 값이 없을 때는 빈 문자열(`""`)이 온다. `"".split(",")` 는 `[""]` 이 되므로
> 위 방식대로 `contains` 로 비교하면 문제없이 `false` 가 된다.

### 3-4. `RedisSessionConfig.java` 복사

admin-service 저장소의 아래 파일을 복사한다.
**패키지 선언만 각자 프로젝트에 맞게 바꾸면 된다.**

```
src/main/java/kr/co/seoulit/his/adminservice/common/config/RedisSessionConfig.java
```

이 파일은 세션을 Redis 에 **JSON 으로** 저장하게 만든다.
기본값은 자바 전용 형식이라 다른 서비스가 읽지 못한다.

> **빈 이름 `springSessionDefaultRedisSerializer` 를 바꾸지 말 것.**
> Spring Session 이 이 이름으로 찾는다. 이름이 다르면 에러 없이 기본 형식으로 되돌아가고,
> 그 서비스만 세션을 못 읽는 상태가 된다. 원인을 찾기 매우 어려운 종류의 고장이다.

### 3-5. 세션 가드 인터셉터

로그인하지 않은 요청이 API 에 닿지 못하게 막는다.
화면만 잠그면 주소창에 `/api/...` 를 직접 쳤을 때 데이터가 그대로 나온다.

아래 파일을 각자 프로젝트에 만든다. (경로·패키지는 각자 규칙대로)

```java
package (각자 패키지).common.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

/**
 * [로그인 세션 확인 인터셉터]
 *
 * 통과시키는 경우가 두 가지다. 부르는 쪽이 "사람"인지 "다른 서비스"인지에 따라 다르다.
 *  - 사람   : 브라우저가 로그인해서 받은 세션이 살아 있는지 본다.
 *  - 서비스 : 팀이 나눠 가진 내부 키(X-Internal-Api-Key)가 맞는지 본다.
 *
 * 서비스 간 호출은 로그인한 사람이 없으므로 세션이 존재할 수 없다.
 * 세션만 보고 막으면 상대 서비스가 기동조차 못 한다.
 */
public class SessionGuardInterceptor implements HandlerInterceptor {

    /** admin-service 가 세션에 넣는 값의 이름. 바꾸면 안 된다. */
    public static final String SESSION_USER_KEY = "LOGIN_USER";

    /** 서비스 간 호출임을 알리는 헤더 이름 */
    public static final String INTERNAL_API_KEY_HEADER = "X-Internal-Api-Key";

    private final String internalApiKey;

    public SessionGuardInterceptor(String internalApiKey) {
        this.internalApiKey = internalApiKey;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // [1] 다른 서비스가 부른 것인지 먼저 본다
        String requestKey = request.getHeader(INTERNAL_API_KEY_HEADER);
        if (internalApiKey != null && !internalApiKey.isBlank() && internalApiKey.equals(requestKey)) {
            return true;
        }

        // [2] 사람이 부른 것이므로 로그인 세션을 확인한다
        // getSession(false) 는 "없으면 새로 만들지 말고 null 을 달라"는 뜻이다.
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(SESSION_USER_KEY) != null) {
            return true;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("{\"code\":401,\"message\":\"로그인이 필요합니다.\",\"data\":null}");
        return false;
    }
}
```

등록은 `WebMvcConfigurer` 에서 한다.
**예외 경로는 서비스마다 다르므로 각자 판단해서 넣는다.**

```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new SessionGuardInterceptor(internalApiKey))
            .addPathPatterns("/api/**")
            .excludePathPatterns(
                    // 로그인 전에 불려야 하는 경로가 있으면 여기에 적는다
            );
}
```

`internalApiKey` 는 `application.properties` 의 값을 읽어서 넣는다.

```java
@Value("${msa.internal-api-key:}")
private String internalApiKey;
```

---

## 4. 확인 방법

### 4-1. 세션이 Redis 에 들어가는가

서비스를 기동하고 **admin-service 화면에서 로그인**한 뒤, 메인 서버 PC 에서 확인한다.

```
redis-cli -a 비밀번호 KEYS spring:session:sessions:*
```

키가 하나 이상 나오면 정상이다.

### 4-2. 내 서비스가 그 세션을 읽는가

컨트롤러에서 꺼내 본다.

```java
@GetMapping("/api/example/whoami")
public String whoami(HttpSession session) {
    SessionUser user = (SessionUser) session.getAttribute("LOGIN_USER");
    return user.getEmpNo() + " / " + user.getEmpName();
}
```

로그인한 브라우저로 호출했을 때 사번과 이름이 나오면 성공이다.

### 4-3. 안 막힌 데가 없는가

로그인하지 않은 상태(시크릿 창)에서 API 주소를 직접 친다.
`401` 이 나와야 정상이다. 데이터가 나오면 인터셉터 경로 설정을 다시 본다.

---

## 5. 자주 나는 문제

| 증상 | 원인 |
|---|---|
| 빌드는 되는데 Redis 가 계속 비어 있다 | Boot 3 용 의존성을 넣었다. 3-1 의 경고 참고 |
| `NOAUTH Authentication required` | `REDIS_PASSWORD` 환경변수 미설정. `setx` 후 IntelliJ 재시작 |
| 로그인은 되는데 `ClassCastException` | `SessionUser` 패키지 경로가 다르다. 3-3 참고 |
| 세션은 읽히는데 값이 `LinkedHashMap` | `RedisSessionConfig` 를 안 넣었거나 빈 이름이 다르다 |
| 다른 서비스 기동 중 401 로 죽는다 | 서비스 간 호출에 `X-Internal-Api-Key` 헤더를 안 붙였다 |
| 요청이 한참 걸리다 실패한다 | Redis 주소가 틀렸다. 메인 서버 IP 확인 |
| 권한이 없는데 통과된다 | `menuCodes` 를 `contains` 로 바로 비교했다. 3-3 의 권한 확인 참고 |
| 권한을 줬는데 `menuCodes` 가 비어 있다 | 그 사람의 **역할**에 메뉴가 배정되지 않았다. 직원이 아니라 역할에 붙는다 |

---

## 6. 부록 — Spring Boot 3.x 를 쓰는 경우

> **이 절은 admin-service 에서 검증하지 못했다.** (admin-service 는 4.1.0)
> 3.x 를 쓰는 팀은 적용 후 4장 확인 절차를 반드시 거치고, 결과를 공유해 달라.

의존성이 다르다.

```gradle
implementation 'org.springframework.boot:spring-boot-starter-data-redis'
implementation 'org.springframework.session:spring-session-data-redis'
```

`RedisSessionConfig` 도 그대로 쓸 수 없다.
Boot 3 은 Jackson 2 를 쓰기 때문에 클래스 이름과 패키지가 다르다.

| Boot 4 (Jackson 3) | Boot 3 (Jackson 2) |
|---|---|
| `tools.jackson.databind.*` | `com.fasterxml.jackson.databind.*` |
| `GenericJacksonJsonRedisSerializer` | `GenericJackson2JsonRedisSerializer` |
| `DefaultTyping` (별도 클래스) | `ObjectMapper.DefaultTyping` (중첩) |

만드는 방식도 빌더가 아니라 `ObjectMapper` 를 직접 설정해서 넘긴다.
설정해야 할 내용 자체는 같다 — **타입 정보를 `@class` 항목으로 붙이고, 허용 범위를
`kr.co.seoulit.his.` 로 제한한다.**

3.x 팀이 나오면 admin-service 담당자와 함께 맞춰본 뒤 이 문서에 확정본을 추가한다.
