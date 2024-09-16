package blps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@SpringBootApplication
public class LabApplication {
  public static void main(String[] args) {
    SpringApplication.run(LabApplication.class, args);
  }

  @Bean
  public SecurityFilterChain security(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(requests -> {
          requests.requestMatchers("/part/**").hasRole("SUPPLIER");
          requests.requestMatchers("/order/**").hasRole("CUSTOMER");
          requests.anyRequest().hasRole("SELLER");
        })
        .httpBasic(Customizer.withDefaults())
        .csrf(AbstractHttpConfigurer::disable)
    ;
    return http.build();
  }

  UserSetupComponent userSetup;

  public LabApplication(UserSetupComponent userSetup) {
    this.userSetup = userSetup;
  }

  @Bean
  public UserDetailsService userDetailsService() {
    return new InMemoryUserDetailsManager(userSetup.getAllUserDetails());
  }
}