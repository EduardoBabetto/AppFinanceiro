package br.com.app.financeiro.service;

import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.app.financeiro.dao.UsuarioDao;
import br.com.app.financeiro.err.exceptions.FinanceiroException;
import br.com.app.financeiro.model.Usuario;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTests {

    @Mock
    private UsuarioDao usuarioDao;

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @DisplayName("Usuario com os dados corretos")
    public Usuario usuarioCorreto(){
        Usuario usuario = new Usuario();
        usuario.setNome("Teste");
        usuario.setEmail("test@gmail.com");
        usuario.setSenha("12345678");
        usuario.setSaldo(BigDecimal.ZERO);
        return usuario;
    }

    @DisplayName("Usuario com email incorreto")
    public Usuario usuarioEmailIncorreto(){
        Usuario usuario = new Usuario();
        usuario.setNome("Teste");
        usuario.setEmail("test.com");
        usuario.setSenha("12345678");
        usuario.setSaldo(BigDecimal.ZERO);
        return usuario;
    }

    @DisplayName("Usuario com senha incorreta")
    public Usuario usuarioSenhaIncorreto(){
        Usuario usuario = new Usuario();
        usuario.setNome("Teste");
        usuario.setEmail("test@gmail.com");
        usuario.setSenha("12345");
        usuario.setSaldo(BigDecimal.ZERO);
        return usuario;
    }

    @Test
    @DisplayName("Adicionar um novo usuário com sucesso")
    public void adicionarUsuarioCase1(){
        Usuario usuario = usuarioCorreto();

        Mockito.when(usuarioDao.adicionarUsuario(Mockito.any(Usuario.class))).thenReturn(1L);
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("12345678");
        
        usuarioService.adicionarUsuario(usuario);

        Mockito.verify(usuarioDao, Mockito.times(1)).adicionarUsuario(Mockito.any(Usuario.class));
        Mockito.verify(passwordEncoder, Mockito.times(1)).encode(Mockito.anyString());
    }

    @Test
    @DisplayName("Erro ao adicionar um novo usuário pela formatação do email")
    public void adicionarUsuarioCase2(){
       Usuario usuario = usuarioEmailIncorreto();

        Exception e = Assertions.assertThrows(FinanceiroException.class, () ->{
            usuarioService.adicionarUsuario(usuario);
        });

        Assertions.assertEquals("Email inválido.", e.getMessage());

        Mockito.verify(usuarioDao, Mockito.times(0)).adicionarUsuario(usuario);
    }

    @Test
    @DisplayName("Erro ao adicionar um novo usuário pela formatação da senha")
    public void adicionarUsuarioCase3(){
       Usuario usuario = usuarioSenhaIncorreto();

        Exception e = Assertions.assertThrows(FinanceiroException.class, () ->{
            usuarioService.adicionarUsuario(usuario);
        });

        Assertions.assertEquals("Senha deve ter pelo menos 8 caracteres.", e.getMessage());

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
    @DisplayName("Erro ao adicionar Saldo não achando a conta")
    public void adicionarSaldoCase2(){
        BigDecimal novoSaldo = new BigDecimal(100.0);
        Long usuarioId = 1L;

        Exception e = Assertions.assertThrows(FinanceiroException.class, () ->{
            usuarioService.adicionarSaldo(usuarioId, novoSaldo);
        });

        Assertions.assertEquals("Conta não encontrada!", e.getMessage());

        Mockito.verify(usuarioDao, Mockito.times(1)).adicionarSaldo(usuarioId, novoSaldo);
    }

    @Test
    @DisplayName("Mostra as informações do usuario com sucesso")
    public void informacoesUsuariosCase1(){
        Long usuarioId = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setNome("Teste");

        Mockito.when(usuarioDao.procurarUsuarioPorId(usuarioId)).thenReturn(usuario);

        Usuario resultado = usuarioService.retornarInformacoes(usuarioId);

        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(usuarioId, resultado.getId());
        Assertions.assertEquals("Teste", resultado.getNome());

        Mockito.verify(usuarioDao, Mockito.times(1)).procurarUsuarioPorId(usuarioId);
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

        Mockito.when(usuarioDao.atualizarInformacoes(Mockito.any(Usuario.class))).thenReturn(true);
        Mockito.when(usuarioDao.procurarUsuarioPorId(Mockito.anyLong())).thenReturn(usuario);

        usuarioService.atualizarInformacoes(usuario);

        Mockito.verify(usuarioDao, Mockito.times(1)).atualizarInformacoes(Mockito.any(Usuario.class));
        Mockito.verify(usuarioDao, Mockito.times(1)).procurarUsuarioPorId(Mockito.anyLong());
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

        Exception e = Assertions.assertThrows(FinanceiroException.class, () ->{
            usuarioService.atualizarInformacoes(usuario);
        });

        Assertions.assertEquals("Conta não encontrada!", e.getMessage());

        Mockito.verify(usuarioDao, Mockito.times(0)).atualizarInformacoes(Mockito.any(Usuario.class));
    }
}
