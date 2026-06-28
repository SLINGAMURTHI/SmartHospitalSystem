package com.hospital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@SpringBootApplication
@EnableCaching
public class HospitalApplication {

	public static void main(String[] args) {
		SpringApplication.run(HospitalApplication.class, args);
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
        .csrf(csrf -> csrf.disable())
        .cors(cors -> cors.configure(http)) 
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
        .httpBasic(org.springframework.security.config.Customizer.withDefaults());
    
    return http.build(); // 🌟 ADD THIS EXACT LINE HERE!
	}
}