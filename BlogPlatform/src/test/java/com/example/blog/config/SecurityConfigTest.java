package com.example.blog.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.example.blog.model.Role;
import com.example.blog.model.User;
import com.example.blog.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

	@Mock
	private UserRepository repozytoriumUzytkownikow;

	@Test
	void powinienBudowacUserDetailsDlaIstniejacegoUzytkownika() {
		User uzytkownik = new User();
		uzytkownik.setNazwaUzytkownika("admin");
		uzytkownik.setHasloHash("hash");
		uzytkownik.setRola(Role.ADMIN);
		when(repozytoriumUzytkownikow.znajdzPoNazwieUzytkownika("admin"))
				.thenReturn(Optional.of(uzytkownik));

		UserDetailsService serwis = new SecurityConfig().dostawcaSzczegolowUzytkownika(repozytoriumUzytkownikow);
		UserDetails details = serwis.loadUserByUsername("admin");

		assertThat(details.getUsername()).isEqualTo("admin");
		assertThat(details.getPassword()).isEqualTo("hash");
		assertThat(details.getAuthorities())
				.extracting(GrantedAuthority::getAuthority)
				.contains("ROLE_ADMIN");
	}

	@Test
	void powinienRzucacWyjatekGdyBrakUzytkownika() {
		when(repozytoriumUzytkownikow.znajdzPoNazwieUzytkownika("brak"))
				.thenReturn(Optional.empty());

		UserDetailsService serwis = new SecurityConfig().dostawcaSzczegolowUzytkownika(repozytoriumUzytkownikow);

		assertThatThrownBy(() -> serwis.loadUserByUsername("brak"))
				.isInstanceOf(UsernameNotFoundException.class);
	}
}
