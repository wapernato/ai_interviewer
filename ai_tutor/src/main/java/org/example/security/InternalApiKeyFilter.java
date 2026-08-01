package org.example.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.config.InternalApiProperties;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import org.example.exception.ErrorResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class InternalApiKeyFilter extends OncePerRequestFilter {

    private static final String INTERNAL_PATH = "/api/internal";
    private static final String INTERNAL_API_KEY_HEADER = "X-Internal-Api-Key";

    private final InternalApiProperties internalApiProperties;
    private final ObjectMapper objectMapper;

    public InternalApiKeyFilter(InternalApiProperties internalApiProperties,
                                ObjectMapper objectMapper) {
        this.internalApiProperties = internalApiProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (!isInternalRequest(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String actualApiKey = request.getHeader(INTERNAL_API_KEY_HEADER);
        String expectedApiKey = internalApiProperties.getApiKey();

        if (actualApiKey == null || !actualApiKey.equals(expectedApiKey)) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "UNAUTHORIZED",
                    "Invalid internal API key."
            );

            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            objectMapper.writeValue(response.getWriter(), errorResponse);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isInternalRequest(String path) {
        return path.equals(INTERNAL_PATH) || path.startsWith(INTERNAL_PATH + "/");
    }
}
