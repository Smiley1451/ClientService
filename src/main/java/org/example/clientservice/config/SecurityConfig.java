package org.example.clientservice.config;



import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchange -> exchange
                        // Allow health checks


                        // Secure all other API endpoints
                        .pathMatchers("/api/clients/**").permitAll()


                )
                .oauth2ResourceServer(oauth2 -> oauth2
                               .jwt()
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable); // Disable CSRF for stateless API

        return http.build();
    }
}