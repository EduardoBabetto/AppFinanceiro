package br.com.app.financeiro.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import br.com.app.financeiro.conexao.Conexao;
import br.com.app.financeiro.enuns.TipoTransferencia;
import br.com.app.financeiro.err.exceptions.FinanceiroException;
import br.com.app.financeiro.model.Transferencia;
import br.com.app.financeiro.service.UsuarioService;

@Repository
public class TransferenciaDao {

    private UsuarioService us;

    public TransferenciaDao(UsuarioService us) {
        this.us = us;
    }

    public Long adicionarTransferencia(Transferencia transferencia) {
        String sql = "INSERT INTO tb_transferencia VALUES (default,?,?,?,?,?,?)";

        try (Connection con = Conexao.abrir();
             PreparedStatement desc = con.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS)) {

            desc.setLong(1, transferencia.getUsuarioId());
            desc.setString(2, transferencia.getNomeDestinatario());
            desc.setBigDecimal(3, transferencia.getValor());
            desc.setString(4, transferencia.getCategoria());
            desc.setString(5, transferencia.getTipo().name());
            desc.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));

            desc.executeUpdate();
            try (ResultSet generatedKeys = desc.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Falha ao adicionar usuário, nenhum ID gerado.");
                }
            }
        } catch (SQLException e) {
            throw new FinanceiroException(e.getMessage(), e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(Exception e){
            throw new FinanceiroException(e.getMessage(),e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<Transferencia> listarTransacoes() {
        List<Transferencia> transferencias = new ArrayList<>();
        String sql = "SELECT * FROM tb_transferencia";

        try (Connection con = Conexao.abrir();
             Statement declaracao = con.createStatement();
             ResultSet resultado = declaracao.executeQuery(sql)) {

            while (resultado.next()) {
                Transferencia transferencia = new Transferencia();
                transferencia.setId(resultado.getLong("pk_id_transferencia"));
                transferencia.setUsuarioId(resultado.getLong("fk_id_usuario"));
                transferencia.setNomeDestinatario(resultado.getString("nm_nome_destinatario"));
                transferencia.setCategoria(resultado.getString("nm_categoria"));
                transferencia.setTipo(TipoTransferencia.valueOf(resultado.getString("tp_transferencia")));
                transferencia.setValor(resultado.getBigDecimal("nr_valor"));
                java.sql.Date sqlDate = resultado.getDate("dt_data");
                if (sqlDate != null) {
                    transferencia.setData(sqlDate.toLocalDate());
                }
                transferencias.add(transferencia);
            }
        } catch (SQLException e) {
            throw new FinanceiroException(e.getMessage(), e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(Exception e){
            throw new FinanceiroException(e.getMessage(),e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return transferencias;
    }

    public List<Transferencia> extratoPorContas(Long id) {
        List<Transferencia> extrato = new ArrayList<>();
        String sql = "SELECT * FROM tb_transferencia WHERE fk_id_usuario = ?";

        try (Connection con = Conexao.abrir();
             PreparedStatement declaracao = con.prepareStatement(sql)) {
                 
                 declaracao.setLong(1, id);
                 try (ResultSet resultado = declaracao.executeQuery()) {
                     while (resultado.next()) {
                    Transferencia transferencia = new Transferencia();
                    transferencia.setId(resultado.getLong("pk_id_transferencia"));
                    transferencia.setUsuarioId(resultado.getLong("fk_id_usuario"));
                    transferencia.setNomeDestinatario(resultado.getString("nm_nome_destinatario"));
                    transferencia.setCategoria(resultado.getString("nm_categoria"));
                    transferencia.setTipo(TipoTransferencia.valueOf(resultado.getString("tp_transferencia")));
                    transferencia.setValor(resultado.getBigDecimal("nr_valor"));
                    java.sql.Date sqlDate = resultado.getDate("dt_data");
                    if (sqlDate != null) {
                        transferencia.setData(sqlDate.toLocalDate());
                    }
                    extrato.add(transferencia);
                }
            }
        } catch (SQLException e) {
            throw new FinanceiroException(e.getMessage(), e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(Exception e){
            throw new FinanceiroException(e.getMessage(),e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return extrato;
    }

    public Transferencia buscarTransferencia(Long id){
        String sql = "SELECT * FROM tb_transferencia WHERE pk_id_transferencia = ?";
        Transferencia transferencia = new Transferencia();
        try (Connection con = Conexao.abrir();
             PreparedStatement declaracao = con.prepareStatement(sql)) {
                 
                 declaracao.setLong(1, id);
                 try (ResultSet resultado = declaracao.executeQuery()) {
                     while (resultado.next()) {
                    transferencia.setId(resultado.getLong("pk_id_transferencia"));
                    transferencia.setUsuarioId(resultado.getLong("fk_id_usuario"));
                    transferencia.setNomeDestinatario(resultado.getString("nm_nome_destinatario"));
                    transferencia.setCategoria(resultado.getString("nm_categoria"));
                    transferencia.setTipo(TipoTransferencia.valueOf(resultado.getString("tp_transferencia")));
                    transferencia.setValor(resultado.getBigDecimal("nr_valor"));
                    java.sql.Date sqlDate = resultado.getDate("dt_data");
                    if (sqlDate != null) {
                        transferencia.setData(sqlDate.toLocalDate());
                    }
                }
            }
        } catch (SQLException e) {
            throw new FinanceiroException(e.getMessage(), e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(Exception e){
            throw new FinanceiroException(e.getMessage(),e,HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return transferencia;
    }

    public void removerTransacao(Transferencia transferencia) {
        Transferencia t = buscarTransferencia(transferencia.getId());
        if (t.getTipo() == TipoTransferencia.ENTRADA) {
            t.setValor(t.getValor().negate());
            us.atualizarReceita(t);
        } else {
            us.removerDespesa(t.getValor(), transferencia.getUsuarioId());
        }
        String sql = "DELETE FROM tb_transferencia WHERE pk_id_transferencia = ? AND fk_id_usuario = ?";
        try (Connection con = Conexao.abrir();
             PreparedStatement desc = con.prepareStatement(sql)) {
            desc.setLong(1, transferencia.getId());
            desc.setLong(2, transferencia.getUsuarioId());
            desc.executeUpdate();
        } catch (SQLException e) {
            throw new FinanceiroException(e.getMessage(), e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(Exception e){
            throw new FinanceiroException(e.getMessage(),e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<Transferencia> extratoOrdData(Long id) {
        List<Transferencia> extrato = new ArrayList<>();
        String sql = "SELECT * FROM tb_transferencia WHERE fk_id_usuario = ? ORDER BY dt_data";
        try (Connection con = Conexao.abrir();
             PreparedStatement declaracao = con.prepareStatement(sql)) {
                 
                 declaracao.setLong(1, id); 
                 try (ResultSet resultado = declaracao.executeQuery()) {
                     while (resultado.next()) {
                    Transferencia transferencia = new Transferencia();
                    transferencia.setId(resultado.getLong("pk_id_transferencia"));
                    transferencia.setUsuarioId(resultado.getLong("fk_id_usuario"));
                    transferencia.setNomeDestinatario(resultado.getString("nm_nome_destinatario"));
                    transferencia.setCategoria(resultado.getString("nm_categoria"));
                    transferencia.setTipo(TipoTransferencia.valueOf(resultado.getString("tp_transferencia")));
                    transferencia.setValor(resultado.getBigDecimal("nr_valor"));
                    java.sql.Date sqlDate = resultado.getDate("dt_data");
                    if (sqlDate != null) {
                        transferencia.setData(sqlDate.toLocalDate());
                    }
                    extrato.add(transferencia);
                }
            }
        } catch (SQLException e) {
            throw new FinanceiroException(e.getMessage(), e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(Exception e){
            throw new FinanceiroException(e.getMessage(),e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return extrato;
    }

    public List<Transferencia> extratoOrdValor(Long id) {
        List<Transferencia> extrato = new ArrayList<>();
        String sql = "SELECT * FROM tb_transferencia WHERE fk_id_usuario = ? ORDER BY nr_valor";
        try (Connection con = Conexao.abrir();
             PreparedStatement declaracao = con.prepareStatement(sql)) {
                 
                 declaracao.setLong(1, id); 
                 try (ResultSet resultado = declaracao.executeQuery()) {
                     while (resultado.next()) {
                    Transferencia transferencia = new Transferencia();
                    transferencia.setId(resultado.getLong("pk_id_transferencia"));
                    transferencia.setUsuarioId(resultado.getLong("fk_id_usuario"));
                    transferencia.setNomeDestinatario(resultado.getString("nm_nome_destinatario"));
                    transferencia.setCategoria(resultado.getString("nm_categoria"));
                    transferencia.setTipo(TipoTransferencia.valueOf(resultado.getString("tp_transferencia")));
                    transferencia.setValor(resultado.getBigDecimal("nr_valor"));
                    java.sql.Date sqlDate = resultado.getDate("dt_data");
                    if (sqlDate != null) {
                        transferencia.setData(sqlDate.toLocalDate());
                    }
                    extrato.add(transferencia);
                }
            }
        } catch (SQLException e) {
            throw new FinanceiroException(e.getMessage(), e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(Exception e){
            throw new FinanceiroException(e.getMessage(),e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return extrato;
    }

    public List<Transferencia> extratoFiltrarTipo(Long id, TipoTransferencia tipo) {
        List<Transferencia> extrato = new ArrayList<>();
        String sql = "SELECT * FROM tb_transferencia WHERE fk_id_usuario = ? AND tp_transferencia = ?";
        try (Connection con = Conexao.abrir();
             PreparedStatement declaracao = con.prepareStatement(sql)) {
                 
                 declaracao.setLong(1, id); 
                 declaracao.setString(2, tipo.name());
                 try (ResultSet resultado = declaracao.executeQuery()) {
                     while (resultado.next()) {
                    Transferencia transferencia = new Transferencia();
                    transferencia.setId(resultado.getLong("pk_id_transferencia"));
                    transferencia.setUsuarioId(resultado.getLong("fk_id_usuario"));
                    transferencia.setNomeDestinatario(resultado.getString("nm_nome_destinatario"));
                    transferencia.setCategoria(resultado.getString("nm_categoria"));
                    transferencia.setTipo(TipoTransferencia.valueOf(resultado.getString("tp_transferencia")));
                    transferencia.setValor(resultado.getBigDecimal("nr_valor"));
                    java.sql.Date sqlDate = resultado.getDate("dt_data");
                    if (sqlDate != null) {
                        transferencia.setData(sqlDate.toLocalDate());
                    }
                    extrato.add(transferencia);
                }
            }
        } catch (SQLException e) {
            throw new FinanceiroException(e.getMessage(), e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(Exception e){
            throw new FinanceiroException(e.getMessage(),e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return extrato;
    }

    public List<Transferencia> extratoFiltrarCategoria(Long id, String categoria) {
        List<Transferencia> extrato = new ArrayList<>();
        String sql = "SELECT * FROM tb_transferencia WHERE fk_id_usuario = ? AND nm_categoria = ?";
        try (Connection con = Conexao.abrir();
             PreparedStatement declaracao = con.prepareStatement(sql)) {
                 
                 declaracao.setLong(1, id); 
                 declaracao.setString(2, categoria); 
                 try (ResultSet resultado = declaracao.executeQuery()) {
                     while (resultado.next()) {
                    Transferencia transferencia = new Transferencia();
                    transferencia.setId(resultado.getLong("pk_id_transferencia"));
                    transferencia.setUsuarioId(resultado.getLong("fk_id_usuario"));
                    transferencia.setNomeDestinatario(resultado.getString("nm_nome_destinatario"));
                    transferencia.setCategoria(resultado.getString("nm_categoria"));
                    transferencia.setTipo(TipoTransferencia.valueOf(resultado.getString("tp_transferencia")));
                    transferencia.setValor(resultado.getBigDecimal("nr_valor"));
                    java.sql.Date sqlDate = resultado.getDate("dt_data");
                    if (sqlDate != null) {
                        transferencia.setData(sqlDate.toLocalDate());
                    }
                    extrato.add(transferencia);
                }
            }
        } catch (SQLException e) {
            throw new FinanceiroException(e.getMessage(), e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(Exception e){
            throw new FinanceiroException(e.getMessage(),e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return extrato;
    }

    public List<Transferencia> extratoFiltrarData(Long id, LocalDate data) {
        List<Transferencia> extrato = new ArrayList<>();
        String sql = "SELECT * FROM tb_transferencia WHERE fk_id_usuario = ? AND dt_data = ?";
        try (Connection con = Conexao.abrir();
             PreparedStatement declaracao = con.prepareStatement(sql)) {
                 
                 declaracao.setLong(1, id); 
                 declaracao.setDate(2, Date.valueOf(data)); 
                 try (ResultSet resultado = declaracao.executeQuery()) {
                     while (resultado.next()) {
                    Transferencia transferencia = new Transferencia();
                    transferencia.setId(resultado.getLong("pk_id_transferencia"));
                    transferencia.setUsuarioId(resultado.getLong("fk_id_usuario"));
                    transferencia.setNomeDestinatario(resultado.getString("nm_nome_destinatario"));
                    transferencia.setCategoria(resultado.getString("nm_categoria"));
                    transferencia.setTipo(TipoTransferencia.valueOf(resultado.getString("tp_transferencia")));
                    transferencia.setValor(resultado.getBigDecimal("nr_valor"));
                    java.sql.Date sqlDate = resultado.getDate("dt_data");
                    if (sqlDate != null) {
                        transferencia.setData(sqlDate.toLocalDate());
                    }
                    extrato.add(transferencia);
                }
            }
        } catch (SQLException e) {
            throw new FinanceiroException(e.getMessage(), e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(Exception e){
            throw new FinanceiroException(e.getMessage(),e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return extrato;
    }
}
