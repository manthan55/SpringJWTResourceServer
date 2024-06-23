package com.manthan.springjwtresourceserver.middlewares;

import com.manthan.springjwtresourceserver.dtos.ValidateTokenRequestDTO;
import com.manthan.springjwtresourceserver.dtos.ValidateTokenResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.Nullable;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private String authServerHost = "http://localhost:8080";
    private RestTemplate restTemplate;

    public AuthInterceptor(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        System.out.println("preHandle");

        String token = request.getHeader("Authorization");

        if(token == null || token.isBlank() || token.isEmpty()) {
            // token doesnt exist in request header
            response.setStatus(401);
            response.getWriter().write("no token present in header");
            response.getWriter().flush();
            return false;
        }


        try{
            token = token.split("\\s+")[1];

            ValidateTokenRequestDTO requestDTO = new ValidateTokenRequestDTO();
            requestDTO.setToken(token);

            ValidateTokenResponseDTO responseDTO = restTemplate.postForEntity(
                    authServerHost+"/auth/validateToken",
                    requestDTO,
                    ValidateTokenResponseDTO.class
            ).getBody();

            if(responseDTO.valid) return true;
        }
        catch(Exception ex){
            response.setStatus(500);
            response.getWriter().write(ex.getMessage());
        }

        response.setStatus(401);
        response.getWriter().write("invalid token");
        response.getWriter().flush();
        return false;
    }


    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) {
        System.out.println("postHandle");
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) {
        System.out.println("afterCompletion");
    }
}
