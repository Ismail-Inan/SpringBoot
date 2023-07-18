package com.meineAngebote.security.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

  private final AuthenticationProvider authenticationProvider;
  private final LogoutHandler logoutHandler;
  private final JwtAuthenticationFilter jwtAuthFilter;
  private final AuthenticationEntryPoint authenticationEntryPoint;

  public SecurityConfiguration(AuthenticationProvider authenticationProvider,
      LogoutHandler logoutHandler,
      JwtAuthenticationFilter jwtAuthenticationFilter,
      @Qualifier("delegatedAuthEntryPoint") AuthenticationEntryPoint authenticationEntryPoint) {
    this.authenticationProvider = authenticationProvider;
    this.jwtAuthFilter = jwtAuthenticationFilter;
    this.authenticationEntryPoint = authenticationEntryPoint;
    this.logoutHandler = logoutHandler;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf().disable()
        .cors(Customizer.withDefaults())
        .authorizeHttpRequests()
        .requestMatchers(
            "/api/v1/auth/**",
            "/api/v1/registration/**",
            "/company-item/public/**",
            "/company-item-image/public/**",
            "/item-statistic/public/**",
            "/company/public/**"
        )
        .permitAll()

        .anyRequest()
        .authenticated()

        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        .and()
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

        .logout()
        .logoutUrl("/api/v1/auth/logout")
        .addLogoutHandler(logoutHandler)
        .logoutSuccessHandler(
            (request, response, authentication) -> SecurityContextHolder.clearContext())
                /*.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)*/
    ;

    return http.build();
  }

}