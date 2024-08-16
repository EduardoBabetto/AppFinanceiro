package br.com.app.financeiro.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import br.com.app.financeiro.conexao.Conexao;
import br.com.app.financeiro.err.exceptions.FinanceiroException;
import br.com.app.financeiro.model.Categoria;
import br.com.app.financeiro.model.Usuario;
import br.com.app.financeiro.service.UsuarioService;

@Repository
public class CategoriaDao {

    private ArrayList<Categoria> CategoriasPadroes = new ArrayList<>();
    private UsuarioService us;

    public CategoriaDao(UsuarioService us) {
        this.us = us;
        carregarCategoriasPadroes();
    }

    public Long adicionarCategoria(Categoria categoria) {
        try (Connection con = Conexao.abrir();
             PreparedStatement desc = con.prepareStatement(
                     "INSERT INTO tb_categoria (fk_id_usuario, nm_nome, ds_descricao, nr_valor) VALUES (?,?,?,?)"
                     ,PreparedStatement.RETURN_GENERATED_KEYS)) {
            desc.setLong(1, categoria.getUsuarioId());
            desc.setString(2, categoria.getNome());
            desc.setString(3, categoria.getDescricao());
            desc.setBigDecimal(4, categoria.getValor());
            desc.executeUpdate();
            try (ResultSet generatedKeys = desc.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    categoria.setId(generatedKeys.getLong(1));
                } else {
                    throw new FinanceiroException("Falha ao adicionar categoria, nenhum ID gerado.",HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            ligacaoUsuarioCategoria(categoria);
            return categoria.getId();
        } catch (SQLException e) {
            throw new FinanceiroException(e.getMessage(), e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(Exception e){
            throw new FinanceiroException(e.getMessage(),e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public boolean adicionarCategoriasPadrao(Usuario usuario) {
        try (Connection con = Conexao.abrir()) {
            for (Categoria categoriasPadroes : CategoriasPadroes) {
                try (PreparedStatement desc = con.prepareStatement(
                        "INSERT INTO tb_categoria (fk_id_usuario, nm_nome, ds_descricao, nr_valor) VALUES (?,?,?,?)"
                        ,PreparedStatement.RETURN_GENERATED_KEYS)) {
                    desc.setLong(1, usuario.getId());
                    desc.setString(2, categoriasPadroes.getNome());
                    desc.setString(3, categoriasPadroes.getDescricao());
                    desc.setBigDecimal(4, BigDecimal.ZERO);
                    desc.executeUpdate();
                    try (ResultSet generatedKeys = desc.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            categoriasPadroes.setId(generatedKeys.getLong(1));
                        } else {
                            throw new SQLException("Falha ao adicionar categoria, nenhum ID gerado.");
                        }
                    }
                    categoriasPadroes.setUsuarioId(usuario.getId());
                    ligacaoUsuarioCategoria(categoriasPadroes);
                }
            }
            return true;
        } catch (SQLException e) {
            throw new FinanceiroException(e.getMessage(), e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(Exception e){
            throw new FinanceiroException(e.getMessage(),e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void carregarCategoriasPadroes() {
        Categoria categoria1 = new Categoria();
        categoria1.setNome("Alimentacao");
        categoria1.setDescricao("Comida, bebidas e supermercado");
        categoria1.setValor(BigDecimal.ZERO);
        CategoriasPadroes.add(categoria1);

        Categoria categoria2 = new Categoria();
        categoria2.setNome("Saude");
        categoria2.setDescricao("Plano de saude, vacinas e remedios");
        categoria2.setValor(BigDecimal.ZERO);
        CategoriasPadroes.add(categoria2);

        Categoria categoria3 = new Categoria();
        categoria3.setNome("Lazer");
        categoria3.setDescricao("Cinema, teatro e entretenimento");
        categoria3.setValor(BigDecimal.ZERO);
        CategoriasPadroes.add(categoria3);

        Categoria categoria4 = new Categoria();
        categoria4.setNome("Educacao");
        categoria4.setDescricao("Cursos, aulas e certificacoes");
        categoria4.setValor(BigDecimal.ZERO);
        CategoriasPadroes.add(categoria4);
    }

    public Categoria buscarCategoria(Long usuarioId, String nome) {
       Categoria categoria = new Categoria();
        String sql = "SELECT * FROM tb_categoria WHERE fk_id_usuario = ? AND nm_nome = ?";
        
        try (Connection con = Conexao.abrir();
             PreparedStatement desc = con.prepareStatement(sql)) {
            desc.setLong(1, usuarioId);
            desc.setString(2, nome);
            ResultSet rs = desc.executeQuery();
            
            while (rs.next()) {
                categoria.setNome(rs.getString("nm_nome"));
                categoria.setDescricao(rs.getString("ds_descricao"));
                categoria.setValor(rs.getBigDecimal("nr_valor"));
                categoria.setId(rs.getLong("pk_id_categoria"));
                categoria.setUsuarioId(rs.getLong("fk_id_usuario"));
            }
            
        } catch (SQLException e) {
            throw new FinanceiroException(e.getMessage(), e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(Exception e){
            throw new FinanceiroException(e.getMessage(),e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    
        return categoria;
    }

    public void removerCategoriaDaLigacao(Long id){
        try (Connection con = Conexao.abrir();
             PreparedStatement desc = con.prepareStatement("DELETE FROM tb_usuario_categoria WHERE fk_id_categoria = ?")) {
            desc.setLong(1, id);
            desc.execute();
        } catch (SQLException e) {
            throw new FinanceiroException(e.getMessage(), e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(Exception e){
            throw new FinanceiroException(e.getMessage(),e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    public void removerCategoria(Categoria categoria) {
        Categoria c = buscarCategoria(categoria.getUsuarioId(), categoria.getNome());
        removerCategoriaDaLigacao(c.getId());
        us.removerDespesa(c.getValor(), categoria.getUsuarioId());
        try (Connection con = Conexao.abrir();
             PreparedStatement desc = con.prepareStatement("DELETE FROM tb_categoria WHERE fk_id_usuario = ? AND nm_nome = ?")) {
            desc.setLong(1, categoria.getUsuarioId());
            desc.setString(2, categoria.getNome());
            desc.execute();
        } catch (SQLException e) {
            throw new FinanceiroException(e.getMessage(), e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(Exception e){
            throw new FinanceiroException(e.getMessage(),e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void adicionarValor(Categoria categoria) {
        try (Connection con = Conexao.abrir();
             PreparedStatement desc = con.prepareStatement(
                     "UPDATE tb_categoria SET nr_valor = nr_valor + ? WHERE fk_id_usuario = ? AND nm_nome = ?")) {
            desc.setBigDecimal(1, categoria.getValor());
            desc.setLong(2, categoria.getUsuarioId());
            desc.setString(3, categoria.getNome());
            desc.execute();
        } catch (SQLException e) {
            throw new FinanceiroException(e.getMessage(), e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(Exception e){
            throw new FinanceiroException(e.getMessage(),e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void removerValor(Categoria categoria) {
        try (Connection con = Conexao.abrir();
             PreparedStatement desc = con.prepareStatement(
                     "UPDATE tb_categoria SET nr_valor = nr_valor - ? WHERE fk_id_usuario = ? AND nm_nome = ?")) {
            desc.setBigDecimal(1, categoria.getValor());
            desc.setLong(2, categoria.getUsuarioId());
            desc.setString(3, categoria.getNome());
            desc.execute();
        } catch (SQLException e) {
            throw new FinanceiroException(e.getMessage(), e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(Exception e){
            throw new FinanceiroException(e.getMessage(),e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<Categoria> categoriaPorUsuario(Long usuarioId) {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT c.nm_nome AS categoria_nome, c.ds_descricao AS categoria_descricao, c.nr_valor AS categoria_valor, c.fk_id_usuario AS usuario_id, c.pk_id_categoria AS categoria_id FROM tb_usuario u JOIN tb_usuario_categoria uc ON u.pk_id_usuario = uc.fk_id_usuario JOIN tb_categoria c ON c.pk_id_categoria = uc.fk_id_categoria WHERE u.pk_id_usuario = ?";
        
        try (Connection con = Conexao.abrir();
             PreparedStatement desc = con.prepareStatement(sql)) {
            desc.setLong(1, usuarioId);
            ResultSet rs = desc.executeQuery();
            
            while (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setNome(rs.getString("categoria_nome"));
                categoria.setDescricao(rs.getString("categoria_descricao"));
                categoria.setValor(rs.getBigDecimal("categoria_valor"));
                categoria.setId(rs.getLong("categoria_id"));
                categoria.setUsuarioId(rs.getLong("usuario_id"));
                
                categorias.add(categoria);
            }
            
        } catch (SQLException e) {
            throw new FinanceiroException(e.getMessage(), e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(Exception e){
            throw new FinanceiroException(e.getMessage(),e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    
        return categorias;
    }
        
    public void ligacaoUsuarioCategoria(Categoria categoria) {
        try (Connection con = Conexao.abrir();
             PreparedStatement desc = con.prepareStatement(
                     "INSERT INTO tb_usuario_categoria (fk_id_usuario, fk_id_categoria) VALUES (?,?)")) {
            desc.setLong(1, categoria.getUsuarioId());
            desc.setLong(2, categoria.getId());
            desc.execute();
        } catch (SQLException e) {
            throw new FinanceiroException(e.getMessage(), e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(Exception e){
            throw new FinanceiroException(e.getMessage(),e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
                        
    }

    public void editarCategoria (Categoria categoria){
        try (Connection con = Conexao.abrir();
             PreparedStatement desc = con.prepareStatement(
                     "UPDATE tb_categoria SET nm_nome = ?, ds_descricao = ?, nr_valor = nr_valor +? WHERE pk_id_categoria = ? AND fk_id_usuario = ?")) {
            desc.setString(1, categoria.getNome());
            desc.setString(2, categoria.getDescricao());
            desc.setBigDecimal(3, categoria.getValor());
            desc.setLong(4, categoria.getId());
            desc.execute();
        } catch (SQLException e) {
            throw new FinanceiroException(e.getMessage(), e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(Exception e){
            throw new FinanceiroException(e.getMessage(),e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
