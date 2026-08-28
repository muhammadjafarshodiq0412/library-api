package com.jafarshodiq.library.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

import static com.jafarshodiq.library.constant.BaseConstant.HeaderParameter.X_TRACKING_REF;
import static com.jafarshodiq.library.constant.BaseConstant.HeaderParameter.MDC_KEY;

@Component
public class TrackingRefFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String trackingRef = request.getHeader(X_TRACKING_REF);

        if (trackingRef == null || trackingRef.isBlank()) {
            trackingRef = UUID.randomUUID().toString();
        }

        try {
            MDC.put(MDC_KEY, trackingRef);

            response.setHeader(
                    X_TRACKING_REF,
                    trackingRef
            );

            filterChain.doFilter(request, response);

        } finally {
            MDC.remove(MDC_KEY);
        }
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }
}