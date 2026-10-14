package com.javaproject.security;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            LoggingAccessDeniedHandler accessDeniedHandler) throws Exception {

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/newUser", "/addUser",
                        "/permission-denied", "/css/**", "/js/**", "/images/**")
                    .permitAll()
                .requestMatchers("/manager/**").hasRole("MANAGER")
                .requestMatchers("/user/**", "/secured/**")
                    .hasAnyRole("USER", "MANAGER")
                .requestMatchers("/boardgameAdded")
                    .hasRole("MANAGER")
                .requestMatchers("/reviewAdded", "/deleteReview/**")
                    .hasRole("MANAGER")
                .requestMatchers("/h2-console/**").denyAll()
                .anyRequest().authenticated()
        );

        http.formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/secured", true)
                .permitAll()
        );

        http.logout(logout -> logout
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
        );

        http.exceptionHandling(ex -> ex
                .accessDeniedHandler(accessDeniedHandler)
        );

        return http.build();
    }
}
