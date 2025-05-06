package com.example.apigateway_service.filter;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {
    Environment env;

    public AuthorizationHeaderFilter(Environment env) {
        super(Config.class);
        this.env = env;
    }

    public static class Config {
        // Put configuration properties here
    }

    // 반환된 GatewayFilter는 요청을 가로채서 인증(Authorization) 헤더를 검사하는 역할
    @Override
    public GatewayFilter apply(Config config) {
        // exchange : 현재 요청과 응답을 담고 있음
        // chain : 다음 필터로 요청을 넘기는 역할
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 요청의 헤더(Header) 에 Authorization이 없으면 401 UNAUTHORIZED 응답을 반환하고 요청을 차단
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
            }

            // Authorization 헤더에서 값을 가져와 Bearer 문자열을 제거한 후 JWT 토큰만 추출
            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = authorizationHeader.replace("Bearer ", "");

            // JWT가 유효하지 않다면 401 UNAUTHORIZED 응답을 반환 후 요청을 차단
            if (!isJwtValid(jwt)) {
                return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
            }

            // JWT 검사가 통과되면 다음 필터로 요청을 넘김 → 정상적인 요청만 API Gateway를 지나도록 함
            return chain.filter(exchange);
        };
    }

    // JWT 토큰이 유효한지 확인
    private boolean isJwtValid(String jwt) {
        byte[] secretKeyBytes = Base64.getEncoder().encode(env.getProperty("token.secret").getBytes());
        SecretKey signingKey = new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS512.getJcaName());

        boolean returnValue = true;
        String subject = null;

        try {
            JwtParser jwtParser = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build();

            subject = jwtParser.parseClaimsJws(jwt).getBody().getSubject();

//            // Jwts.parser() : JWT를 분석하는 도구
//            // setSigningKey(env.getProperty("token.secret")) : JWT 검증을 위해 비밀키 사용
//            subject = Jwts.parser().setSigningKey(env.getProperty("token.secret"))
//                    // .parseClaimsJws(jwt) : 전달받은 jwt 토큰을 해석해서 유효한지 확인
//                    .parseClaimsJws(jwt).getBody()
//                    // 토큰에서 subject(사용자 ID) 추출
//                    .getSubject();
        // JWT가 조작되었거나 만료되었으면 예외 발생
        } catch (Exception ex) {
            returnValue = false;
        }

        // subject가 null이거나 비어 있으면 false 반환
        if (subject == null || subject.isEmpty()){
            returnValue = false;
        }

        return returnValue;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        // exchange에서 HTTP 응답 객체 가져오기
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        log.error(err);
        // 응답 완료 (Mono<Void> 반환), Spring WebFlux 방식에서 비동기적으로 응답을 종료
        return response.setComplete();
    }
}
