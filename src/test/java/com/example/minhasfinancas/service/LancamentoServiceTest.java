package com.example.minhasfinancas.service;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import java.util.Arrays;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.minhasfinancas.model.entity.Lancamento;
import com.example.minhasfinancas.model.entity.Usuario;
import com.example.minhasfinancas.model.enums.StatusLancamento;
import com.example.minhasfinancas.model.repository.LancamentoRepository;
import com.example.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.example.minhasfinancas.service.impl.LancamentoServiceImpl;
import com.example.minhasfinancas.exception.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")

public class LancamentoServiceTest {

	@SpyBean
	LancamentoServiceImpl service;
	
	@MockBean
	LancamentoRepository repository;
	
	@Test
	public void  deveSalvarUmLancamento() {
		//Cenario
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doNothing().when(service).validar(lancamentoASalvar);
		
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		
		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
		
		//Execucao
		Lancamento lancamento = service.salvar(lancamentoASalvar);
		
		
		//Verificacao
		Assertions.assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
				
		
		
	}
	
	
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		
		//Cenario
		Lancamento lancamentoASalvar =  LancamentoRepositoryTest.criarLancamento();
		
		Mockito.doThrow( RegraNegocioException.class ).when(service).validar(lancamentoASalvar);
		
		//Execução e Verificação
		Assertions.catchThrowableOfType(() -> service.salvar(lancamentoASalvar), RegraNegocioException.class);
		
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}
	
	
	@Test
	public void  deveAtualizarUmLancamento() {
		//Cenario
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doNothing().when(service).validar(lancamentoASalvar);
		
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		
		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
		
		//Execucao
		service.atualizar(lancamentoSalvo);
		
		
		//Verificacao
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
				
		
		
	}
	
	
    public void deveLancarErroAoTentarAtualizarUmLancaemntoQueAindaNaoFoiSalvo() {
		
		//Cenario
		Lancamento lancamento =  LancamentoRepositoryTest.criarLancamento();
		
		Mockito.doThrow( RegraNegocioException.class ).when(service).validar(lancamento);
		
		//Execução e Verificação
		Assertions.catchThrowableOfType(() -> service.atualizar(lancamento), NullPointerException.class);
		
		Mockito.verify(repository, Mockito.never()).save(lancamento);
	}
    
    
    
    @Test
    public void deveDeletarUmLancamento() {
    	//Cenario
    	Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
    	lancamento.setId(1l);
    	
    	//execucao
    	service.deletar(lancamento);
    
    	//Verificacao
    	Mockito.verify(repository).delete(lancamento);
    	
    }
    
    
    @Test
    public void deveLancarErroAoDeletarUmLancamentoQueAindaNaoFoiSalvo() {
    	
    	//Cenario
    	Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
    	
    	
    	
    	
    	//Execução
    	Assertions.catchThrowableOfType(() ->service.deletar(lancamento), NullPointerException.class);
    	
    	
    	//Verificacao
    	Mockito.verify(repository, Mockito.never()).delete(lancamento);
    	
    	
    }
    
    
    @Test
    public void deveFiltrarLancamentos() {
    	
    	//Cenario
    	Lancamento lancamento  = LancamentoRepositoryTest.criarLancamento();
    	lancamento.setId(1l);
    	
    	List<Lancamento> lista = Arrays.asList(lancamento);
    	Mockito.when( repository.findAll(Mockito.any(Example.class)) ).thenReturn(lista);
    	
    	
    	//Execução
    	List<Lancamento> resultado = service.buscar(lancamento);
    	
    	
    	//Verificacao
    	Assertions
    	        .assertThat(resultado)
    	        .isNotEmpty()
    	        .hasSize(1)
    	        .contains(lancamento);
    	
    	
    }
    
    
    @Test
    public void deveAtualizarOStatusDeUmLancamento() {
    	
    	//Cenario
    	Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
    	lancamento.setId(1l);
    	lancamento.setStatus(StatusLancamento.PENDENTE);
    	
    	StatusLancamento novoStatus  = StatusLancamento.EFETIVADO;
    	Mockito.doReturn(lancamento).when(service).atualizar(lancamento);
    	
    	
    	//Execucao
    	service.atualizarStatus(lancamento, novoStatus);
    	
    	
    	//Verificacoes
    	Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
    	Mockito.verify(service).atualizar(lancamento);
    	
    	
    	
    	
    	
    }
    
    
    
    @Test
    public void deveObterUmLancamentoPorID() {
    	
    	//Cenario
    	Long id = 1l;
    	Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
    	lancamento.setId(id);
    	
    	Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));
    	
    	//execucao
    	Optional<Lancamento> resultado = service.obterPorId(id);
    	
    	
    	//Verificacoes
    	Assertions.assertThat(resultado.isPresent()).isTrue();
    	
    }
    
    @Test
    public void deveRetornarVazioQuandoOLancamentoNaoExiste() {
    	
    	//Cenario
    	Long id = 1l;
    	Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
    	lancamento.setId(id);
    	
    	Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
    	
    	//execucao
    	Optional<Lancamento> resultado = service.obterPorId(id);
    	
    	
    	//Verificacoes
    	Assertions.assertThat(resultado.isPresent()).isFalse();
    	
    }
    
    
    @Test
    public void deveLancarErrosAoValidarUmLancamento() {
    	
    	Lancamento lancamento = new Lancamento();
    	
    	Throwable erro = Assertions.catchThrowable(() -> service.validar(lancamento));
    	Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descricao valida");
    	
    	lancamento.setDescricao("");
    	
    	erro = Assertions.catchThrowable(() -> service.validar(lancamento));
    	Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descricao valida");
    	
    	
    	lancamento.setDescricao("Salario");
    	
    	erro = Assertions.catchThrowable(() -> service.validar(lancamento));
    	Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês valido");
    	
    	lancamento.setMes(0);
    	
    	erro = Assertions.catchThrowable(() -> service.validar(lancamento));
    	Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês valido");
    	
    	lancamento.setMes(13);
    	
    	erro = Assertions.catchThrowable(() -> service.validar(lancamento));
    	Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês valido");
    	
    	lancamento.setMes(1);
    	
    	erro = Assertions.catchThrowable(() -> service.validar(lancamento));
    	Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano valido");
    	
    	
    	lancamento.setAno(130);
    	
    	erro = Assertions.catchThrowable(() -> service.validar(lancamento));
    	Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano valido");
    	
    	
    	lancamento.setAno(2020);
    	
    	erro = Assertions.catchThrowable(() -> service.validar(lancamento));
    	Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuario");
    	
    	
    	lancamento.setUsuario(new Usuario());
    	
    	erro = Assertions.catchThrowable(() -> service.validar(lancamento));
    	Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuario");
    	
    	
    	lancamento.getUsuario().setId(1l);
    	
    	
    	erro = Assertions.catchThrowable(() -> service.validar(lancamento));
    	Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor Valido");
    	
    	
    	lancamento.setValor(BigDecimal.ZERO);
    	
    	erro = Assertions.catchThrowable(() -> service.validar(lancamento));
    	Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor Valido");
    	
    	
    	lancamento.setValor(BigDecimal.valueOf(1));
    	
    	
    	erro = Assertions.catchThrowable(() -> service.validar(lancamento));
    	Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Tipo de Lancamento");
    	
    	
    	
    }
    
    
	
	
	
}
