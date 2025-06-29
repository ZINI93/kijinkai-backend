package com.kijinkai.domain.exchange.util;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component // Spring Bean으로 등록되도록 @Component 어노테이션 추가
public class HttpClientUtil {

    private final HttpClient httpClient;

    public HttpClientUtil() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2) // HTTP/2 사용
                .followRedirects(HttpClient.Redirect.NORMAL) // 일반적인 리다이렉트 팔로우
                .connectTimeout(Duration.ofSeconds(10)) // 연결 타임아웃 10초
                .build();
    }

    /**
     * HTTP GET 요청을 보내고 응답 본문을 문자열로 반환합니다.
     * @param url 요청할 URL
     * @return 응답 본문 문자열
     * @throws IOException HTTP 통신 중 입출력 오류 발생 시
     * @throws InterruptedException 대기 중 스레드 인터럽트 발생 시
     * @throws RuntimeException HTTP 응답 상태 코드가 2xx 범위가 아닐 때
     */
    public String get(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return response.body();
        } else {
            throw new IOException("HTTP GET 요청 실패. 상태 코드: " + response.statusCode() + ", 응답 본문: " + response.body());
        }
    }
}