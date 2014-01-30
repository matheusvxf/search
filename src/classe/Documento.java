package classe;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe para representar os documentos que contem as palavras.
 * 
 * @author Matheus Venturyne Xavier Ferreira
 * @version 1.01
 * @see Trie
 * @see Word
 */
public class Documento implements Comparable<Documento> {
	/**
	 * Armazena o nome do arquivo
	 */
	String nome;
	/**
	 * ArrayList de ocorrencias de determinada palavra nesse documento.
	 */
	List<Ocorrencia> ocorrencias;

	public Documento() {
		ocorrencias = new ArrayList<Ocorrencia>();
	}

	/**
	 * Insere o nome do arquivo e o caracter de ocorrencia.
	 * 
	 * @param nome
	 *            nome do arquivo
	 * @param caracter
	 *            numero do caracter em que ocorre a ocorrencia.
	 */
	public Documento(String nome, int caracter) {
		this();
		this.nome = nome;
		this.ocorrencias = new ArrayList<Ocorrencia>();
		this.ocorrencias.add(new Ocorrencia(caracter));
	}

	public void addOcorrencia(Ocorrencia a) {
		ocorrencias.add(a);
	}

	public String getNome() {
		return nome;
	}

	public List<Ocorrencia> getOcorrencias() {
		return ocorrencias;
	}

	/**
	 * Clona o documento.
	 */
	public Documento clone() {
		Documento retorno = new Documento();
		retorno.nome = nome;
		for (Ocorrencia a : ocorrencias)
			retorno.ocorrencias.add(a.clone());
		return retorno;
	}

	/**
	 * Compara se dois documentos a partir do numero de ocorrencias nesse
	 * documento.
	 * 
	 * @return -1 se o numero de correncias e menor, 0 se igual, 1 se maior.
	 */
	@Override
	public int compareTo(Documento arg0) {
		if (this.ocorrencias.size() > arg0.ocorrencias.size())
			return -1;
		if (this.ocorrencias.size() < arg0.ocorrencias.size())
			return 1;
		return 0;
	}

	/**
	 * Compara se dois arquivos sao iguais a partir do nome.
	 * 
	 * @return verdadeiro se iguais, falso caso contrario.
	 */
	public boolean equals(Object ob) {
		Documento d = (Documento) ob;
		return this.getNome().compareTo(d.getNome()) == 0;
	}
}