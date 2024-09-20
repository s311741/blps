package blps.supplies;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collections;

@EnableKafka
@SpringBootApplication
@ImportResource("classpath:/spring-security.xml")
public class SuppliesApplication {
  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(SuppliesApplication.class);
    app.setDefaultProperties(Collections.singletonMap("server.port", "8090"));
    app.run(args);
  }

  @Bean
  public SecurityFilterChain security(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(requests -> {
          requests.requestMatchers("/part/**").hasRole("SUPPLIER");
          requests.requestMatchers("/error/**").permitAll();
          requests.anyRequest().hasRole("SELLER");
        })
        .httpBasic(Customizer.withDefaults())
        .csrf(AbstractHttpConfigurer::disable)
    ;
    return http.build();
  }
}