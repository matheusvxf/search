package classe;

/**
 * Representa pagina invertida para o armazenamento das palavras. Contem o
 * primeiro Node da arvore Trie. Por padrao o primeiro Node e inicializado no
 * construtor, e nao armazenara palavras, apenas fornece o caminho para as
 * insercoes.
 * 
 * @author Matheus Venturyne Xavier Ferreira
 * @version 1.01
 * @see classe.Node
 * @see classe.Word
 * @see classe.Documento
 * @see classe.Ocorrencia
 */
public class Trie {
	/**
	 * Atributo raiz.
	 */
	private Node root;

	/**
	 * Inicializa a raiz.
	 */
	public Trie() {
		root = new Node();
	}

	/**
	 * Metodo para inserir na trie que chama o inserir da classe Node. Despacha
	 * a insercacao para o metodo insercacao da classe Node
	 * {@link Node#insert(Word, int)}. Inicialmente declara um atributo para
	 * contar a altura em que se encontra a recursao em relacao a raiz para
	 * controlar qual caracter deve ser analisado na palavra para determinar o
	 * caminho de descida na Trie.
	 * 
	 * @param reg
	 *            Registro Word a ser inserido.
	 * @return Verdadeiro se a insercacao foi bem sucedida, falso caso
	 *         contrario.
	 */
	public boolean put(Word reg) {
		int i = 0;
		return root.insert(reg, i);
	}

	/**
	 * Metodo de buscar na trie. Prepara os atributos e envia para o metodo de
	 * pesquisa {@link Trie#getTrie(Word, Node, int)}. Inicialmente declara um
	 * atributo para contar a altura em que se encontra a recursao em relacao a
	 * raiz para controlar qual caracter deve ser analisado na palavra para
	 * determinar o caminho de descida na Trie.
	 * 
	 * @param reg
	 *            Registro a ser buscado na Trie. Por definicao esse registro
	 *            apenas utiliza o atributo que representa a palavra para ser
	 *            buscado, outros atributos como lista de documentos e
	 *            ocorrencias e' desconsiderado na busca.
	 * @return O registro contido na arvore trie que contem a palavra com sua
	 *         lista de documentos e ocorrencias nos documentos.
	 */
	public Word get(Word reg) {
		int i = 0;
		return getTrie(reg, root, i);
	}

	/**
	 * Metodo responsavel por varer a trie buscando palavras.
	 * 
	 * @param reg
	 *            Palavra a ser buscada
	 * @param node
	 *            Node atual em que esta sendo buscado
	 * @param i
	 *            Altura da arvore.
	 * @return O registro da Trie em que a palavra foi encontrada
	 */
	public Word getTrie(Word reg, Node node, int i) {
		int indice = reg.getIndex(i);
		if (node == null) {
			return null;
		}
		if (node.getRegistro() != null) {
			if (reg.compareTo(node.getRegistro()) == 0) { // Registro encontrado
				return node.getRegistro().clone();
			}
		}
		if (node.getRegistroCompleto() != null && indice < 0) {
			if (reg.compareTo(node.getRegistroCompleto()) == 0)
				return node.getRegistroCompleto().clone();
		}
		if (indice >= 0) {
			++i;
			return getTrie(reg, node.getArrayIndex(indice), i);
		}
		return null;
	}
}
