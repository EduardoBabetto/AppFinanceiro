# App de gerenciamento financeiro pessoal

Uma aplicação financeira para gerenciamento das finanças pessoais.

# Índice

- [Descrição](#descrição)
- [Instalação](#instalação)
- [Uso](#uso)
- [Configuração](#configuração)
- [Licença](#licença)
- [Autores](#autores)
- [Tecnologias] (#tecnologias)
# Descrição

Este projeto é uma aplicação financeira desenvolvida em Java utilizando Spring Boot. Ele oferece funcionalidades de gerenciamento de contas de usuário, autenticação JWT.

# Instalação

### Pré-requisitos

- [Java 17](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html)
- [Maven](https://maven.apache.org/)

### Passos

1. Clone o repositório:
   ```bash
   git clone https://github.com/EduardoBabetto/AppFinanceiro.git

# Uso

### Endpoints principais

Login: "Post /auth/login"
```json
{
  "username": "example@gmail.com",
  "password": "12345678"
}
```

Adicionar Usuário: "Post /usuario/adicionar/"
```json
{
    "nome": "Ash Ketchium",
    "email": "Groundon@gmail.com",
    "senha": "12345677",
    "saldo": 100.00
}
```

# Configuração

## Configurações do banco de dados
spring.datasource.url=jdbc:postgresql://localhost:5432/financeiro
spring.datasource.username=postgres
spring.datasource.password=3duBDD!$
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

## Configurações JWT
projeto.jwtSecret=bxOksa8BHgdAhR80Y3pEYvS5M+MnF2sheFDqprkTqQ4odqoszJLW1ikw64/nT/dTvlgrcBTq7HfK1B9Gai2h5A==

projeto.jwtExpirationMs=90000

# Link de documentação

Link da documentação
Uma vez com a aplicação em execução em sua maquina acesse acesse o link abaixo para ter acesso a documentação completa dos enpoints:

http://localhost:8080/api-myfreelas/swagger-ui.html

# Licença
Este projeto está licenciado sob a licença MIT. Veja o arquivo LICENSE para mais detalhes.

# Autores
- Eduardo dos Santos Babetto

# Histórico de Versões
[1.0.0] - 2024-07-01

# Tecnologias
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)
![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Visual Studio Code](https://img.shields.io/badge/Visual%20Studio%20Code-0078d7.svg?style=for-the-badge&logo=visual-studio-code&logoColor=white)
![Markdown](https://img.shields.io/badge/markdown-%23000000.svg?style=for-the-badge&logo=markdown&logoColor=white)
![Swagger](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Jira](https://img.shields.io/badge/jira-%230A0FFF.svg?style=for-the-badge&logo=jira&logoColor=white)
### Explicação das Seções

- **Título do Projeto**: Nome do seu projeto.
- **Descrição**: Uma breve descrição do que é o projeto e suas funcionalidades principais.
- **Índice**: Facilita a navegação no README.
- **Instalação**: Passo a passo para configurar e rodar o projeto localmente.
- **Uso**: Exemplos de como utilizar as funcionalidades principais do projeto.
- **Configuração**: Instruções sobre como configurar o projeto, incluindo variáveis de ambiente e propriedades de configuração.
- **Licença**: Informação sobre a licença do projeto.
- **Autores**: Quem contribuiu com o projeto.
- **Histórico de Versões**: Registro das versões do projeto e suas mudanças principais.

Este modelo pode ser adaptado conforme o crescimento e a evolução do seu projeto, adicionando seções ou detalhes específicos conforme necessário.
