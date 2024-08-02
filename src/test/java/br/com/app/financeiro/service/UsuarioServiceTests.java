package br.com.app.financeiro.service;

import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.app.financeiro.dao.UsuarioDao;
import br.com.app.financeiro.exceptions.FinanceiroException;
import br.com.app.financeiro.model.Usuario;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTests {

    @Mock
    private UsuarioDao usuarioDao;

    @InjectMocks
    @Autowired
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Adicionar um novo usuário com sucesso")
    public void adicionarUsuarioCase1(){
        Usuario usuario = new Usuario();
        usuario.setNome("Teste");
        usuario.setEmail("test@gmail.com");
        usuario.setSenha("12345678");
        usuario.setSaldo(BigDecimal.ZERO);

        Mockito.when(usuarioDao.adicionarUsuario(usuario)).thenReturn(1L);

        usuarioService.adicionarUsuario(usuario);

        Mockito.verify(usuarioDao, Mockito.times(1)).adicionarUsuario(usuario);
    }

    @Test
    @DisplayName("Erro ao adicionar um novo usuário pela formatação do email")
    public void adicionarUsuarioCase2(){
        Usuario usuario = new Usuario();
        usuario.setNome("Teste");
        usuario.setEmail("test.com");
        usuario.setSenha("12345678");
        usuario.setSaldo(BigDecimal.ZERO);

        Mockito.when(usuarioDao.adicionarUsuario(usuario)).thenReturn(null);

        Exception e = Assertions.assertThrows(FinanceiroException.class, () ->{
            usuarioService.adicionarUsuario(usuario);
        });

        Assertions.assertEquals("Email inválido", e.getMessage());

        Mockito.verify(usuarioDao, Mockito.times(0)).adicionarUsuario(usuario);
    }

    @Test
    @DisplayName("Erro ao adicionar um novo usuário pela formatação da senha")
    public void adicionarUsuarioCase3(){
        Usuario usuario = new Usuario();
        usuario.setNome("Teste");
        usuario.setEmail("test@gmail.com");
        usuario.setSenha("12345");
        usuario.setSaldo(BigDecimal.ZERO);

        Mockito.when(usuarioDao.adicionarUsuario(usuario)).thenReturn(null);

        Exception e = Assertions.assertThrows(FinanceiroException.class, () ->{
            usuarioService.adicionarUsuario(usuario);
        });

        Assertions.assertEquals("Senha inválida", e.getMessage());

        Mockito.verify(usuarioDao, Mockito.times(0)).adicionarUsuario(usuario);
    }

    @Test
    @DisplayName("Sucesso ao adicionar Saldo")
    public void adicionarSaldoCase1(){
        BigDecimal novoSaldo = new BigDecimal(100.0);
        Long usuarioId = 1L;

        Mockito.when(usuarioDao.adicionarSaldo(usuarioId, novoSaldo)).thenReturn(true);

        usuarioService.adicionarSaldo(usuarioId, novoSaldo);

        Mockito.verify(usuarioDao, Mockito.times(1)).adicionarSaldo(usuarioId, novoSaldo);
    }

    @Test
    @DisplayName("Erro ao adicionar Saldo colocando letras no lugar do valor")
    public void adicionarSaldoCase2(){
        BigDecimal novoSaldo = new BigDecimal(0);
        Long usuarioId = 1L;

        Mockito.when(usuarioDao.adicionarSaldo(usuarioId, novoSaldo)).thenReturn(false);

        Exception e = Assertions.assertThrows(FinanceiroException.class, () ->{
            usuarioService.adicionarSaldo(usuarioId, novoSaldo);
        });

        Assertions.assertEquals("Dados inválidos", e.getMessage());

        Mockito.verify(usuarioDao, Mockito.times(0)).adicionarSaldo(usuarioId, novoSaldo);
    }

    @Test
    @DisplayName("Mostra as informações do usuario com sucesso")
    public void informacoesUsuariosCase1(){
        Long usuarioId = 1L;

        Mockito.when(usuarioDao.procurarUsuarioPorId(usuarioId)).thenReturn(new Usuario());

        usuarioService.retornarInformacoes(usuarioId);

        Mockito.verify(usuarioDao, Mockito.times(1)).procurarUsuarioPorId(usuarioId);
    }

    @Test
    @DisplayName("Erro ao mostrar pelo id inexistente")
    public void informacoesUsuariosCase2(){
        Long usuarioId = null;

        Mockito.when(usuarioDao.procurarUsuarioPorId(usuarioId)).thenReturn(null);

        Exception e = Assertions.assertThrows(FinanceiroException.class, () ->{
            usuarioService.retornarInformacoes(usuarioId);
        });

        Assertions.assertEquals("Conta não encontrada", e.getMessage());

        Mockito.verify(usuarioDao, Mockito.times(0)).procurarUsuarioPorId(usuarioId);
    }

    @Test
    @DisplayName("Sucesso ao atualizar o usuario")
    public void atualizarUsuarioCase1(){
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Teste2");
        usuario.setEmail("test2@gmail.com");
        usuario.setSenha("12345677");
        usuario.setSaldo(BigDecimal.ZERO);

        Mockito.when(usuarioDao.atualizarInformacoes(usuario)).thenReturn(true);

        usuarioService.atualizarInformacoes(usuario);

        Mockito.verify(usuarioDao, Mockito.times(1)).atualizarInformacoes(usuario);
    }

    @Test
    @DisplayName("Erro ao atualizar o usuario pelos dados inválidos")
    public void atualizarUsuarioCase2(){
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Teste2");
        usuario.setEmail("test2.com");
        usuario.setSenha("1234567");
        usuario.setSaldo(BigDecimal.ZERO);

        Mockito.when(usuarioDao.atualizarInformacoes(usuario)).thenReturn(false);

        Exception e = Assertions.assertThrows(FinanceiroException.class, () ->{
            usuarioService.atualizarInformacoes(usuario);
        });

        Assertions.assertEquals("Dados inválidos", e.getMessage());

        Mockito.verify(usuarioDao, Mockito.times(0)).atualizarInformacoes(usuario);
    }

}
