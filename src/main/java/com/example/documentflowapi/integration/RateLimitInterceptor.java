package com.example.documentflowapi.integration;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.time.Duration;

@Component
@Slf4j
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Bucket bucket = Bucket.builder()
            .addLimit(Bandwidth.classic(2, Refill.intervally(2, Duration.ofMinutes(1))))
            .build();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("DEBUG: Interceptor yoxlayır... Qalan token: " + bucket.getAvailableTokens());

        if (bucket.tryConsume(1)) {
            return true;
        } else {
            log.warn("RATE LIMIT BLOKLADI: {}", request.getRemoteAddr());
            response.setStatus(429);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\": \"Həddindən artıq sorğu! Bir az gözləyin.\"}");
            return false;
        }
    }
}