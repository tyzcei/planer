package by.bsuir.semesterpassport.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Позволяет использовать @PreAuthorize в контроллерах
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, AuthenticationProvider authenticationProvider) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // 1. Критично для React: разрешаем все предзапросы OPTIONS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 2. Публичные эндпоинты (регистрация, вход)
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // 3. АДМИН-ПАНЕЛЬ: только для пользователей с ролью ADMIN
                        .requestMatchers("/api/v1/admin/**").hasAuthority("ADMIN")

                        // 4. УПРАВЛЕНИЕ ГРУППОЙ: создание лаб для всех (доступно Админу и Старосте)
                        .requestMatchers("/api/v1/labs/group-creation").hasAnyAuthority("ADMIN", "GROUP_LEADER")

// Внутри authorizeHttpRequests
                                .requestMatchers(HttpMethod.PUT, "/api/v1/users/**").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/v1/users/**").authenticated()

                        // 5. Остальные запросы (личные лабы, статы и т.д.) только после логина
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Разрешаем фронтенд на Vite (стандартный порт 5173)
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));

        // Полный список методов для CRUD
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Разрешаем передачу заголовка Authorization и Content-Type
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With"));

        // Позволяет фронтенду читать заголовок Authorization, если нужно
        configuration.setExposedHeaders(List.of("Authorization"));

        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // Кешируем CORS-ответ на час

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}