package br.com.app.financeiro.service;

import java.io.IOException;
import java.math.BigDecimal;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.app.financeiro.err.exceptions.FinanceiroException;

@Service
public class APIConversao {

    private static final Logger logger = LoggerFactory.getLogger(APIConversao.class);
    private static final String BASE_URL = "https://economia.awesomeapi.com.br/json/last/";

    public BigDecimal getCotacao(String moedaOrigem) {
        String parMoedas = moedaOrigem + "BRL";
        String apiUrl = BASE_URL + moedaOrigem + "-BRL";
        BigDecimal taxa = BigDecimal.ONE;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(apiUrl);
            logger.info("Enviando solicitação para a API: {}", apiUrl);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                
                // Tratamento específico para status code
                if (statusCode == 200) {
                    // Sucesso
                    logger.info("Resposta bem-sucedida da API: {}", statusCode);
                    taxa = processarResposta(response, parMoedas);
                } else if (statusCode == 404) {
                    // Recurso não encontrado
                    logger.warn("Recurso não encontrado: {}", parMoedas);
                    throw new FinanceiroException("Par de moedas não encontrado: " + parMoedas, 404, HttpStatus.BAD_REQUEST);
                } else if (statusCode >= 500) {
                    // Erro no servidor da API
                    logger.error("Erro no servidor da API: Código de status {}", statusCode);
                    throw new FinanceiroException("Erro no servidor da API: Código de status " + statusCode, 500, HttpStatus.BAD_REQUEST);
                } else {
                    // Outros erros inesperados
                    logger.error("Erro inesperado na API: Código de status {}", statusCode);
                    throw new FinanceiroException("Erro inesperado na API: Código de status " + statusCode, statusCode, HttpStatus.BAD_REQUEST);
                }

            }
        } catch (IOException e) {
            logger.error("Erro de comunicação ao buscar taxas de conversão: {}", e.getMessage(), e);
            throw new FinanceiroException("Erro de comunicação ao buscar taxas de conversão", 500, e, HttpStatus.BAD_REQUEST);
        }

        logger.info("Cotação obtida com sucesso: {} = {}", parMoedas, taxa);
        return taxa;
    }

    private BigDecimal processarResposta(CloseableHttpResponse response, String parMoedas) throws IOException {
        HttpEntity entity = response.getEntity();
        if (entity == null) {
            logger.error("Resposta da API é nula");
            throw new FinanceiroException("A resposta da API está vazia.", 500, HttpStatus.BAD_REQUEST);
        }

        String result = EntityUtils.toString(entity);
        logger.debug("Resposta da API: {}", result);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(result);

        if (rootNode == null || rootNode.isEmpty()) {
            logger.error("Estrutura do JSON é nula ou vazia");
            throw new FinanceiroException("Estrutura de resposta da API inválida.", 500, HttpStatus.BAD_REQUEST);
        }

        JsonNode moedaNode = rootNode.path(parMoedas);
        if (moedaNode.isMissingNode() || !moedaNode.has("bid")) {
            logger.warn("Cotação não encontrada para o par: {}", parMoedas);
            throw new FinanceiroException("Cotação não encontrada para o par: " + parMoedas, 404, HttpStatus.NOT_FOUND);
        }

        return new BigDecimal(moedaNode.path("bid").asText());
    }
}