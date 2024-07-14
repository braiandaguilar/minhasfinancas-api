package com.example.minhasfinancas.service;

import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.minhasfinancas.exception.ErroAutenticacao;
import com.example.minhasfinancas.exception.RegraNegocioException;
import com.example.minhasfinancas.model.entity.Usuario;
import com.example.minhasfinancas.model.repository.UsuarioRepository;
import com.example.minhasfinancas.service.impl.UsuarioServiceImpl;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

    @SpyBean
	UsuarioServiceImpl service;
	
	@MockBean
    UsuarioRepository repository;
	
	
	
	
	@Test
	public void deveSalvarUmUsuario() {
		
		
		//Cenario
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		Usuario usuario = Usuario.builder()
				          .id(1l)
				          .nome("nome")
				          .email("email@email.com")
				          .senha("senha")
				          .build();
		
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		
		//acao
		Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
		
		
		//Verificação
		Assertions.assertThat(usuarioSalvo).isNotNull();
		Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1l);
		Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
		Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@email.com");
		Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
		
		
	}
	
	
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		
		//Cenario
		String email = "email@email.com";
		Usuario usuario = Usuario.builder().email(email).build();
		Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);
		
		
		//Cenario
		service.salvarUsuario(usuario);
		
		
		//Verificacao
		Mockito.verify(repository, Mockito.never()).save(usuario);
		
		
		
	}
	
	
	
	
	
	@Test(expected = Test.None.class)
	public void deveAutenticarUmUsuarioComSucesso() {
		//cenario
		String email = "email@email.com";
		String senha = "senha";
		
		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
		Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
		
		
		//acao
		Usuario result = service.autenticar(email, senha);
		
		
		//verificacao
		Assertions.assertThat(result).isNotNull();
			
		
	}
	
	
	@Test
	public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComEmailInformado() {
		
		//Cenario
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		
		//acao
		Throwable exception = Assertions.catchThrowable(() -> service.autenticar("email@email.com", "senha"));
		
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Usuario não encontrado");
		
	}
	
	@Test
	public void deveLancarErroQuandoSenhaNaoBater() {
		
		//cenario
	    String email = "email@email.com";
		String senha = "senha";
				
	    Usuario usuario = Usuario.builder().email(email).senha(Mockito.anyString()).id(1l).build();
		Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
				
				
		//acao
		
		Throwable exception = Assertions.catchThrowable( () -> service.autenticar(email, senha));
		
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Senha Invalida");
				
		
		
	}
	
	
	
	
	
	
	
	
	@Test(expected = Test.None.class)
	public void deveValidarEmail() {
		
		//Cenario
		
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
		
		
		//Acao
		service.validarEmail("email@email.com");
		
		
		
		
		//Cenario
		
	}
	
	@Test(expected = RegraNegocioException.class)
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
		
		//Cenario
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);	
				
		//Acao
		service.validarEmail("email@email.com");
				
				
				
				
		//Cenario
		
		
	}
	
	
	
	
}
