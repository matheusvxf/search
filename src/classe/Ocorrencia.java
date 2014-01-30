package classe;

/**
 * @author Matheus Venturyne Xavier Ferreira
 * @version 1.01
 */

/**
 * Classe para ocorrencia das palavras no arquivo correspondente. Possui apenas
 * o numero do caracter.
 */
public class Ocorrencia {
	/**
	 * Posicao do caracter dessa ocorrencia
	 */
	int caracter;

	public Ocorrencia() {
		caracter = 0;
	}

	public Ocorrencia(int caracter) {
		this.caracter = caracter;
	}

	public Ocorrencia clone() {
		return new Ocorrencia(caracter);
	}

	public int getCaracter() {
		return caracter;
	}

	public void setCaracter(int caracter) {
		this.caracter = caracter;
	}
}