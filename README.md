# Cultural Diary System

Este é um sistema de diário cultural desenvolvido em Java, permitindo aos usuários registrar, gerenciar, pesquisar e avaliar diferentes tipos de mídias culturais como livros, filmes e séries. O sistema oferece tanto uma interface de console quanto uma interface gráfica de usuário (GUI) baseada em JavaFX.

Este projeto foi desenvolvido para a disciplina de MI - Programação, que é baseada na metodologia PBL (Aprendizagem Baseada em Problemas), durante o primeiro semestre de 2025.

## Funcionalidades

* **Gerenciamento de Mídia:**
    * Adicionar novos livros com detalhes como título, autor, editora, ISBN e posse.
    * Adicionar novos filmes com informações como título, direção, roteiro, elenco e plataformas de streaming.
    * Adicionar novas séries, incluindo elenco principal e plataformas de streaming.
    * Adicionar temporadas a séries existentes, com número de episódios e data de lançamento.
* **Gerenciamento de Gêneros:**
    * Adicionar novos gêneros ao sistema.
    * Listar todos os gêneros cadastrados.
* **Sistema de Avaliação (Reviews):**
    * Criar e adicionar avaliações (comentário e nota em estrelas) para livros, filmes e temporadas de séries.
    * As avaliações são aninhadas dentro das respectivas mídias ou temporadas.
* **Pesquisa e Filtragem:**
    * Pesquisar mídias por título, gênero, ano de lançamento, pessoas (autor, diretor, elenco) e ISBN.
    * Listar todas as mídias, com opções de ordenação (alfabética, por avaliação) e filtragem (por ano e gênero).
* **Persistência de Dados:**
    * Os dados são salvos e carregados de um arquivo binário (`cultural_diary.dat`) usando serialização Java, garantindo que as informações não sejam perdidas ao encerrar a aplicação.
* **Interfaces de Usuário:**
    * **Interface de Console:** Permite interação textual via terminal.
    * **Interface Gráfica (JavaFX):** Proporciona uma experiência visual mais rica para adição, pesquisa e visualização de detalhes de mídia.

## Autores

* Julia Santana de Oliveira
* Davi Figuerêdo

