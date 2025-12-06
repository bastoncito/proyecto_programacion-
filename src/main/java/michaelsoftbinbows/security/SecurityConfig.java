package michaelsoftbinbows.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de Spring Security. Refuerza el flujo correcto de la página, evitando que un
 * usuario pueda acceder a pantallas sin autenticarse.
 */
@Configuration
public class SecurityConfig {
  /**
   * Configuración de seguridad para login y logout Obliga al usuario a seguir el flujo de pantallas
   * que deseamos.
   *
   * @param http petición http
   * @return objeto http o null
   * @throws Exception si es que hay un error al construir http
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.ignoringRequestMatchers("/api/**")) // temporal para probar postman
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        "/", "/styles/**", "/images/**", "/js/**", "/uploads/**", "/login", "/register", "/api/**")
                    .permitAll() // public endpoints
                    .requestMatchers("/admin/**")
                    .hasAnyAuthority("ROLE_ADMIN", "ROLE_MODERADOR")
                    .anyRequest()
                    .authenticated() // everything else requires login
            )
        .formLogin(
            form ->
                form.loginPage("/login") // custom login page (you need to create it)
                    .failureUrl("/login?error")
                    .loginProcessingUrl("/login")
                    .usernameParameter("usuario")
                    .passwordParameter("contrasena")
                    .successHandler(
                        (request, response, authentication) -> {
                          CustomUserDetails userDetails =
                              (CustomUserDetails) authentication.getPrincipal();
                          if (userDetails.getAuthorities().stream()
                              .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                            response.sendRedirect("/admin");
                          } else if (userDetails.getAuthorities().stream()
                              .anyMatch(a -> a.getAuthority().equals("ROLE_MODERADOR"))) {
                            response.sendRedirect("/admin"); // or moderator panel
                          } else {
                            response.sendRedirect("/home");
                          }
                        })
                    .permitAll())
        .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout").permitAll())
        .exceptionHandling(ex -> ex.accessDeniedPage("/acceso-denegado"));

    return http.build();
  }

  /**
   * Método que devuleve un encriptador de contraseñas BCrypt.
   *
   * @return objeto BCryptPasswordEncoder
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
