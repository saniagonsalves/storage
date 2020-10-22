package com.saniagonsalves.storage.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class AuthErrorHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse,
                       AccessDeniedException e) throws IOException {

        Authentication auth
            = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            log.info("User '" + auth.getName() + "' attempted to access the protected URL: "
                     + httpServletRequest.getRequestURI());
            log.info(auth.toString());
            httpServletResponse.getWriter()
                .println("Hi " + auth.getName() + ", this action is not permitted. Access Denied.");
        } else {
            httpServletResponse.getWriter().println("Hi, this action is not permitted. Access Denied.");
        }

        httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
}
