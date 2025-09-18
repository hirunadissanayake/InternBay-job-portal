// Updated WebSecurityConfig.java - Remove passwordEncoder bean
package lk.ijse.gdse.main.internbaybackend.config;

import lk.ijse.gdse.main.internbaybackend.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    private final UserService userService;
    private final JwtFilter jwtFilter;
    private final PasswordEncoder passwordEncoder;

    public WebSecurityConfig(UserService userService, JwtFilter jwtFilter, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtFilter = jwtFilter;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // Public authentication endpoints
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/api/v1/user/register",
                                "/api/v1/user/login"
                        ).permitAll()

                        // Public category endpoints
                        .requestMatchers("/api/v1/category/getAll").permitAll()

                        // Public job search and view endpoints
                        .requestMatchers(HttpMethod.GET, "/api/v1/jobs/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/jobs/{jobId}").permitAll()

                        // Protected job management endpoints (require authentication)
                        .requestMatchers(HttpMethod.POST, "/api/v1/jobs").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/jobs/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/jobs/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/jobs/**").authenticated()
                        .requestMatchers("/api/v1/jobs/employer/**").authenticated()

                        // Protected profile and application endpoints
                        .requestMatchers("/api/v1/employer/**").authenticated()
                        .requestMatchers("/api/v1/candidate/**").authenticated()
                        .requestMatchers("/api/v1/applications/**").authenticated()

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}