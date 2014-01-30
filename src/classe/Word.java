package classe;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe que representa o armazenamento de uma palavra na pagina invertida.
 * Possui uma lista de documentos que essa palavra esta inserida. Cada elemento
 * nessa lista de documentos possui uma lista de ocorrencias dessa palavra no
 * respectivo documento.
 * 
 * @author Matheus Venturyne Xavier Ferreira
 * @version 1.01
 * @see classe.Documento
 * @see classe.Ocorrencia
 * @see java.util.ArrayList
 */
public class Word {
	/**
	 * Atributo que armazena a palavra (String)
	 */
	private String palavra;
	/**
	 * <code>ArrayList</code> dos documentos
	 */
	private List<Documento> documentos;

	/**
	 * Inicializa a palavra.
	 */
	public Word() {
		this.palavra = "";
		this.documentos = new ArrayList<Documento>();
	}

	/**
	 * Inicializa a palavra a partir de uma string passada.
	 * 
	 * @param palavra
	 */
	public Word(String palavra) {
		this();
		this.palavra = palavra;
	}

	/**
	 * Inicializa a palavra com a String palavra, o nome do documento que ela
	 * esta presente e a posicao da ocorrencia dessa palavra.
	 * 
	 * @param palavra
	 * @param documento
	 * @param caracter
	 */
	public Word(String palavra, String documento, int caracter) {
		this();
		this.palavra = palavra;
		this.documentos.add(new Documento(documento, caracter));
	}

	/**
	 * Cria uma palavra clonando a palavra passada.
	 * 
	 * @param a
	 *            palavra a ser clonada.
	 */
	public Word(Word a) {
		this();
		palavra = a.palavra;
		for (Documento d : a.documentos)
			documentos.add(d.clone());
	}

	/**
	 * Retorna o numero do array do Node para o caracter de posicao na palavra
	 * passado.
	 * 
	 * @param index
	 * @return um inteiro correspondente a posicao de 0 a 26 do alfabeto q
	 *         corresponde ao iesimo caracter da palavra.
	 */
	public int getIndex(int index) {
		if (index >= palavra.length())
			return -1;
		return palavra.codePointAt(index) - "a".codePointAt(0);
	}

	/**
	 * 
	 * @return lista de documentos que contem essa palavra.
	 */
	public List<Documento> getDocumentos() {
		return documentos;
	}

	/**
	 * 
	 * @param documentos
	 */
	public void setDocumentos(List<Documento> documentos) {
		this.documentos = documentos;
	}

	/**
	 * Clona esse objeto.
	 */
	public Word clone() {
		return new Word(this);
	}

	/**
	 * Compara duas palavras
	 * 
	 * @param registro
	 *            Palavra a ser comparada com o objeto.
	 * @return -1 se alfabeticamente menor, 0 se iguais, 1 se alfabeticamente
	 *         maior.
	 */
	public int compareTo(Word registro) {
		return registro.palavra.compareTo(this.palavra);
	}

	/**
	 * Realiza multipla insercao de palavras na palavra atual. Insere a palavra
	 * passada por parametro na palavra atual recolhendo o seu documento e o
	 * numero da ocorrencia nesse documento. Analisa se a palavra a ser inserida
	 * ja nao esta registrada nessa palavra (condicao de igualdade: possui o
	 * mesmo documento sendo uma das ocorrencias igual)
	 * 
	 * @param reg
	 *            Word a ser mesclada com o objeto atual.
	 * @return verdadeiro se inserido, falso caso contrario.
	 */
	public boolean multipleInsertion(Word reg) {
		if (this.palavra.compareTo(this.palavra) != 0)
			return false;
		for (Documento d : documentos) {
			if (d.nome.compareTo(reg.documentos.get(0).nome) == 0) {
				for (Ocorrencia o : documentos.get(0).ocorrencias)
					if (o.caracter == reg.documentos.get(0).ocorrencias.get(0).caracter)
						return false;
				d.ocorrencias.add(reg.documentos.get(0).ocorrencias.get(0));
				return true;
			}
		}
		documentos.add(reg.documentos.get(0).clone());
		return true;
	}

	/**
	 * Retorna a string correspondente a essa palavra.
	 */
	public String toString() {
		return palavra;
	}

	/**
	 * Adiciona um documento a essa palavra
	 * 
	 * @param a
	 *            Objeto documento.
	 */
	public void addDocumento(Documento a) {
		documentos.add(a);
	}

	/**
	 * Retorna a palavra representada por esse objeto.
	 * 
	 * @return palavra representada pelo objeto.
	 */
	public String getPalavra() {
		return palavra;
	}
}