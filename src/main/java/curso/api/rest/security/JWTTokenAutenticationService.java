package curso.api.rest.security;

import java.io.IOException;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import curso.api.rest.ApplicationContextLoad;
import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Component
public class JWTTokenAutenticationService {
   
	/*Tempo de expiração do token 2 dias*/
	private static final long EXPIRATION_TIME = 172800000;
	
	/*Uma senha unica para compor a autenticacao e ajudar na segurança*/
	private static final String SECRET = "senhaExtremamenteSecreta";
	
	/*Prefixo padrão de Token*/
	private static final String TOKEN_PREFIX = "Bearer";
	
	private static final String HEADER_STRING = "Authorization";
	
	/*Gerando token de autenticacao e adicionando ao cabeçalho e resposta Http*/
	public void addAuthentication(HttpServletResponse response, String username) throws IOException {
		
		String JWT = Jwts.builder() /*Chama o gerador de token*/
				         .setSubject(username) /*Adiciona o usuario que esta fazendo o login*/
				         .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) /*Tempo de expiração*/
				         .signWith(SignatureAlgorithm.HS512, SECRET).compact(); /*Compactação e algoritmos de geração de senha*/
		
		String token = TOKEN_PREFIX + " " + JWT; 
		
		/*Adiciona no cabeçalho http*/
		response.addHeader(HEADER_STRING, token);
		
		/*Escreve token como resposta no corpo http*/
		response.getWriter().write("{\"Authorization\": \""+token+"\"}");
	}
		
	/* Retorna o usuário validao com token ou caso não seja valido retorna null */
	public Authentication getAuthentication(HttpServletRequest request) {

		/* Pega o token enviado no cabeçalho do http */
		String token = request.getHeader(HEADER_STRING);

		if (token != null) {
			
			String tokenLimpo = token.replace(TOKEN_PREFIX, "");
			/* Faz a validação do token do usuário na requisição */
			String user = Jwts.parser().setSigningKey(SECRET)
					.parseClaimsJws(tokenLimpo)
					.getBody().getSubject();

			if (user != null) {
               Usuario usuario = ApplicationContextLoad.getApplicationContext()
                        		   .getBean(UsuarioRepository.class).findByLogin(user);
               
               if (usuario != null) {
            	   
            	   if (tokenLimpo.equalsIgnoreCase(usuario.getToken())) {
             	   return new UsernamePasswordAuthenticationToken(
            			              usuario.getLogin(),
            			              usuario.getSenha(),
            			              usuario.getAuthorities());
               }
               }
			} 
		}
		return null; /* Não autorizado */		
	}	
}
