package teste;

import classe.*;

public class Teste {

	public static void main(String[] args) {
		// Cria a pagina invertida
		Trie Pagina = new Trie();
		
		// Cria o leitor de arquivos
		ReadTextFile reader = new ReadTextFile();
		
		// Cria o controlador, interface de busca, insercao e menus.
		// Seta com o leitor criado
		Controle paginaInvertida = new Controle(reader);
		
		// Seta a pagina invertida no controlador
		paginaInvertida.setPaginaInvertida(Pagina);
		
		// Chama o metodo de leitura de todos os arquivos
		paginaInvertida.readAllFiles();
		
		// Apresenta menu ao usuario
		paginaInvertida.menu();
		
		// Fim de programa
		System.out.println("FIM");
	}
}
