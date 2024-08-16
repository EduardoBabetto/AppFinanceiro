package br.com.app.financeiro.service;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import br.com.app.financeiro.dao.TransferenciaDao;
import br.com.app.financeiro.enuns.TipoTransferencia;
import br.com.app.financeiro.err.exceptions.FinanceiroException;
import br.com.app.financeiro.model.Transferencia;
import br.com.app.financeiro.model.Usuario;

@Service
public class TransferenciaService {

    private static final Logger logger = LoggerFactory.getLogger(TransferenciaService.class);

    private final TransferenciaDao transferenciaDao;
    private final UsuarioService usuarioService;

    public TransferenciaService(UsuarioService usuarioService, TransferenciaDao transferenciaDao) {
        this.transferenciaDao = transferenciaDao;
        this.usuarioService = usuarioService;
    }

    public Long adicionarTransacao(Transferencia transacao) {
        try {
            validarTransferencia(transacao);
            Long id = transferenciaDao.adicionarTransferencia(transacao);
            Usuario u = usuarioService.retornarInformacoes(transacao.getUsuarioId());

            if (transacao.getTipo().equals(TipoTransferencia.ENTRADA)) {
                usuarioService.atualizarReceita(transacao);
            } else {
                if (transacao.getValor().compareTo(u.getSaldo()) > 0) {
                    throw new FinanceiroException("Não pode remover valor maior que o seu saldo",
                    HttpStatus.CONFLICT);
                } else {
                    usuarioService.adicionarDespesa(transacao.getValor(), transacao.getUsuarioId());
                }
            }
            return id;
        } catch (FinanceiroException ex) {
            logger.error("Erro ao adicionar transação: {}", ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            logger.error("Erro inesperado ao adicionar transação", ex);
            throw new FinanceiroException("Erro ao adicionar transação.",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<Transferencia> listarTransacoes() {
        try {
            return transferenciaDao.listarTransacoes();
        } catch (Exception ex) {
            logger.error("Erro ao listar transações", ex);
            throw new FinanceiroException("Erro ao listar transações.",HttpStatus.BAD_REQUEST);
        }
    }

    public List<Transferencia> extratoPorContas(Long usuarioId) {
        try {
            return transferenciaDao.extratoPorContas(usuarioId);
        } catch (Exception ex) {
            logger.error("Erro ao obter extrato por contas", ex);
            throw new FinanceiroException("Erro ao obter extrato por contas.",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void removerTransacao(Transferencia transferencia) {
        try {
            transferenciaDao.removerTransacao(transferencia);
        } catch (Exception ex) {
            logger.error("Erro ao remover transação", ex);
            throw new FinanceiroException("Erro ao remover transação.",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<Transferencia> extratoOrdData(Long id) {
        try {
            return transferenciaDao.extratoOrdData(id);
        } catch (Exception ex) {
            logger.error("Erro ao ordenar extrato por data", ex);
            throw new FinanceiroException("Erro ao ordenar extrato por data.",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<Transferencia> extratoOrdValor(Long id) {
        try {
            return transferenciaDao.extratoOrdValor(id);
        } catch (Exception ex) {
            logger.error("Erro ao ordenar extrato por valor", ex);
            throw new FinanceiroException("Erro ao ordenar extrato por valor.",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<Transferencia> extratoFiltrarTipo(Long id, TipoTransferencia tipo) {
        try {
            return transferenciaDao.extratoFiltrarTipo(id, tipo);
        } catch (Exception ex) {
            logger.error("Erro ao filtrar extrato por tipo de transferência", ex);
            throw new FinanceiroException("Erro ao filtrar extrato por tipo de transferência.",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<Transferencia> extratoFiltrarCategoria(Long id, String categoria) {
        try {
            return transferenciaDao.extratoFiltrarCategoria(id, categoria);
        } catch (Exception ex) {
            logger.error("Erro ao filtrar extrato por categoria", ex);
            throw new FinanceiroException("Erro ao filtrar extrato por categoria.",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<Transferencia> extratoFiltrarData(Long id, LocalDate data) {
        try {
            return transferenciaDao.extratoFiltrarData(id, data);
        } catch (Exception ex) {
            logger.error("Erro ao filtrar extrato por data", ex);
            throw new FinanceiroException("Erro ao filtrar extrato por data.",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void validarTransferencia(Transferencia transferencia) {
        if (transferencia == null) {
            throw new FinanceiroException("Transferência não pode ser nula.",HttpStatus.INTERNAL_SERVER_ERROR);
        }
        validarNomeDestinatario(transferencia.getNomeDestinatario());
        validarCategoria(transferencia.getCategoria());
        validarTipoTransferencia(transferencia.getTipo());
    }

    private void validarNomeDestinatario(String nomeDestinatario) {
        if (!nomeDestinatario.matches("^[A-Za-zÀ-ÖØ-öø-ÿÇç0-9]+$")) {
            throw new FinanceiroException("Nome do destinatário inválido. Deve conter apenas letras (maiúsculas e minúsculas),"+
             "incluindo caracteres acentuados e ç.",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void validarCategoria(String categoria) {
        if (!categoria.matches("^[A-Za-zÀ-ÖØ-öø-ÿÇç ]+$")) {
            throw new FinanceiroException("Categoria inválida. Deve conter apenas letras (maiúsculas e minúsculas), "+
            "incluindo caracteres acentuados, ç e espaços.",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void validarTipoTransferencia(TipoTransferencia tipo) {
        if (tipo == null) {
            throw new FinanceiroException("Tipo de transferência não pode ser nulo.",HttpStatus.BAD_REQUEST);
        }
    }
}
