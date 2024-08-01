package br.com.app.financeiro.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import br.com.app.financeiro.conexao.Conexao;
import br.com.app.financeiro.exceptions.FinanceiroException;
import br.com.app.financeiro.model.Usuario;

@Repository
public class UsuarioDao {
    private ResultSet resultado;
    
    public Long adicionarUsuario(Usuario usuario) {
        String sql = "INSERT INTO tb_usuario (nm_nome, nm_email, nm_senha, nr_saldo, nr_despesas, nr_receitas) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = Conexao.abrir();
             PreparedStatement desc = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            desc.setString(1, usuario.getNome());
            desc.setString(2, usuario.getEmail());
            desc.setString(3, usuario.getSenha());
            desc.setBigDecimal(4, usuario.getSaldo());
            desc.setBigDecimal(5, BigDecimal.ZERO);
            desc.setBigDecimal(6, BigDecimal.ZERO);
            desc.executeUpdate();

            try (ResultSet generatedKeys = desc.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Falha ao adicionar usuário, nenhum ID gerado.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public boolean adicionarSaldo(Long id, BigDecimal novoSaldo) {
        try (Connection con = Conexao.abrir();
             PreparedStatement desc = con.prepareStatement(
                     "UPDATE tb_usuario SET nr_saldo = nr_saldo + ? WHERE pk_id_usuario = ?")) {
            desc.setBigDecimal(1, novoSaldo);
            desc.setLong(2, id);
            desc.execute();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void removerUsuarioDaLigacao(Long id){
        try (Connection con = Conexao.abrir();
             PreparedStatement desc = con.prepareStatement("DELETE FROM tb_usuario_categoria WHERE fk_id_usuario = ?")) {
            desc.setLong(1, id);
            desc.execute();
        } catch (SQLException e) {
            throw new FinanceiroException(e.getMessage(), e);
        }
    }

    public Usuario procurarUsuarioPorId(Long id) {
        Usuario usuario = new Usuario();
        try (Connection con = Conexao.abrir();
             PreparedStatement desc = con.prepareStatement(
                     "SELECT * FROM tb_usuario WHERE pk_id_usuario = ?")) {
            desc.setLong(1, id);
            resultado = desc.executeQuery();
            if (resultado.next()) {
                usuario.setId(resultado.getLong("pk_id_usuario"));
                usuario.setSaldo(resultado.getBigDecimal("nr_saldo"));
                usuario.setDespesas(resultado.getBigDecimal("nr_despesas"));
                usuario.setReceitas(resultado.getBigDecimal("nr_receitas"));
                usuario.setEmail(resultado.getString("nm_email"));
                usuario.setNome(resultado.getString("nm_nome"));
                usuario.setSenha(resultado.getString("nm_senha"));
            }
        } catch (SQLException e) {
            throw new FinanceiroException(e.getMessage(), e);
        }
        return usuario;
    }

    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection con = Conexao.abrir();
             Statement declaracao = con.createStatement()) {
            resultado = declaracao.executeQuery("SELECT * FROM tb_usuario");
            while (resultado.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(resultado.getLong("pk_id_usuario"));
                usuario.setSaldo(resultado.getBigDecimal("nr_saldo"));
                usuario.setEmail(resultado.getString("nm_email"));
                usuario.setNome(resultado.getString("nm_nome"));
                usuario.setSenha(resultado.getString("nm_senha"));
                usuario.setDespesas(resultado.getBigDecimal("nr_despesas"));
                usuario.setReceitas(resultado.getBigDecimal("nr_receitas"));
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            throw new FinanceiroException(e.getMessage(), e);
        }
        return usuarios;
    }

    public void adicionarDespesa(BigDecimal valor, Long id) {
        Usuario usuario = procurarUsuarioPorId(id);
            if(usuario.getSaldo().compareTo(valor) < 0) {
                throw new FinanceiroException("Saldo insuficiente");
            }

        try (Connection con = Conexao.abrir();
             PreparedStatement desc = con.prepareStatement(
                     "UPDATE tb_usuario SET nr_saldo = ?, nr_despesas = ? WHERE pk_id_usuario = ?")) {
            desc.setBigDecimal(1, usuario.getSaldo().subtract(valor));
            desc.setBigDecimal(2, usuario.getDespesas().add(valor));
            desc.setLong(3, usuario.getId());
            desc.execute();
        } catch (SQLException e) {
            throw new FinanceiroException(e.getMessage(), e);
        }
    }
    
    public void retirarDespesa(BigDecimal valor, Long id) {
        Usuario usuario = procurarUsuarioPorId(id);
        try (Connection con = Conexao.abrir();
             PreparedStatement desc = con.prepareStatement(
                     "UPDATE tb_usuario SET nr_saldo = ?, nr_despesas = ? WHERE pk_id_usuario = ?")) {
            desc.setBigDecimal(1, usuario.getSaldo().add(valor));
            desc.setBigDecimal(2, usuario.getDespesas().subtract(valor));
            desc.setLong(3, usuario.getId());
            desc.execute();
        } catch (SQLException e) {
            throw new FinanceiroException(e.getMessage(), e);
        }
    }

    public void atualizarReceita(BigDecimal valor, Long id, Usuario usuario) {
        try (Connection con = Conexao.abrir();
             PreparedStatement desc = con.prepareStatement(
                     "UPDATE tb_usuario SET nr_receitas=?, nr_saldo = ? WHERE pk_id_usuario = ?")) {
            desc.setBigDecimal(1, usuario.getReceitas().add(valor));
            desc.setBigDecimal(2, usuario.getSaldo());
            desc.setLong(3, usuario.getId());
            desc.execute();
        } catch (SQLException e) {
            throw new FinanceiroException(e.getMessage(), e);
        }
    }

    public Usuario findbyEmail(String email) {
        try (Connection con = Conexao.abrir();
             PreparedStatement desc = con.prepareStatement(
                     "SELECT * FROM tb_usuario WHERE nm_email = ?")) {
            desc.setString(1, email);
            resultado = desc.executeQuery();
            if (resultado.next()) {
                return new Usuario(
                        resultado.getString("nm_email"),
                        resultado.getString("nm_senha"),
                        resultado.getLong("pk_id_usuario"),
                        resultado.getString("nm_nome"),
                        resultado.getBigDecimal("nr_saldo"),
                        resultado.getBigDecimal("nr_despesas"),
                        resultado.getBigDecimal("nr_receitas")
                );
            }
        } catch (SQLException e) {
            throw new FinanceiroException(e.getMessage(), e);
        }
        return null;
    }

    public void atualizarInformacoes(Usuario usuario) {
        if(findbyEmail(usuario.getEmail()) != null) {
            throw new FinanceiroException("Email ja cadastrado");
        }
        try (Connection con = Conexao.abrir();
             PreparedStatement desc = con.prepareStatement(
                     "UPDATE tb_usuario SET nm_nome = ?, nm_senha = ? , nm_email = ? WHERE pk_id_usuario = ?")) {
            desc.setString(1, usuario.getNome());
            desc.setString(2, usuario.getSenha());
            desc.setString(3, usuario.getEmail());
            desc.setLong(4, usuario.getId());
            desc.execute();
        } catch (SQLException e) {
            throw new FinanceiroException(e.getMessage(), e);
        }
}
}

