package br.com.app.financeiro.conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    public static Connection abrir() {
		Connection conexao = null;
	try {
		conexao = DriverManager.getConnection("jdbc:postgresql://localhost:5432/financeiro",
	"postgres","3duBDD!$");
	
	}
	catch(SQLException e) {
		System.out.println("Erro ao abrir conexão: "+ e.getMessage());
	}
	return conexao;
}
}
