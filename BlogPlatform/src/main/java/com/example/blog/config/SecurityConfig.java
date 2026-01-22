package com.example.blog.config;

import com.example.blog.model.User;
import com.example.blog.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain lancuchBezpieczenstwa(HttpSecurity bezpieczenstwoHttp) throws Exception {
		bezpieczenstwoHttp
				.csrf(ochrona -> ochrona.ignoringRequestMatchers("/api/**"))
				.authorizeHttpRequests(autoryzacja -> autoryzacja
						.requestMatchers(
								HttpMethod.GET,
								"/",
								"/posts",
								"/posts/*",
								"/search",
								"/css/**",
								"/api/v1/posts/**",
								"/swagger-ui/**",
								"/swagger-ui.html",
								"/v3/api-docs/**"
						).permitAll()
						.requestMatchers("/login", "/register").permitAll()
						.requestMatchers("/admin/**", "/api/v1/admin/**").hasRole("ADMIN")
						.anyRequest().authenticated()
				)
				.formLogin(formularz -> formularz
						.loginPage("/login")
						.defaultSuccessUrl("/dashboard", true)
						.permitAll()
				)
				.logout(wyjscie -> wyjscie
						.logoutUrl("/logout")
						.logoutSuccessUrl("/login?logout")
				);
		return bezpieczenstwoHttp.build();
	}

	@Bean
	public PasswordEncoder szyfratorHasel() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService dostawcaSzczegolowUzytkownika(UserRepository repozytoriumUzytkownikow) {
		return nazwaUzytkownika -> {
			User uzytkownik = repozytoriumUzytkownikow.znajdzPoNazwieUzytkownika(nazwaUzytkownika)
					.orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono uzytkownika"));
			return org.springframework.security.core.userdetails.User
					.withUsername(uzytkownik.getNazwaUzytkownika())
					.password(uzytkownik.getHasloHash())
					.roles(uzytkownik.getRola().name())
					.build();
		};
	}
}
