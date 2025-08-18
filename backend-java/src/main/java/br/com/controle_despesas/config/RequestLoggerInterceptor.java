package br.com.controle_despesas.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class RequestLoggerInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggerInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response,
            Object handler) {
        logger.info("REQ: {} {}", request.getMethod(), request.getRequestURI());
        return true;
    }
}
