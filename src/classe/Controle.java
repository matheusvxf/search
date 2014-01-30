package classe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe que controla a leitura dos arquivos e armazenagem na arvore Trie e a
 * busca de palavras e frases na arvore. <br />
 * Utiliza uma arvore trie como pagina invertida para endereçar o numero do
 * caracter do texto que contem a palavra especificada.
 * 
 * @author Matheus Venturyne Xavier Ferreira
 * @version 1.01
 * @see java.util.Hashtable
 * @see java.util.ArrayList
 * @see java.util.Collections
 * @see java.util.Scanner
 * @see java.util.regex.Matcher
 * @see java.util.regex.Pattern
 * @see classe.Word
 * @see classe.ReadTextFile
 * @see classe.Trie
 */

public class Controle {

	/**
	 * Arvore Trie como pagina invertida.
	 * 
	 */
	private Trie paginaInvertida;
	/**
	 * Armazena a ultima palavra pesquisada na pagina.
	 * 
	 */
	private Word buscado;
	/**
	 * Armazena uma lista das palavras da ultima frase pesquisada.
	 */
	private List<Word> buscados;
	/**
	 * Flag. Verdade se a ultima pesquisa foi de frase.
	 */
	private boolean phrase;
	/**
	 * Objeto de manipulacao dos arquivos.
	 * 
	 */
	private final ReadTextFile reader;
	/**
	 * Delimitador inicial das palavras pesquisadas. Caracter a ser inserido no
	 * texto original para marca a ocorrencia da palavra pesquisada. Por padrao
	 * o delimitadores sao codigos para colorir a palavra, mas em determinados
	 * consoles pode nao funcionar de forma adequada apenas inserindo esses
	 * caracteres entre a palavra buscada.
	 */
	private final String beginMarc = "\033[31m";
	/**
	 * Delimitador final das palavras pesquisadas.
	 */
	private final String endMarc = "\033[0m";
	/**
	 * Matrix de calculo das distancias entre palavras nos varios arquivos Usada
	 * para calculo das relevancias relativos a proximidades das palavras
	 * pesquisadas (complexo)
	 */
	private Hashtable<String, List<Integer>[][]> matrix;

	/**
	 * @param reader
	 *            objeto <code>ReadTextFile</code> que controla o fluxo dos
	 *            textos.
	 */
	// Construtor do controlador
	public Controle(ReadTextFile reader) {
		paginaInvertida = new Trie();
		buscado = null;
		buscados = null;
		this.reader = reader;
		phrase = false;
	}

	/**
	 * Insere as palavras contidas na string passada na pagina invertida,
	 * marcada com o nome do arquivo que a frase foi retirada tambem passado
	 * como parametro.
	 * 
	 * Utiliza uma expressao regular para encontras as palavras extraindo o
	 * numero do caracter inicial e final que delelimitam a palavra.
	 * 
	 * @param s
	 *            <code>String</code> contendo o texto.
	 * @param file
	 *            <code>String</code> contendo o nome do documento que foi
	 *            retirado o texto
	 */
	public void putWords(String s, String file) {
		String textoTratado = ReadTextFile.normaliza(s);
		Pattern p = Pattern.compile("\\w+");
		Matcher m = p.matcher(textoTratado);
		Word word;
		while (m.find()) {
			word = new Word(textoTratado.substring(m.start(), m.end()), file,
					m.start());
			paginaInvertida.put(word);
		}
	}

	/**
	 * Metodo para ler todos os arquivos posicionados no objeto reader e inserir
	 * suas palavras na arvore trie invocando o metodo
	 * {@link Controle#putWords(String, String)}
	 */
	// Le todos os arquivos e insere na Trie.
	public void readAllFiles() {
		String file;
		while (reader.hasNextFile()) {
			file = reader.openNextFile();
			putWords(reader.readNextFile(), file);
		}
	}

	/**
	 * Metodo que apresenta o menu para o usuario. De acordo com a opcao
	 * escolhida invoca o metodo {@link Controle#searchByWord(String)} ou
	 * {@link Controle#searchByPhrase(String)} em seguida despacha para o menu
	 * de resultados {@link Controle#resultMenu()}
	 */
	// Menu para o usuario
	public void menu() {
		int op = 1;
		String s;
		Scanner scan = new Scanner(System.in);
		while (op > 0) {
			System.out.println("Escolha a opcao de busca: ");
			System.out.println("1-Busca por palavra");
			System.out.println("2-Busca por frase");
			System.out.println("3-Sair");
			System.out.println("Opcao: ");
			op = scan.nextInt();
			scan.nextLine();
			switch (op) {
			case 1:
				System.out.println("Palavra: ");
				s = scan.nextLine();
				if (this.searchByWord(s))
					this.resultMenu();
				else
					System.out.println("A palavra nao foi encontrada!");
				break;
			case 2:
				System.out.println("Frase: ");
				s = scan.nextLine();
				if (this.searchByPhrase(s))
					this.resultMenu();
				else
					System.out.println("A frase nao foi encontrada!");
				break;
			case 3:
				op = 0;
				break;
			default:
				System.out.println("Opcao Invalida!");
			}
		}
	}

	/**
	 * Pesquisa a palavra recebida na arvore trie. Se encontrado o objeto
	 * <code>Word</code> e armazenado no atributo {@link #buscado} da classe. A
	 * String recebida passa pelo metodo {@link ReadTextFile#normaliza(String)}
	 * antes de realizada a busca para que sejam removidos acentos e/ou
	 * caracteres invalidos na string palavra.
	 * 
	 * @param s
	 *            String contendo a palavra a ser buscada
	 * @return verdadeiro se a palavra foi encontrada, falso caso contrario
	 */
	// Recebe uma string com a palavra a ser buscada na trie
	public boolean searchByWord(String s) {
		String tratada = ReadTextFile.normaliza(s);
		buscado = paginaInvertida.get(new Word(tratada));
		phrase = false;
		if (buscado == null)
			return false;
		return true;
	}

	/**
	 * Pesquisa a frase recebida na arvore trie. As palavra da frase devem estar
	 * contida inteiramente (todas as palavras) em cada arquivo caso contrario
	 * esse arquivo e removido da lista de todas as palavras. Caso nem todas as
	 * palavras estejam contidas em pelo menos um arquivo a frase e marcada como
	 * nao encontrada. A String recebida passa pelo metodo
	 * {@link ReadTextFile#normaliza(String)} antes de realizada a busca para
	 * que sejam removidos acentos e/ou caracteres invalidos na string palavra.
	 * O atributo {@link #buscados} recebe as palavras pesquisadas.
	 * 
	 * @param s
	 *            A frase a ser pesquisada.
	 * @return verdadeiro se a frase foi encontrada em pelo menos um arquivo,
	 *         falso caso contrario.
	 */
	// Recebe uma string com a frase a ser buscada na trie
	public boolean searchByPhrase(String s) {
		phrase = true;
		String tratada = ReadTextFile.normaliza(s);
		Pattern p = Pattern.compile("\\w+");
		Matcher m = p.matcher(tratada);
		Word word;
		buscados = new ArrayList<Word>();
		List<Documento> documentos = new ArrayList<Documento>();
		while (m.find()) {
			word = new Word(tratada.substring(m.start(), m.end()));
			buscado = paginaInvertida.get(word);
			if (buscado == null)
				return false;
			boolean contains = false;
			for (Documento d : buscado.getDocumentos()) {
				for (Documento e : documentos)
					if (d.nome.compareTo(e.nome) == 0)
						contains = true;
				if (!contains)
					documentos.add(d);
			}
			buscados.add(buscado);
		}
		// Seleciona os documentos com ocorrencia de todas as palavras
		List<Documento> tmp = new ArrayList<Documento>();
		tmp.addAll(documentos);
		for (Documento d : documentos) {
			for (Word b : buscados) {
				boolean contem = false;
				for (Documento e : b.getDocumentos())
					if (e.getNome().compareTo(d.getNome()) == 0)
						contem = true;
				if (!contem)
					tmp.remove(d);
			}
		}
		documentos = tmp;
		// Remove os documentos registrados nas palavras que nao entao
		// estao na lista dos documentos que apresentam todas as palvras
		tmp = new ArrayList<Documento>();
		for (Word b : buscados) {
			tmp = new ArrayList<Documento>();
			tmp.addAll(b.getDocumentos());
			for (Documento d : b.getDocumentos()) {
				boolean contem = false;
				for (Documento e : documentos)
					if (e.getNome().compareTo(d.getNome()) == 0)
						contem = true;
				if (!contem)
					tmp.remove(d);
			}
			b.setDocumentos(tmp);
		}
		// Analise se alguma palavra passou a nao possuir documentos
		// validos
		for (Word b : buscados) {
			if (b.getDocumentos().size() == 0)
				return false; // Ha palavra nao esta presente em todos os
								// documentos
		}
		return true;
	}

	/**
	 * Menu para o resultado da pesquisa. Analisa se o atributo {@link #phrase}
	 * esta marcado como verdadeiro indicando que a ultima busca foi uma busca
	 * por frase, despachando para o metodo {@link Controle#resultMenuPhrase()}.
	 * Caso contrario despacha para o metodo {@link Controle#resultMenuWord()}.
	 */
	// Chama o menu do resultado da ultima pesquisa. Analisa se foi pesquisa de
	// palavra ou frase e
	// despacha para o metodo adequado
	public void resultMenu() {
		if (phrase) {
			resultMenuPhrase();
		} else
			resultMenuWord();
	}

	/**
	 * Ordena o {@link ArrayList} {@link Controle#buscados} quanto a relevancia
	 * da frase encontrada nos documentos. Inicialmente ordena os documentos
	 * segundo a contagem total de ocorrencia de todas as palavras dentro do
	 * documento (ultimo parametro de relevancia). Em seguida utiliza o atributo
	 * {@link Controle#matrix} como estrutura de dados adicionais para calculo
	 * das distancias e busca dos arquivos que possuem menor distancia entre as
	 * palavras. A estrutura e um hashMap cuja chave e o nome do documento e
	 * possui uma referencia para uma matriz de duas dimensoes, onde a primeira
	 * dimensao representa as palavras da frase (ordem da palavra na frase), e a
	 * segunda dimensao representa a distancia da palavra da dimensao n para a
	 * palavra de dimensao (n+1). Cada combinacao de dimensoes [n][m] possui um
	 * {@link ArrayList} devido a propriedade de ocorrencias serem
	 * indeterminadas, portando as distancias da ocorrencia de uma palavra as
	 * ocorrencias da palavra seguinte pode assumir quantidades variadas. <br/ >
	 * <br/ >
	 * Emprega {@link java.util.Collections#sort(List, java.util.Comparator)}
	 * como parte da ordenacao.
	 * 
	 * @return {@link ArrayList} com a ordem de relevancia dos documentos.
	 */
	@SuppressWarnings("unchecked")
	private List<Documento> ordPorRel() {
		int[] fitness = new int[buscados.get(0).getDocumentos().size()];
		for (int i = 0; i < buscados.size(); i++) {
			for (int j = 0; j < buscados.get(i).getDocumentos().size(); j++) {
				fitness[j] += buscados.get(i).getDocumentos().get(j).ocorrencias
						.size();
			}
		}
		List<Documento> doc = new ArrayList<Documento>();
		doc.addAll(buscados.get(0).getDocumentos());

		Documento aux;
		int aux2;
		Documento[] list = new Documento[doc.size()];
		doc.toArray(list);

		// Realiza uma pre-ordenacao dos documentos segundo o criterio de menor
		// relevancia:
		// A maior soma de ocorrencia de todas as palavras.
		for (int i = 0; i < doc.size() - 1; i++) {
			for (int j = 0; j < doc.size() - 1; j++) {
				if (fitness[j] < fitness[j + 1]) {
					aux = list[j];
					aux2 = fitness[j];
					list[j] = list[j + 1];
					fitness[j] = fitness[j + 1];
					list[j + 1] = aux;
					fitness[j + 1] = aux2;
				}
			}
		}

		doc.clear();
		for (int i = 0; i < buscados.get(0).getDocumentos().size(); i++) {
			doc.add(list[i]);
		}

		matrix = new Hashtable<String, List<Integer>[][]>();

		int i = 0;
		for (Word w : buscados) {
			for (Documento d : w.getDocumentos()) {
				if (!matrix.containsKey(d.getNome()))
					matrix.put(d.getNome(),
							new ArrayList[buscados.size()][buscados.size()]);
				for (Ocorrencia o : d.ocorrencias) {
					if (matrix.get(d.getNome())[i][0] == null)
						matrix.get(d.getNome())[i][0] = new ArrayList<Integer>();
					matrix.get(d.getNome())[i][0].add(o.caracter);
				}
			}
			i++;
		}

		// Calcula distancia
		for (Documento d : doc) {
			for (i = 0; i < buscados.size() - 1; i++) {
				if (matrix.get(d.getNome())[i][1] == null)
					matrix.get(d.getNome())[i][1] = new ArrayList<Integer>();
				for (int k = 0; k < matrix.get(d.getNome())[i][0].size(); k++) {
					for (int m = 0; m < matrix.get(d.getNome())[i + 1][0]
							.size(); m++) {
						matrix.get(d.getNome())[i][1].add(matrix.get(d
								.getNome())[i + 1][0].get(m)
								- matrix.get(d.getNome())[i][0].get(k));
					}
				}
			}
		}

		// Realiza a ordenacao definitiva segundo a relevancia
		int menor = 0;
		boolean primeiro;
		boolean repete = true;
		boolean repeteLoop = false;
		List<Documento> docum = new ArrayList<Documento>();
		List<Documento> maiores = new ArrayList<Documento>();
		List<Documento> listAuxiliar = new ArrayList<Documento>();
		List<Documento> documAux = new ArrayList<Documento>();
		Documento atual = null;
		docum.addAll(doc);
		i = 0;
		while (repete && maiores.size() < doc.size() && i < buscados.size() - 1) {
			primeiro = true;
			for (Documento d : docum) {
				for (int k = 0; k < matrix.get(d.getNome())[i][1].size(); k++) {
					if (primeiro && matrix.get(d.getNome())[i][1].get(k) >= 0) {
						menor = matrix.get(d.getNome())[i][1].get(k);
						atual = d;
						primeiro = false;
						repete = false;
					} else if (matrix.get(d.getNome())[i][1].get(k) >= 0
							&& matrix.get(d.getNome())[i][1].get(k) <= menor) {
						if (menor == matrix.get(d.getNome())[i][1].get(k)
								&& !d.equals(atual)) {
							listAuxiliar.add(d);
							if (!listAuxiliar.contains(atual))
								listAuxiliar.add(atual);
							repete = true;
						} else
							repete = false;
						menor = matrix.get(d.getNome())[i][1].get(k);
						atual = d;
					}
				}
			}
			if (!repete) {
				if (repeteLoop) {
					docum.clear();
					docum.addAll(documAux);
					documAux.clear();
				}
				maiores.add(atual);
				docum.remove(atual);
				repete = true;
				repeteLoop = false;
				i = 0;
			} else {
				i++;
				// Salva o documento somente se e' a primeira iteracao do loop
				// de repeticao
				if (!repeteLoop)
					documAux.addAll(docum);
				docum.clear();
				// Analisa se algum documento foi selecionado pelo criterio de distancias.
				// Se nenhum foi selecionado deve-se obrigatoriamente empregar o criterio de ocorrencias.
				if(listAuxiliar.size() > 0)
					docum.addAll(listAuxiliar);
				else
					docum.addAll(documAux);
				listAuxiliar.clear();
				repeteLoop = true;
				if (i >= buscados.size() - 1) {
					for (Documento d : doc) {
						for (Documento d2 : docum)
							if (d.equals(d2))
								maiores.add(d);
					}
					i = 0;
					docum.clear();
					docum.addAll(documAux);
				}
			}
		}

		for (Documento d : doc) {
			if (!maiores.contains(d)) {
				maiores.add(d);
			}
		}
		return maiores;
	}

	/**
	 * Metodo de mneu para o resultado de busca por frase.
	 */
	private void resultMenuPhrase() {
		// Analisa se a busca foi bem sucedida
		if (buscados == null || buscados.size() == 0) {
			buscados = null;
			return;
		}
		// Analisa se a busca foi apenas de uma palavra
		// Se sim trata como uma busca por palavr
		if (buscados.size() == 1) {
			buscado = buscados.get(0);
			resultMenuWord();
			return;
		}
		// Ordena por relevancia
		List<Documento> doc = this.ordPorRel();
		int z = 1;
		for (Documento d : doc) {
			String scan = reader.getFileByTitle(d.getNome());
			List<Integer> marcacao = new ArrayList<Integer>();
			List<Integer> marcacaoExpande = new ArrayList<Integer>();
			// Coleta as marcacoes das palavras contidadas na frase para o
			// arquivo especificado
			for (int i = 0; i < buscados.size(); i++) {
				marcacao.add(matrix.get(d.getNome())[i][0].get(0));
				marcacao.add(matrix.get(d.getNome())[i][0].get(0)
						+ buscados.get(i).getPalavra().length());
			}
			Collections.sort(marcacao);

			// Expande as marcacoes para apresentar tambem o texto proximos ao
			// invez de apenas as palavras.
			Integer[] vector = new Integer[marcacao.size()];
			marcacao.toArray(vector);
			marcacaoExpande.add((betterDiff(vector[0], 50)));
			marcacaoExpande.add(vector[marcacao.size() - 1] + 50 > scan
					.length() - 1 ? scan.length() - 1
					: vector[marcacao.size() - 1] + 50);

			for (int i = 2; i < marcacao.size(); i += 2) {
				if (vector[i] - vector[i - 1] > 10) {
					marcacaoExpande.add(vector[i - 1] + 5);
					marcacaoExpande.add(vector[i] - 5);
				}
			}
			// Ordena as marcacoes
			Collections.sort(marcacaoExpande);

			// Seleciona a parte da string segundo as marcacoes
			String formata = "";
			for (int i = 0; i < marcacaoExpande.size() - 1; i += 2)
				formata += "... "
						+ scan.substring(marcacaoExpande.get(i),
								marcacaoExpande.get(i + 1)) + " ... ";
			// Forma a string de impressao, marcando todas as palavras buscadas
			// contidas na String
			formata = this.marcaByRegexp(formata, buscados);

			// Imprime
			System.out.println(z + "- " + d.nome + "\n" + formata + "\n"
					+ "--------------------------------------------------");
			z++;
		}
		int op = 1;
		Scanner sc = new Scanner(System.in);
		// Loop de escolha da opcao do texto a ser lido
		while (op > 0) {
			System.out.println("\nEscolha o texto ou (0) para sair: ");
			op = sc.nextInt();
			if (op <= 0 || op > doc.size()) {
				if (op > doc.size()) {
					System.out.println("\nOpcao invalida!");
				}
			} else {
				String text = reader.getFileByTitle(doc.get(op - 1).getNome());
				List<Integer> marcacoes = new ArrayList<Integer>();
				for (Word w : buscados) {
					for (Documento d : w.getDocumentos()) {
						if (d.getNome().compareTo(doc.get(op - 1).getNome()) == 0) {
							marcacoes.addAll(this.juntaMarcacao(d.ocorrencias,
									w));
						}
					}
				}

				text = realizaMarcacao(marcacoes, text);
				System.out
						.println("\n-------------------------------------------"
								+ "\nTitulo: "
								+ doc.get(op - 1).getNome()
								+ "\n" + text);
			}
		}
	}

	/**
	 * Marca uma pequena string formatada com as palavras passadas pela
	 * ArrayList de {@link classe.Word}
	 * 
	 * @param s
	 *            String a ser marcada pelo delimitadores.
	 * @param l
	 *            Lista das palavras que devem ser encontradas e destacadas.
	 * @return A string marcada.
	 */
	// Marca a string passada com as palavras passadas na lista.
	private String marcaByRegexp(String s, List<Word> l) {
		String tratada = ReadTextFile.normaliza(s);
		String marcada = "";
		String pattern = "\\b(" + l.get(0).getPalavra();
		for (int i = 1; i < l.size(); i++) {
			pattern += "|" + l.get(i).getPalavra();
		}
		pattern += ")\\b";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(tratada);
		int inicio = 0;
		while (m.find()) {
			marcada += s.substring(inicio, m.start()) + beginMarc
					+ s.substring(m.start(), m.end()) + endMarc;
			inicio = m.end();
		}
		marcada += s.substring(inicio);
		return marcada;
	}

	/**
	 * Realiza a marcacao da string passada utilizando a lista de numero de
	 * caracteres. Cada par de inteiros da lista representa o intervalo que
	 * contem uma palavra. Usado para marcar o texto inteiro.
	 * 
	 * @param l
	 *            lista de inteiros.
	 * @param s
	 *            string a ser marcada.
	 * @return a string marcada.
	 */
	// Realiza a marcacao da string segundo a lista de inteiros passada
	private String realizaMarcacao(List<Integer> l, String s) {
		String marcado = "";
		int index = 0;
		Collections.sort(l); // Ordena as ococorrencias para correta montagem da
								// string
		for (int i = 0; i < l.size(); i += 2) {
			marcado += s.substring(index, l.get(i)) + beginMarc
					+ s.substring(l.get(i), l.get(i + 1)) + endMarc;
			index = l.get(i + 1);
		}
		marcado += s.substring(index);
		return marcado;
	}

	/**
	 * Une as coordenadas de marcacao utilizando a ocorrencia e o tamanho da
	 * palavra a ser marcada.
	 * 
	 * @param o
	 * @param w
	 * @return lista de inteiros
	 */
	private List<Integer> juntaMarcacao(List<Ocorrencia> o, Word w) {
		List<Integer> list = new ArrayList<Integer>();
		for (Ocorrencia oc : o) {
			list.add(oc.caracter);
			list.add(oc.caracter + w.getPalavra().length());
		}
		return list;
	}

	/**
	 * Menu de resultado de pesquisa por palavra. Utiliza
	 * {@link java.util.Collections#sort(List)} para realizar a ordenacao das
	 * relevancias dos documentos.
	 */
	private void resultMenuWord() {
		if (buscado == null)
			return;
		List<Documento> doc = new ArrayList<Documento>();
		int i = 1;
		for (Documento d : buscado.getDocumentos())
			doc.add(d.clone());
		Collections.sort(doc);
		for (Documento d : doc) {
			String scan = reader.getFileByTitle(d.getNome());
			int index = d.ocorrencias.get(0).getCaracter();
			int outIndex = betterDiff(index, 50);
			String a = scan.substring(index - outIndex, index)
					+ beginMarc
					+ scan.substring(index, index
							+ buscado.getPalavra().length())
					+ endMarc
					+ scan.substring(
							index + buscado.getPalavra().length(),
							((index + buscado.getPalavra().length() + 50) >= scan
									.length() ? (scan.length() - 1) : index
									+ buscado.getPalavra().length() + 50));
			System.out.println(i + "- " + d.nome + "\tEncontrados: "
					+ d.ocorrencias.size() + "\n\"" + a + "\"\n"
					+ "--------------------------------------------------");
			i++;
		}
		int op = 1;
		Scanner scan = new Scanner(System.in);
		while (op > 0) {
			System.out.println("\nEscolha o texto ou (0) para sair: ");
			op = scan.nextInt();
			if (op <= 0 || op > doc.size()) {
				if (op > doc.size()) {
					System.out.println("\nOpcao invalida!");
				}
			} else {
				String text = reader.getFileByTitle(doc.get(op - 1).getNome());
				String textMarcado = this.marcaTexto(
						doc.get(op - 1).ocorrencias, text, buscado);
				System.out
						.println("\n-------------------------------------------"
								+ "\nTitulo: "
								+ doc.get(op - 1).getNome()
								+ "\n" + textMarcado);
			}
		}
	}

	/**
	 * Marca o texto segundo a lista de ocorrencias passada, na string de texto
	 * tambem passada segundo a palavra buscada.
	 * 
	 * @param o
	 *            lista de ocorrencias
	 * @param texto
	 * @param buscado
	 *            palavra buscada. Utiliza o tamanho da palavra juntamente com
	 *            as ocorrencias para definir as marcacoes.
	 * @return string com as marcacoes.
	 */
	private String marcaTexto(List<Ocorrencia> o, String texto, Word buscado) {
		String marcado = "";
		int index = 0;
		for (Ocorrencia oc : o) {
			marcado += texto.substring(index, oc.caracter)
					+ beginMarc
					+ texto.substring(oc.caracter, oc.caracter
							+ buscado.getPalavra().length()) + endMarc;
			index = oc.caracter + buscado.getPalavra().length();
		}
		marcado += texto.substring(index);
		return marcado;
	}

	/**
	 * Encontra a melhor diferenca entre 'a' e 'b' tal que a - b >= 0 matendo a
	 * fixo
	 */
	private int betterDiff(int a, int b) {
		while (a - b < 0) {
			b--;
		}
		return b;
	}

	/**
	 * @return referencia para a pagina invertida
	 */
	public Trie getPaginaInvertida() {
		return paginaInvertida;
	}

	/**
	 * 
	 * @param paginaInvertida
	 */
	public void setPaginaInvertida(Trie paginaInvertida) {
		this.paginaInvertida = paginaInvertida;
	}

	/**
	 * 
	 * @return ultima palavra buscada
	 */
	public Word getBuscado() {
		return buscado;
	}

	/**
	 * 
	 * @return objeto utilizado como interface leitura e pesquisa com robot.txt
	 */
	public ReadTextFile getReader() {
		return reader;
	}
}
