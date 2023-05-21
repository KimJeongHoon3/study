spring_security_oauth2_0

- spring security oauth2.0 resource server
  - 기본적인 컨셉은 리소스 서버는 리소스 제공을 위해 인가서버로 요청들어온놈이 권한이 있는지 물어보고 권한이 있을때만 리소스를 제공해주는것
    - 주 역할
      - Verifying Access Token : Access Token의 확인
      - Verifying Scope : Scope의 확인(Scope는 각 Client가 가지는 리소스에 대한 권한의 개념)
      - Error Codes and Unauthorizaed Access : 에러코드및 비인가 접근에 대한 처리.
  - 의존성
    - spring-security-oauth2-resource-server
    - spring-security-oauth2-jose
    - => spring-boot-starter-oauth2-resource-server 이거 쓰면 위 두개 들어가있음
  - jwt를 사용하는 Resource server
    - 기동시점에 하는 처리 (아래 프로세스는 인가서버가 반드시 기동되어있는 상태여야함. 인가서버 다운되어있으면 기동실패)
       1. Provider 설정 엔드포인트 또는 인가 서버 메타데이터 엔드포인트를 찔러서 응답으로 jwks_url 프로퍼티를 처리
       2. jwks_url에 유효한 공개키를 질의하기위한 검증 전략 설정
       3. 발행서버 url에 대한 각 Jwt iss 클레임을 검증할 전략을 설정
    - 런타임에 하는 처리
      - Authorization: Bearer 헤더에 포함한 모든 요청 처리
      - jwt 형식에 이상이 없으면 리소스 서버는 아래와 같이 처리
         1. 기동시 jwks_url 엔드포인트에서 가져와jwt에 매칭한 공개키로 서명을 검증
         2. jwt에 있는 exp, nbf타임스탬프, iss 클레임을 검증
         3. 각 scope를 "SCOPE_" 프리픽스를 달아 권한(Authority)에 매핑
      - `Authentication#getPrincipal` 결과는 스프링 시큐리티의 Jwt 객체
        - `Authentication#getName`은 JWT의 sub 프로퍼티 값이 있으면 이 값을 사용
      - springboot에서 인가서버 지정방법
        ```yml
            spring:
            security:
                oauth2:
                resourceserver:
                    jwt:
                    issuer-uri: https://idp.example.com/issuer
        ```
  - 인가 설정
    - 디폴트
      - scope나 scp 속성을 기반으로 권한(Authority)승인 리스트("SCOPE_" prefix를 셋팅해줌)를 만든다
        - ex
          - `{ …, "scope" : "READ"}` 이렇게 jwt의 claim에 셋팅이 되어있다면, security에서 "SCOPE_READ" 이렇게 만들어서 권한을 확인하는게 디폴트
    - scope 속성이 아닌 다른걸 사용하고 싶을때
      - `jwtAuthenticationConverter()`
        - `Jwt` -> `Authentification` 으로 변환하는 컨버터를 설정 
        - ex. authorities 라는 claim으로 권한을 변경하고, 권한의 prefix를 "ROLE_"로 변경하고자하는 경우
          ```java
            @EnableWebSecurity
            public class CustomAuthoritiesClaimName extends WebSecurityConfigurerAdapter {
                protected void configure(HttpSecurity http) {
                    http
                        .authorizeRequests(authorize -> authorize
                            .anyRequest().authenticated()
                        )
                        .oauth2ResourceServer(oauth2 -> oauth2
                            .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                            )
                        );
                }
            }

            JwtAuthenticationConverter jwtAuthenticationConverter() {
                JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
                grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
                grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

                JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
                jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
                return jwtAuthenticationConverter;
            }
          ```
  - 검증 설정
    - 리소스 서버는 기본적으로 iss 클레임, exp 클레임, nbf 타임스템프 클레임을 검증
    - ex. aud 클레임이 messaging 일때만
      ```java
        OAuth2TokenValidator<Jwt> audienceValidator() {
            return new JwtClaimValidator<List<String>>(AUD, aud -> aud.contains("messaging"));
        }
      ```
  - Claim들을 Jwt객체로 mapping 관련 설정
    - `MappedJwtClaimSetConverter` 으로 jwt claim들을 매핑한다
    - 사용방법
      ```java
        @Bean
        JwtDecoder jwtDecoder() {
            NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();

            MappedJwtClaimSetConverter converter = MappedJwtClaimSetConverter
                    .withDefaults(Collections.singletonMap("sub", this::lookupUserIdBySub)); // sub의 디폴트 컨버터를 제외한 다른 디폴트컨버터는 그대로 유지

            // MappedJwtClaimSetConverter.withDefaults(Collections.singletonMap("legacyclaim", legacy -> null)); // 이렇게 제거도 가능

            jwtDecoder.setClaimSetConverter(converter);

            return jwtDecoder;
        }

        ////////////////////////////////////////

        // 매우 개별적인 커스텀 가능
        public class UsernameSubClaimAdapter implements Converter<Map<String, Object>, Map<String, Object>> {
            private final MappedJwtClaimSetConverter delegate =
                    MappedJwtClaimSetConverter.withDefaults(Collections.emptyMap());

            public Map<String, Object> convert(Map<String, Object> claims) {
                Map<String, Object> convertedClaims = this.delegate.convert(claims);

                String username = (String) convertedClaims.get("user_name");
                convertedClaims.put("sub", username);

                return convertedClaims;
            }
        }

        @Bean
        JwtDecoder jwtDecoder() {
            NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
            jwtDecoder.setClaimSetConverter(new UsernameSubClaimAdapter());
            return jwtDecoder;
        }
      ```
  - 리소스 서버가 인가서버와 통신시 옵션(ConnectionTimeout, readTimeout 등)은 NimbusJwtDecoder 의 RestOperations 에서 설정
    - 인가서버로 확인하는 작업은 NimbusJwtDecoder에서 해준다.. (물론 리소스서버가 인가서버와 통신할 일 없음(ex. jwt 검증을 위한 공개키가 리소스서버에 셋팅되어있는경우) 상관없고)
  - 리소스 서버가 토큰만 검증하는게아닌, 토큰만료시 재발급하는것도 가능
    ```java
        // 아래와 같이 사용가능하나, spring-security-oauth2-client 모듈이 있어야 아래 사용가능
        /*
            해당 로직은 아래 3가지일을 수행
                1. introspection 엔드포인트에 토큰의 유효성 검증위임
                2. /userinfo 엔드포인트와 관련있는 적절한 클라이언트 등록정보를 검색
                3. /userinfo 엔드포인트를 실행해서 결과를 반환
        */
        
        public class UserInfoOpaqueTokenIntrospector implements OpaqueTokenIntrospector {
            private final OpaqueTokenIntrospector delegate =
                    new NimbusOpaqueTokenIntrospector("https://idp.example.org/introspect", "client", "secret");
            private final OAuth2UserService oauth2UserService = new DefaultOAuth2UserService();

            private final ClientRegistrationRepository repository;

            // ... constructor

            @Override
            public OAuth2AuthenticatedPrincipal introspect(String token) {
                OAuth2AuthenticatedPrincipal authorized = this.delegate.introspect(token);
                Instant issuedAt = authorized.getAttribute(ISSUED_AT);
                Instant expiresAt = authorized.getAttribute(EXPIRES_AT);
                ClientRegistration clientRegistration = this.repository.findByRegistrationId("registration-id");
                OAuth2AccessToken token = new OAuth2AccessToken(BEARER, token, issuedAt, expiresAt);
                OAuth2UserRequest oauth2UserRequest = new OAuth2UserRequest(clientRegistration, token);
                return this.oauth2UserService.loadUser(oauth2UserRequest);
            }
        }

        @Bean
        OpaqueTokenIntrospector introspector() {
            return new UserInfoOpaqueTokenIntrospector(...);
        }
    ```
    
- 출처 
  - https://godekdls.github.io/Spring%20Security/oauth2/#123-oauth-20-resource-server
    
---

- 기타 참고

  - hasRole vs hasAuthority
    - https://www.baeldung.com/spring-security-expressions

  - [jwt](https://velopert.com/2389)
    - 등록된 (registered) 클레임
      - 등록된 클레임들은 서비스에서 필요한 정보들이 아닌, 토큰에 대한 정보들을 담기위하여 이름이 이미 정해진 클레임들입니다. 등록된 클레임의 사용은 모두 선택적 (optional)이며, 이에 포함된 클레임 이름들은 다음과 같습니다:
        - iss: 토큰 발급자 (issuer)
        - sub: 토큰 제목 (subject)
        - aud: 토큰 대상자 (audience)
        - exp: 토큰의 만료시간 (expiraton), 시간은 NumericDate 형식으로 되어있어야 하며 (예: 1480849147370) 언제나 현재 시간보다 이후로 설정되어있어야합니다.
        - nbf: Not Before 를 의미하며, 토큰의 활성 날짜와 비슷한 개념입니다. 여기에도 NumericDate 형식으로 날짜를 지정하며, 이 날짜가 지나기 전까지는 토큰이 처리되지 않습니다.
        - iat: 토큰이 발급된 시간 (issued at), 이 값을 사용하여 토큰의 age 가 얼마나 되었는지 판단 할 수 있습니다.
        - jti: JWT의 고유 식별자로서, 주로 중복적인 처리를 방지하기 위하여 사용됩니다. 일회용 토큰에 사용하면 유용합니다.