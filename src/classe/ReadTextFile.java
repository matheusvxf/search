package classe;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe de interface de leitura com os arquivos contidos na lista robots.txt.
 * 
 * @author Matheus Venturyne Xavier Ferreira
 * @version 1.01
 * @see java.util.ArrayList
 * @see java.util.Scanner
 * @see java.io.File
 * @see java.util.regex.Matcher
 * @see java.util.regex.Pattern
 */
public class ReadTextFile {
	/**
	 * Armazena o objeto file correspondente a robots.
	 */
	private final File lista = new File("Files/robots.txt");
	/**
	 * Armazena os caracteres a serem substituidos (remover acentos).
	 */
	public static String[] REPLACES = { "a", "e", "i", "o", "u", "c", "A", "E",
			"I", "O", "U", "C" };
	/**
	 * Array para os patterns da regex
	 */
	public static Pattern[] PATTERNS = null;
	/**
	 * Manipulador do arquivo de lista de arquivos (robots)
	 */
	Scanner handleList;
	/**
	 * Manipulador dos arquivos a serem lidos
	 */
	Scanner handleFile;
	/**
	 * Lista do caminho no disco dos arquivos lidos
	 */
	List<String> textos;

	/**
	 * Cria o handleList para a lista robots.txt.
	 */
	public ReadTextFile() {
		try {
			handleList = new Scanner(lista);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		textos = new ArrayList<String>();
	}

	/**
	 * Abre o proximo arquivo da lista.
	 * 
	 * @return o nome do arquivo lido.
	 */
	public String openNextFile() {
		String file = nextFileName();
		File nome = new File("Files/" + file);
		textos.add(nome.getAbsolutePath());
		try {
			handleFile = new Scanner(nome);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * Le a proxima linha do arquivo atual.
	 * 
	 * @return a linha.
	 */
	public String readNextFileLine() {
		return normaliza(handleFile.nextLine()) + "\n";
	}

	/**
	 * Compila as patterns para remocao das acentuacoes.
	 */
	private static void compilePatterns() {
		PATTERNS = new Pattern[REPLACES.length];
		PATTERNS[0] = Pattern.compile("[âãáàä]");
		PATTERNS[1] = Pattern.compile("[éèêë]");
		PATTERNS[2] = Pattern.compile("[íìîï]");
		PATTERNS[3] = Pattern.compile("[óòôõö]");
		PATTERNS[4] = Pattern.compile("[úùûü]");
		PATTERNS[5] = Pattern.compile("[ç]");
		PATTERNS[6] = Pattern.compile("[ÂÃÁÀÄ]");
		PATTERNS[7] = Pattern.compile("[ÉÈÊË]");
		PATTERNS[8] = Pattern.compile("[ÍÌÎÏ]");
		PATTERNS[9] = Pattern.compile("[ÓÒÔÕÖ]");
		PATTERNS[10] = Pattern.compile("[ÚÙÛÜ]");
		PATTERNS[11] = Pattern.compile("[Ç]");
	}

	/**
	 * Substitui os caracteres acentuados por nao acentuados. Substitui tudo que
	 * nao é alfabético por espacos em branco. Utiliza as expressoes regulares
	 * compiladas em {@link ReadTextFile#PATTERNS} para remover os acentos. Em
	 * seguida utiliza duas expressoes regulares, uma para substituir todos os
	 * caracteres nao alfabeticos por espacoes em brancos e outra para
	 * substituir todos os caracteres que nao sao da tabelas ASCII para espacos
	 * em branco.
	 * 
	 * @param text
	 *            cadeia de caracter a ser tratada.
	 * @return a cadeia de caracter tratada.
	 */
	public static String normaliza(String text) {
		if (PATTERNS == null) {
			compilePatterns();
		}

		String result = text;
		for (int i = 0; i < PATTERNS.length; i++) {
			Matcher matcher = PATTERNS[i].matcher(result);
			result = matcher.replaceAll(REPLACES[i]);
		}
		result = result.replaceAll("[^\\p{Alpha}]", " ");
		result = result.replaceAll("[^\\p{ASCII}]", " ");
		result = result.toLowerCase();
		return result;
	}

	/**
	 * Le o proximo arquivo inteiro.
	 * 
	 * @return uma string contendo todo o texto.
	 */
	public String readNextFile() {
		String retorno = "";
		while (handleFile.hasNext()) {
			retorno += readNextFileLine();
		}
		return retorno;
	}

	/**
	 * Retorna o nome do proximo arquivo da lista.
	 * 
	 * @return o nome do arquivo
	 */
	public String nextFileName() {
		if (handleList.hasNext())
			return handleList.nextLine();
		return null;
	}

	/**
	 * @return verdadeiro se ha proximo arquivo na lista, falso caso contrario.
	 */
	public boolean hasNextFile() {
		return handleList.hasNext();
	}

	/**
	 * Le o arquivo com nome passado. Esse arquivo deve ter sido lido
	 * anteriormente e salvo na String de lista de arquivos lidos.
	 * 
	 * @param nome
	 *            nome do arquivo a ser lido.
	 * @return string contendo todo o arquivo.
	 */
	public String readFile(String nome) {
		Scanner handle = null;
		String retorno = "";
		try {
			handle = new Scanner(new File(nome));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while (handle.hasNext())
			retorno += handle.nextLine() + "\n";
		return retorno;
	}

	/**
	 * @param i
	 *            o indice do iesimo arquivo lido
	 * @return o caminho para o iesimo arquivo lido.
	 */
	public String getTextos(int i) {
		return textos.get(i);
	}

	/**
	 * Retorna todo o arquivo de nome passado como uma string normalizada (sem
	 * acentos e somente com caracteres alfabéticos). Utiliza uma expressao
	 * regular para encontrar o arquivo pelo titulo dentro da lista de arquivos
	 * com caminho completo. Dessa forma pode-se passar apenas o titulo do
	 * arquivo a ser buscado e nao seu caminho completo.
	 * 
	 * @return todo o texto normalizado.
	 */
	public String getFileByTitle(String t) {
		Pattern p = Pattern.compile(t + "$");

		for (String te : textos) {
			Matcher m = p.matcher(te);
			if (m.find())
				return readFile(te);
		}
		return null;
	}
}
