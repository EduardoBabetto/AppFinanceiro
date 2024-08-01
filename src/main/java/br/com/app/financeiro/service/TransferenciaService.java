package br.com.app.financeiro.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.app.financeiro.dao.TransferenciaDao;
import br.com.app.financeiro.enuns.TipoTransferencia;
import br.com.app.financeiro.exceptions.FinanceiroException;
import br.com.app.financeiro.model.Transferencia;
import br.com.app.financeiro.model.Usuario;

@Service
public class TransferenciaService {
    
    private final TransferenciaDao transferenciaDao;
    private final UsuarioService usuarioService;

    public TransferenciaService(UsuarioService usuarioService, TransferenciaDao transferenciaDao) {
        this.transferenciaDao = transferenciaDao;
        this.usuarioService=  usuarioService;
    }

    public void adicionarTransacao(Transferencia transacao) {
        validarTransferencia(transacao);
        transferenciaDao.adicionarTransferencia(transacao);
        Usuario u = usuarioService.retornarInformacoes(transacao.getUsuarioId());
        if(transacao.getTipo().equals(TipoTransferencia.ENTRADA)){
            usuarioService.atualizarReceita(transacao);
        }
        else{
            if(transacao.getValor().compareTo(u.getSaldo()) > 0){
             throw new FinanceiroException("Não pode remover valor maior que o seu saldo");
            }
            else{
                usuarioService.adicionarDespesa(transacao.getValor(), transacao.getUsuarioId());
            }
        }
    }

    public List<Transferencia> listarTransacoes() {
        return transferenciaDao.listarTransacoes(); 
    }

    public List<Transferencia> extratoPorContas(Long usuarioId) {
        return transferenciaDao.extratoPorContas(usuarioId);
    }

    public void removerTransacao(Transferencia transferencia) {
        transferenciaDao.removerTransacao(transferencia);
    }

    public List<Transferencia> extratoOrdData (Long id) {
        return transferenciaDao.extratoOrdData(id);
    }

    public List<Transferencia> extratoOrdValor (Long id) {
        return transferenciaDao.extratoOrdValor(id);
    }

    public List<Transferencia> extratoFiltrarTipo (Long id, TipoTransferencia tipo) {
        return transferenciaDao.extratoFiltrarTipo(id,tipo);
    }

    public List<Transferencia> extratoFiltrarCategoria (Long id, String categoria) {
        return transferenciaDao.extratoFiltrarCategoria(id,categoria);
    }

    public List<Transferencia> extratoFiltrarData (Long id, LocalDate data) {
        return transferenciaDao.extratoFiltrarData(id,data);
    }

    private void validarTransferencia(Transferencia transferencia) {
        if (transferencia == null) {
            throw new FinanceiroException("Transferência não pode ser nula.");
        }
        validarNomeDestinatario(transferencia.getNomeDestinatario());
        validarCategoria(transferencia.getCategoria());
        validarTipoTransferencia(transferencia.getTipo());
    }

    private void validarNomeDestinatario(String nomeDestinatario) {
        if (!nomeDestinatario.matches("^[A-Za-zÀ-ÖØ-öø-ÿÇç0-9]+$")) {
            throw new FinanceiroException("Nome do destinatário inválido. Deve conter apenas letras (maiúsculas e minúsculas), incluindo caracteres acentuados e ç.");
        }
    }

    private void validarCategoria(String categoria) {
        if (!categoria.matches("^[A-Za-zÀ-ÖØ-öø-ÿÇç ]+$")) {
            throw new FinanceiroException("Categoria inválida. Deve conter apenas letras (maiúsculas e minúsculas), incluindo caracteres acentuados, ç e espaços.");
        }
    }

    private void validarTipoTransferencia(TipoTransferencia tipo) {
        if (tipo == null) {
            throw new FinanceiroException("Tipo de transferência não pode ser nulo.");
        }
    }


}
