package gnu.capstone.G_Learn_E.global.security;

import gnu.capstone.G_Learn_E.global.jwt.JwtAuthenticationFilter;
import gnu.capstone.G_Learn_E.global.security.exception.handler.CustomAccessDeniedHandler;
import gnu.capstone.G_Learn_E.global.security.exception.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityPathProperties securityPathProperties;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint authenticationExceptionHandler;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    // CORS 설정
    CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration globalConfig = new CorsConfiguration();
        // globalConfig.setAllowedOrigins(List.of("http://localhost:3000"));
//		globalConfig.setAllowedOrigins(List.of("*")); // 테스트용 TODO: 위에 패턴으로 변경해야 함
        globalConfig.setAllowedOriginPatterns(List.of("*")); // 모든 도메인 허용
        globalConfig.setAllowedMethods(List.of("GET", "POST", "PATCH", "PUT", "DELETE"));
        globalConfig.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        // globalConfig.setAllowedHeaders(List.of("*"));
        globalConfig.setAllowCredentials(true);

        CorsConfiguration swaggerConfig = new CorsConfiguration();
        swaggerConfig.addAllowedOriginPattern("*");
        swaggerConfig.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
        swaggerConfig.setAllowedHeaders(List.of("*"));
        swaggerConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", globalConfig);                  // 기본 API 설정
        source.registerCorsConfiguration("/swagger-ui/**", swaggerConfig);       // Swagger UI 전용 설정
        source.registerCorsConfiguration("/v3/api-docs/**", swaggerConfig);      // OpenAPI docs 설정
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.
                httpBasic(HttpBasicConfigurer::disable)
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource())) // CORS 설정 추가
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationExceptionHandler)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(securityPathProperties.getPermitAll().toArray(new String[0])).permitAll()
                        .requestMatchers(securityPathProperties.getAuthenticated().toArray(new String[0])).authenticated()
                        .requestMatchers(securityPathProperties.getAnonymous().toArray(new String[0])).anonymous()
                        .requestMatchers(securityPathProperties.getEmailAuth().toArray(new String[0])).permitAll()
                        .anyRequest().permitAll())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
//                .addFilterBefore(verificationStatusFilter, UsernamePasswordAuthenticationFilter.class)
//                .addFilterBefore(pendingUserFilter, VerificationStatusFilter.class)
//                .addFilterBefore(jwtAuthenticationFilter, PendingUserFilter.class)
        ;
        return httpSecurity.build();
    }
}
