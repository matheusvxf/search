package classe;

/**
 * Contem a estrutura de armazenamento da Trie. Possui um array de 26
 * posicoes(padrao) uma cada letra. Um atributo que armazena a palavra em caso
 * de ela nao estar completa e um para armazenar a palavra caso o Node atual
 * tenha gerado o caminho completo para a palavra. Uma palavra quando completa
 * seu caminho deve ir para esse segundo atributo e removida do atributo de
 * palavras de caminho nao completo.
 * 
 * @author Matheus Venturyne Xavier Ferreira
 * @version 1.01
 * @see classe.Trie
 * @see Word
 */
public class Node {
	/**
	 * Array com os caminhos a partir das letras.
	 */
	private Node array[];
	/**
	 * Word que armazena palavras de caminho nao completo.
	 */
	private Word registro;
	/**
	 * Word que armazena palavras de caminho completo.
	 */
	private Word registroCompleto;
	/**
	 * Numero de caminhos a ser inicializado para cada Node.
	 */
	private final int path = 26;

	/**
	 * Inicializa um node padrao.
	 */
	public Node() {
		registro = null;
		registroCompleto = null;
		array = new Node[path];
	}

	/**
	 * Inicializa um node ja inserindo uma palavra no registro de caminho nao
	 * completo.
	 * 
	 * @param reg
	 *            Word a ser inserida.
	 */
	public Node(Word reg) {
		this();
		registro = reg.clone();
	}

	/**
	 * Metodo para inserir as palavras no Node. Primeiramente analiza se o
	 * caminho foi completo. Se sim invoca {@link Node#insert(Word)} para fazer
	 * uma multipla insercao (mesclar o registro do node atual com o registro do
	 * node a ser inserido). Caso contrario Analisa se o registro do node atual
	 * nao e nulo. Se nao e nulo compara o registro de caminho nao completo com
	 * a palavra a ser inserido, se iguais novamente faz uma multiplainsercao
	 * nesse registro mesclando o registro com a a palavra a ser inserido. Caso
	 * nao seja igual invoca o metodo {@link Node#insert(Word, Word, int)},
	 * removendo o registro de caminho nao completo do node atual e encarrega o
	 * metodo chamado de fazer a insercao (fazer a arvore crescer ate que os
	 * caminhos dos registro se difiram). Em caso de o registro ser nulo,
	 * indicando que a mais caminho a ser percorrido, incrementa o atributo que
	 * indica altura atual do node e invoca o metodo novamente de forma
	 * recursiva.
	 * 
	 * @param reg
	 *            registro a ser inserido.
	 * @param i
	 *            altura do node atual.
	 * @return verdadeiro se a insercao foi bem sucedida, falso caso contrario.
	 */
	public boolean insert(Word reg, int i) {
		int indice = reg.getIndex(i);
		if (indice < 0) {
			return this.insert(reg);
		}
		if (!this.registroIsNull()) {
			if (reg.compareTo(this.getRegistro()) == 0) { // Registro ja
															// inserido
				return this.registro.multipleInsertion(reg);
			}
			Word aux = registro;
			registro = null;
			this.insert(reg, aux, i);

		} else {
			i++;
			if (this.array[indice] == null) {
				this.array[indice] = new Node();
				if (reg.getIndex(i) < 0)
					this.array[indice].insert(reg);
				else
					this.array[indice].registro = reg.clone();
			} else
				return this.array[indice].insert(reg, i);

		}
		return true;
	}

	/**
	 * Metodo para inserir um No caso o percurso esteja completo. Analisa se o
	 * registroCompleto nao e' nulo nesse caso fazendo uma multipla insercao
	 * invocando o metodo {@link Word#multipleInsertion(Word)}. Caso contrario
	 * cria um novo objeto Word a ser atribuido em {@link Node#registroCompleto}
	 * com um clone da palavra a ser inserida.
	 * 
	 * @param reg
	 *            registro a ser inserido
	 * @return verdadeiro se a insercao foi bem sucedida, falso caso contrario.
	 */
	private boolean insert(Word reg) {
		if (this.registroCompleto != null)
			// Invoca o metodo do registro palavra para realizar multiplica
			// insercao de uma palavra
			return this.registroCompleto.multipleInsertion(reg);
		this.registroCompleto = new Word(reg);
		return true;
	}

	/**
	 * Metodo para expandir a arvore quando e' preciso dividir o caminho na
	 * arvore. Primeiramente recebe os indices para o caminho a ser seguido para
	 * a proxima letra da palavra utilizando da altura da arvore e o metodo
	 * {@link Word#getIndex(int)} para obter o indice para essa letra. <br/ >
	 * <br/ >
	 * 
	 * Primeiro analisa se o proximo indice retornado e menor que 0 indicando um
	 * caminho completo. Nesse caso revoca a insercao para
	 * {@link Node#insert(Word)} responsavel pelas multiplas insercoes, para os
	 * nodes nessa situacao. Caso ambos os nodes sejam de registro completo
	 * retorna e fim de insercao. Caso contrario o node de caminho incompleto e
	 * inserido revogando a funcao para o metodo {@link Node#insert(Word, int)}
	 * para os nodes de caminho incompleto e novamente fim de insercao. Caso nao
	 * haja nenhum node de multipla insercao a altura do node e' incrementada.
	 * Nesse caso e analisado se o caminho a ser seguido pelas duas palavra e'
	 * diferente. Nesse caso a insercao e revogada para o metodo
	 * {@link Node#insert(Word, int)} para cada uma das palavras. Caso sigam um
	 * caminho igual e o caso de crescimento da arvore, entao o metodo e
	 * novamente chamado de forma recursiva para o node subsequente.
	 * 
	 * @param reg
	 *            registro inicialmente a ser inserido.
	 * @param aux
	 *            registro removida que precisa ser re-alocado na arvore.
	 * @param i
	 *            altura do node atual.
	 */
	private void insert(Word reg, Word aux, int i) {
		int indReg = reg.getIndex(i);
		int indAux = aux.getIndex(i);
		if (indReg < 0) {
			insert(reg);
		}
		if (indAux < 0) {
			insert(aux);
		}
		if (indReg < 0 && indAux < 0)
			return;
		if (indReg < 0) {
			insert(aux, i);
			return;
		}
		if (indAux < 0) {
			insert(reg, i);
			return;
		}
		i++;
		if (indReg != indAux) {
			this.array[indReg] = new Node();
			this.array[indAux] = new Node();
			this.array[indReg].insert(reg, i);
			this.array[indAux].insert(aux, i);
		} else {
			this.array[indReg] = new Node();
			this.array[indReg].insert(reg, aux, i);
		}
	}

	public void setRegistroCompleto(Word reg) {
		registroCompleto = reg;
	}

	public void setRegistro(Word reg) {
		registro = reg;
	}

	/**
	 * Analisa se o registro e nulo.
	 * 
	 * @return verdadeiro se nulo, falso caso contrario.
	 */
	public boolean registroIsNull() {
		if (registro == null)
			return true;
		return false;
	}

	public Word getRegistro() {
		return registro;
	}

	public Node getArrayIndex(int indice) {
		return array[indice];
	}

	public Word getRegistroCompleto() {
		return registroCompleto;
	}
}