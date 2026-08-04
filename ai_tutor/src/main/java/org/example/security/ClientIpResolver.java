package org.example.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class ClientIpResolver {

    public String resolve(HttpServletRequest request) {
        String forwardIp = request.getHeader("X-Forwarded-For");

        if(forwardIp != null && !forwardIp.isBlank()){
            String[] parts = forwardIp.split(",");
            String firstIp = parts[0];
            if(!((firstIp.trim()).isBlank())){
                return firstIp.trim();
            }
        }

        String realIp = request.getHeader("X-Real-IP");

        if(realIp != null && !realIp.isBlank()){
            return realIp.trim();
        }

        return request.getRemoteAddr();
    }
}
