package curso.api.rest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;



@RestController
@RequestMapping(value = "/usuario")
public class IndexController {
    
	@Autowired
	private UsuarioRepository usuRepository;
	
	
	List<Usuario> usuarios = new ArrayList<Usuario>();
	
//	@GetMapping(value = "/", produces = "application/json")
//	public ResponseEntity init() {
//		return new ResponseEntity("Olá spring boot", HttpStatus.OK);
//	}
	
	
//	@GetMapping(value = "/", produces = "application/json")
//	public ResponseEntity init(@RequestParam (value = "nome", required = true, defaultValue = "Nome é obrigatório") String nome, @RequestParam(value = "salario")float salario) {
//		
//		System.out.println("Nome :"+nome);
//		return new ResponseEntity("Olá spring boot, Seu nome é :"+nome+" Salario é de:"+salario, HttpStatus.OK);
//	}
//	@GetMapping(value = "/", produces = "application/json")
//	public ResponseEntity<Usuario> init() {
//		
//		Usuario usuario = new Usuario();
//		
//		usuario.setId(10L);
//		usuario.setNome("Daniel");
//		usuario.setSenha("123");
//		usuario.setLogin("del");
//		
//        Usuario usuario2 = new Usuario();
//		
//		usuario2.setId(10L);
//		usuario2.setNome("Vitor");
//		usuario2.setSenha("432");
//		usuario2.setLogin("vi");
//		
//		
//		usuarios.add(usuario);
//		usuarios.add(usuario2);
//		
//		
//		return new  ResponseEntity(usuarios, HttpStatus.OK);
//		
//	}
	
	
	
	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Usuario> init(@PathVariable(value = "id") Long id) {
		
		Optional<Usuario> usuario = usuRepository.findById(id);
		
		return  new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}
	
	/*Vamos supor que o carregamento de usuário seja um processo lento
	 * e queremos controlar ele com cache para agilizar o processo*/
	@GetMapping(value = "/", produces = "application/json")
	@Cacheable("cacheUsuarios")
	public ResponseEntity<List<Usuario>> usuarios() throws InterruptedException {
		
		 List<Usuario> list = (List<Usuario>) usuRepository.findAll();
		
		 Thread.sleep(6000); /*Segura o codigo por 6 segundos*/
         
		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}
	
	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario){		
		
		if (!usuario.getTelefones().isEmpty()) {			
			usuario.getTelefones().forEach(t -> {
				t.setUsuario(usuario);
			});
		}
		String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhaCriptografada);
		Usuario usuarioSalvo = usuRepository.save(usuario);	
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);	
	}
	
	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> alterar(@RequestBody Usuario usuario){
		
		if (!usuario.getTelefones().isEmpty()) {			
			usuario.getTelefones().forEach(t -> {
				t.setUsuario(usuario);
			});
		}
		Usuario usuarioTemp = usuRepository.findByLogin(usuario.getLogin());
		if (!usuarioTemp.getSenha().equals(usuario.getSenha())) {
			String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhaCriptografada);			
		}
		
		
		
		Usuario usuarioSalvo = usuRepository.save(usuario);
		System.out.println("Atualizei");
		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);	
	}
	
	@DeleteMapping(value = "/{id}", produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
	public boolean delete(@PathVariable("id") Long id) {
		
		usuRepository.deleteById(id);
	
		return true;		
	}
	
}










