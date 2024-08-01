package br.com.app.financeiro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "br.com.app.financeiro")
public class FinanceiroApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinanceiroApplication.class, args);
	}

}
