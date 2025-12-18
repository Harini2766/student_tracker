package security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtFilter jwtFilter;

    // Password Encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Authentication Manager
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Security Filter Chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // 1. Disable CSRF (Standard for APIs)
            .csrf(csrf -> csrf.disable())

            // 2. Disable default login forms
            .httpBasic(httpBasic -> httpBasic.disable())
            .formLogin(formLogin -> formLogin.disable())

            // 3. Authorization rules
            .authorizeHttpRequests(requests -> requests

                // *** CRITICAL FIX: Resume download access ***
                .requestMatchers("/api/resume/download/**")
                    .hasAnyAuthority("STUDENT", "STAFF")

                // A. Public access
                .requestMatchers("/", "/index.html", "/auth/**").permitAll()
                .requestMatchers("/assets/**", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/student/**", "/staff/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()

                // *** Temporary sync endpoints ***
                .requestMatchers("/api/staff/trigger-refresh").permitAll()
                .requestMatchers("/api/student/refresh-all").permitAll()

                .requestMatchers("/api/staff/dashboard/metrics").hasAuthority("STAFF") // <--- ADD THIS LINE
                
                // B. Student APIs
                .requestMatchers("/api/student/**").hasAuthority("STUDENT")

                // C. HackerRank APIs
                .requestMatchers("/api/data/hackerrank/**")
                    .hasAnyAuthority("STUDENT", "STAFF")

                // D. Certificates
                .requestMatchers(
                        "/api/certificates/all",
                        "/api/certificates/verify/**",
                        "/api/certificates/reject/**"
                ).hasAuthority("STAFF")

                .requestMatchers("/api/certificates/download/**")
                    .hasAnyAuthority("STUDENT", "STAFF")

                // E. Staff APIs
                .requestMatchers("/api/staff/**").hasAuthority("STAFF")

                // F. Catch-all
                .anyRequest().authenticated()
            )

            // 4. Stateless session
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        // 5. JWT Filter
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
