# Sistema Biblioteca

<p align="center">
  <img src="https://img.shields.io/badge/Linguagem-Java%2026-ED8B00?logo=openjdk&logoColor=white" alt="Linguagem: Java 26">
  <img src="https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven&logoColor=white" alt="Build: Maven">
  <img src="https://img.shields.io/badge/Banco-PostgreSQL-4169E1?logo=postgresql&logoColor=white" alt="Banco: PostgreSQL">
  <img src="https://img.shields.io/badge/IDE-NetBeans-1B6AC6?logo=apachenetbeanside&logoColor=white" alt="IDE: NetBeans">
</p>

Sistema de biblioteca simples em Java, desenvolvido em sala de aula na disciplina de Programação de Computadores 3 (Sistemas de Informação, IFPR). O projeto ainda está em desenvolvimento.

## Anotações das aulas

### Aula 1 — Criação do projeto
- Projeto Maven criado no NetBeans.
- Pacotes separados no padrão MVC: modelo, controle e visão.

### Aula 2 — Classes do modelo
- Classes Livro, Usuario, Aluno, Professor e Biblioteca.
- Aluno e Professor herdam de Usuario.
- A Biblioteca guarda o acervo e os usuários em listas, com métodos para adicionar e remover.

### Aula 3 — Controle e visão
- Construtores nas classes do modelo.
- Busca de livro por título.
- BibliotecaControle: cadastra livros, alunos e professores, lista e busca.
- BibliotecaVisao: mostra as informações no terminal.
- O main passa a usar modelo, controle e visão com dados de exemplo.

### Aula 4 — Ajustes no main
- Dados de exemplo atualizados e variáveis em português.
- Correção na exibição da matrícula.

### Aula 5 — Ligação com o banco de dados
- Driver do PostgreSQL adicionado no Maven.
- Scripts SQL para criar o banco, o usuário e as tabelas de livros e usuários.
- ConexaoBanco: abre a conexão com o banco usando JDBC.
