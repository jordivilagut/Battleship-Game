package salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
class GlobalAuthenticationService extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	private PlayerRepository players;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService());
	}

	@Bean
    UserDetailsService userDetailsService() {
		return new UserDetailsService() {

			@Override
			public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
				Player player = players.findByUsername(username);
				if(player != null) {
					return new User(player.getUsername(), player.getPassword(), true, true, true, true, AuthorityUtils.createAuthorityList("USER"));
				} else {
					throw new UsernameNotFoundException("Could not find the user: "	+ username + ".");
				}
			}
		};
	}
}
