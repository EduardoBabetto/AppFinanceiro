package br.com.app.financeiro.enuns;

public enum TipoTransferencia {
        ENTRADA("entrada"),
        SAIDA("saida");
    
        private String valor;
    
        TipoTransferencia(String valor) {
            this.valor = valor;
        }
    
        public String getValor() {
            return valor;
        }
    
        public static TipoTransferencia fromString(String texto) {
            for (TipoTransferencia b : TipoTransferencia.values()) {
                if (b.valor.equalsIgnoreCase(texto)) {
                    return b;
                }
            }
            return null;
        } 

        public static String toString(TipoTransferencia tipoTransferencia) {
            return tipoTransferencia.getValor();
        }
    }
    
