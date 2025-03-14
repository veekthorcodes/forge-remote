package com.onpointserv.forge_remote;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
class ForgeConfig {
	@Bean
	ForgeFilter tokenFilter(FitValidator tokenValidator) {
		return new ForgeFilter(tokenValidator);
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, ForgeFilter tokenFilter) throws Exception {
		return http
				.csrf(csrf -> csrf.disable())
				.httpBasic(Customizer.withDefaults())
				.authorizeHttpRequests(
						auth -> auth.requestMatchers("/", "/forge/**").permitAll()
								.anyRequest().authenticated())
				.addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}
}
