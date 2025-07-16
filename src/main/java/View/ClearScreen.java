package View;

/**
 * Classe utilitária simples para tentar "limpar" a tela do console.
 * A implementação atual apenas imprime múltiplas linhas em branco,
 * o que pode simular uma limpeza em alguns terminais, mas não é uma
 * solução robusta ou universal.
 */
public class ClearScreen {

    /**
     * Tenta limpar a tela do console imprimindo um número fixo (50) de linhas em branco.
     * A eficácia deste método depende do ambiente de terminal onde a aplicação está sendo executada.
     * Não há garantia de que a tela será efetivamente limpa.
     */
public static void clear() {
    for (int i = 0; i < 50; i++) {
        System.out.println();
    }
}
}
