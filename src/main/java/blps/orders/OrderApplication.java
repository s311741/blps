package blps.orders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collections;

@EnableKafka
@EnableJpaRepositories
@SpringBootApplication
@ImportResource("classpath:/spring-security.xml")
public class OrderApplication {
  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(OrderApplication.class);
    app.setDefaultProperties(Collections.singletonMap("server.port", "8080"));
    app.run(args);
  }

  @Bean
  public SecurityFilterChain security(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(requests -> {
          requests.requestMatchers("/order/testKafka").permitAll();
          requests.requestMatchers("/order/**").hasRole("CUSTOMER");
          requests.requestMatchers("/error/**").permitAll();
          requests.anyRequest().hasRole("SELLER");
        })
        .httpBasic(Customizer.withDefaults())
        .csrf(AbstractHttpConfigurer::disable)
    ;
    return http.build();
  }
}