package br.com.app.financeiro.service;

import java.io.IOException;
import java.math.BigDecimal;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.app.financeiro.exceptions.FinanceiroException;

@Service
public class APIConversao {
    
    private static final String BASE_URL = "https://economia.awesomeapi.com.br/last/";

    public  BigDecimal getCotacao(String moedaOrigem) throws IOException {
        String parMoedas = moedaOrigem + "BRL";
        BigDecimal taxas = new BigDecimal(1.0);
     
        String apiUrl = BASE_URL + moedaOrigem + "-BRL";

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(apiUrl);

        System.out.println("Sending request to: " + apiUrl);

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String result = EntityUtils.toString(entity);
                System.out.println("API Response: " + result);

                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(result);

                if (!rootNode.isMissingNode()) {
                    JsonNode moedaNode = rootNode.path(parMoedas);
                    if (!moedaNode.isMissingNode() && moedaNode.has("bid")) {
                        taxas = new BigDecimal(moedaNode.path("bid").asText());
                    } else {
                        System.out.println("Cotação de caixa não encontrada para o par: " + parMoedas);
                    }
                } else {
                    System.out.println("Resposta da API não contém o nó 'rates'");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new FinanceiroException("Falha ao buscar taxas de conversão", 500, e);
        } finally {
            httpClient.close();
        }

        return taxas;
    }
}
