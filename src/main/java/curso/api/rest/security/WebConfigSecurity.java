package curso.api.rest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import curso.api.rest.service.ImplementacaoUserDetailService;

/*Mapeia URL, enderecos, autoriza ou bloqueia acesso a URL*/


@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private ImplementacaoUserDetailService implementacaoUserDetailService;

	/*Configura as solicitações de acesso por Http*/
	@Override
	protected void configure(HttpSecurity http) throws Exception {
	   /*Ativando a proteção contra usuário que não estão validados por token*/
		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
		
		/*Ativando a permissão para acesso a página inicial do sistema*/
		.disable().authorizeRequests().antMatchers("/").permitAll()
		.antMatchers("/index").permitAll()
		
		/*URL de logout - Redireciona após o user deslogar do sistema*/
		.anyRequest().authenticated().and().logout().logoutSuccessUrl("/index")
		
		/*Mapeia URL de Logout e invalida o usuario*/
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
		
		/*Filtra requisições de login para autenticação*/
		.and().addFilterBefore(new JWTLoginFilter("/login", authenticationManager()),
				                                  UsernamePasswordAuthenticationFilter.class)
		
		/*Filtra demais requisições para verificar a presença do TOKEN JWT no HEADER HTTP*/
		.addFilterBefore(new JwtApiAutenticationFilter(), UsernamePasswordAuthenticationFilter.class);			
	}	
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	    /*Service que irá consultar no banco de dados*/
		auth.userDetailsService(implementacaoUserDetailService)
		/*Padrão de codificacao de senha de usuario*/
		.passwordEncoder(new BCryptPasswordEncoder());		
	}
}