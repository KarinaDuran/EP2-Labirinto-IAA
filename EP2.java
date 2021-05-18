/**************************************************/
/* Karina Duran Munhos				  */
/**************************************************/
import java.io.*;
import java.util.*;
import java.util.Arrays;

class Item {

	private int lin, col, value, weight;

	public Item(String s) {

		String[] parts = s.split(" +");
		lin = Integer.parseInt(parts[0]);
		col = Integer.parseInt(parts[1]);
		value = Integer.parseInt(parts[2]);
		weight = Integer.parseInt(parts[3]);
	}

	public Item(int lin, int col, int value, int weight) {

		this.lin = lin;
		this.col = col;
		this.value = value;
		this.weight = weight;
	}

	public int getLin() {

		return lin;
	}

	public int getCol() {

		return col;
	}

	public int[] getCoordinates() {

		return new int[] { getLin(), getCol() };
	}

	public int getValue() {

		return value;
	}

	public int getWeight() {

		return weight;
	}

	public String toString() {

		return "Item: coordinates = (" + getLin() + ", " + getCol() + "), value = " + getValue() + " weight = "
				+ getWeight();
	}
}

// le o mapa
class Map {

	public static final char FREE = '.';

	private char[][] map;
	private Item[] items;
	private int nLin, nCol, nItems, startLin, startCol, endLin, endCol, size;

	// Abre o arquivo e converte em uma matriz, com os tamanhos especificados no
	// inicio
	public Map(String fileName) {

		try {

			BufferedReader in = new BufferedReader(new FileReader(fileName));

			Scanner scanner = new Scanner(new File(fileName));

			nLin = scanner.nextInt();
			nCol = scanner.nextInt();

			map = new char[nLin][nCol];
			size = 0;

			for (int i = 0; i < nLin; i++) {

				String line = scanner.next();

				for (int j = 0; j < nCol; j++) {

					map[i][j] = line.charAt(j);

					if (free(i, j))
						size++;
				}
			}

			nItems = scanner.nextInt();
			items = new Item[nItems];

			for (int i = 0; i < nItems; i++) {

				items[i] = new Item(scanner.nextInt(), scanner.nextInt(), scanner.nextInt(), scanner.nextInt());
			}

			startLin = scanner.nextInt();
			startCol = scanner.nextInt();
			endLin = scanner.nextInt();
			endCol = scanner.nextInt();
		} catch (IOException e) {

			System.out.println("Error loading map... :(");
			e.printStackTrace();
		}
	}

	public void print() {

		System.out.println("Map size (lines x columns): " + nLin + " x " + nCol);

		for (int i = 0; i < nLin; i++) {

			for (int j = 0; j < nCol; j++) {

				System.out.print(map[i][j]);
			}

			System.out.println();
		}

		System.out.println("Number of items: " + nItems);

		for (int i = 0; i < nItems; i++) {

			System.out.println(items[i]);
		}
	}

	// Verifica se o movimento pode ser tomado
	public boolean blocked(int lin, int col) {

		return !free(lin, col);
	}

	public boolean free(int lin, int col) {

		return map[lin][col] == FREE;
	}

	// Marca o passo
	public void step(int lin, int col) {

		map[lin][col] = '*';
	}

	// Desmarca o passo
	public void unstep(int lin, int col) {
		map[lin][col] = FREE;
	}

	// Verifica se acabou
	public boolean finished(int lin, int col) {

		return (lin == endLin && col == endCol);
	}

	public int getStartLin() {

		return startLin;
	}

	public int getStartCol() {

		return startCol;
	}

	public int getItens() {
		return nItems;
	}

	public int getSize() {

		return size;
	}

	public int nLines() {

		return nLin;
	}

	public int nColumns() {

		return nCol;
	}

	public Item getItem(int lin, int col) {

		for (int i = 0; i < items.length; i++) {

			Item item = items[i];

			if (item.getLin() == lin && item.getCol() == col)
				return item;
		}

		return null;
	}
}

public class EP2 {
	public static final boolean DEBUG = false;

	// Metodo auxiliar que retorna o tempo gasto em um determinado caminho
	public static double calculaTempo(Map map, int[] vetor) {
		double peso = 0;
		// Variavel de retorno contendo tempo
		double aux = 0;
		if (vetor[0] == 3)
			return Math.pow(1 + (peso / 10), 2);
		// Verifica cada conjunto de linha e coluna do caminho se ha um item
		for (int i = 1; i < vetor[0] - 2; i += 2) {
			Item x = map.getItem(vetor[i], vetor[i + 1]);
			if (x != null)
				peso += x.getWeight();
			aux += Math.pow(1 + (peso / 10), 2);
		}
		return aux;
	}

	// Funcao auxilar que retorna o valor dos itens acumulados por um determinado
	// caminho
	public static int coleta(Map map, int[] vetor) {
		int i = 1;
		int valor = 0;
		while (i < vetor[0]) {
			Item x = map.getItem(vetor[i], vetor[i + 1]);
			if (x != null)
				valor += x.getValue();
			i += 2;
		}
		return valor;
	}

	public static int[] findPath(Map map, int criteria, int lin, int col) {
		// Aloca espaco para os possiveis caminhos e para um que sera o caminho certo de
		// acordo com o criterio escolhido
		int[] caminhoCerto = new int[2 * map.getSize()];
		int[] solucaoCima = new int[2 * map.getSize()];
		int[] solucaoDireita = new int[2 * map.getSize()];
		int[] solucaoBaixo = new int[2 * map.getSize()];
		int[] solucaoEsquerda = new int[2 * map.getSize()];

		// Determina o index e aloca o espaco para o vetor que sera retornado (path)
		int path_index = 1;
		int[] path;
		path = new int[2 * map.getSize()];

		// Adicionamos as coordenadas da posição (lin, col) no path
		path[path_index] = lin;
		path[path_index + 1] = col;
		path_index += 2;
		// Recebe o tamanho do vetor
		path[0] = path_index;

		// Efetivação de um passo
		// Marcamos no mapa que a posição está sendo ocupada.
		map.step(lin, col);

		// Se tiver encontrado a posicao final, desmarca o ultimo passo e retorna a
		// posicao final
		if (map.finished(lin, col)) {
			map.unstep(lin, col);
			return path;
		}
		// Variaveis auxiliares de acordo com o criterio:
		// Auxiliar para as chamadas de outros metodos
		double tempo;
		// Auxiliar para o criterio 1 (menor caminho)
		int menorCaminho = 2 * map.getSize();
		// Auxiliar para o criterio 2 (maior caminho)
		int maiorCaminho = 0;
		// Auxiliar para o criterio 3 (caminho mais valioso)
		int valor = -1;
		// Auxiliar para o criterio 4 (caminho mais rapido)
		double maiorTempo = -1;

		// Auxiliar que recebera o caminho certo a ser seguido
		// 1 para solucaoCima, 2 para solucaoDireita, 3 para solucaoBaixo e 4 para
		// solucaoEsquerda
		int aux = 0;

		// Verifica se eh possivel ir para determinado caminho e se sim, realiza a
		// chamada recursiva
		// Então, verifica-se se eh ou nao o caminho candidato a ser o certo.
		// Compara-se criterios de cada caminho com as variaveis auxiliares, dependendo
		// do criterio
		// escolhido

		// No caso de criterio 3, chama a funcao auxiliar coleta, que retorna o valor
		// coletado nesse caminho
		// No caso de criterio 4, chama a funcao calculaTempo que retorna o tempo gasto
		// pelo caminho

		// Caso o caminho nao encontre uma saida, retorna null, sendo considerado,
		// portanto, caminho inviavel

		// Cima
		if (lin - 1 >= 0 && map.free(lin - 1, col)) {
			solucaoCima = findPath(map, criteria, lin - 1, col);
			if (solucaoCima != null) {
				tempo = calculaTempo(map, solucaoCima);
				if (criteria == 1 && solucaoCima[0] < menorCaminho) {
					menorCaminho = solucaoCima[0];
					aux = 1;
				} else if (criteria == 2 && solucaoCima[0] > maiorCaminho) {
					maiorCaminho = solucaoCima[0];
					aux = 1;
				} else if (criteria == 3 && coleta(map, solucaoCima) > valor) {
					valor = coleta(map, solucaoCima);
					aux = 1;
				} else if (criteria == 4 && (maiorTempo == -1 || tempo <= maiorTempo)) {
					maiorTempo = tempo;
					aux = 1;
				}
			}
		}

		// Direita
		if (col + 1 < map.nColumns() && map.free(lin, col + 1)) {
			solucaoDireita = findPath(map, criteria, lin, col + 1);
			// limpar pro proximo
			if (solucaoDireita != null) {
				tempo = calculaTempo(map, solucaoDireita);
				if (criteria == 1 && solucaoDireita[0] <= menorCaminho) {
					menorCaminho = solucaoDireita[0];
					aux = 2;
				} else if (criteria == 2 && solucaoDireita[0] >= maiorCaminho) {
					maiorCaminho = solucaoDireita[0];
					aux = 2;
				} else if (criteria == 3 && coleta(map, solucaoDireita) >= valor) {
					valor = coleta(map, solucaoDireita);
					aux = 2;
				} else if (criteria == 4 && (maiorTempo == -1 || tempo <= maiorTempo)) {
					maiorTempo = tempo;
					aux = 2;
				}
			}
		}

		// Baixo
		if (lin + 1 < map.nLines() && map.free(lin + 1, col)) {
			solucaoBaixo = findPath(map, criteria, lin + 1, col);
			if (solucaoBaixo != null) {
				tempo = calculaTempo(map, solucaoBaixo);
				if (criteria == 1 && solucaoBaixo[0] <= menorCaminho) {
					menorCaminho = solucaoBaixo[0];
					aux = 3;
				} else if (criteria == 2 && solucaoBaixo[0] >= maiorCaminho) {
					maiorCaminho = solucaoBaixo[0];
					aux = 3;
				} else if (criteria == 3 && coleta(map, solucaoBaixo) >= valor) {
					valor = coleta(map, solucaoBaixo);
					aux = 3;
				} else if (criteria == 4 && (maiorTempo == -1 || tempo <= maiorTempo)) {
					maiorTempo = tempo;
					aux = 3;
				}
			}
		}
		// Esquerda
		if (col - 1 >= 0 && map.free(lin, col - 1)) {
			solucaoEsquerda = findPath(map, criteria, lin, col - 1);
			if (solucaoEsquerda != null) {
				tempo = calculaTempo(map, solucaoEsquerda);
				if (criteria == 1 && solucaoEsquerda[0] < menorCaminho) {
					aux = 4;
				} else if (criteria == 2 && solucaoEsquerda[0] >= maiorCaminho) {
					aux = 4;
				} else if (criteria == 3 && coleta(map, solucaoEsquerda) >= valor) {
					aux = 4;
				} else if (criteria == 4 && (maiorTempo == -1 || tempo <= maiorTempo)) {
					aux = 4;
				}
			}
		}

		// Desmarca o passo para proximos caminhos talvez usarem essa posicao
		map.unstep(lin, col);

		// Verifica qual o caminho certo e entao, atribui ao vetor auxiliar caminhoCerto
		// o caminho
		// Se for 0, indica que nao ha mais caminhos a se seguir a partir daquele ponto
		// e nao encontrou
		// a saida, sendo assim retorna null;
		if (aux == 0)
			return null;
		else if (aux == 1)
			caminhoCerto = solucaoCima;
		else if (aux == 2)
			caminhoCerto = solucaoDireita;
		else if (aux == 3)
			caminhoCerto = solucaoBaixo;
		else if (aux == 4)
			caminhoCerto = solucaoEsquerda;

		// Junta o vetor caminhoCerto com path (vetor de retorno)
		for (int j = 1; j < caminhoCerto[0]; j++) {
			path[path_index] = caminhoCerto[j];
			path_index++;
		}

		// Atribui a path [0] o tamanho atualizado
		path[0] = path_index;

		// Retorna o caminho percorrido
		return path;
	}

	public static void printSolution(Map map, int[] path) {

		// A partir do mapa e do path contendo a solução, imprime a saída conforme
		// especificações do EP.
		int totalItems = 0;
		int totalValue = 0;
		int totalWeight = 0;
		int j = 0;
		// Vetor auxiliar para armazenar as coordenadas dos itens
		int[] itens = new int[2 * map.getItens()];

		int path_size = path[0];

		// Calcula o tempo do caminho
		double tempo = calculaTempo(map, path);

		// Imprime o numero de passos dados e o tempo
		System.out.println((path_size - 1) / 2 + " " + tempo);

		for (int i = 1; i < path_size; i += 2) {

			int lin = path[i];
			int col = path[i + 1];
			Item item = map.getItem(lin, col);

			System.out.println(lin + " " + col);

			if (item != null) {

				totalItems++;
				totalValue += item.getValue();
				totalWeight += item.getWeight();
				// Salva no vetor auxiliar as coordenadas de cada item
				itens[j] = item.getLin();
				itens[j + 1] = item.getCol();
				j += 2;

			}
		}
		// Imprime o total de itens, o valor total e o peso
		System.out.println(totalItems + " " + totalValue + " " + totalWeight);
		// Imprime as coordenadas dos itens
		for (int i = 0; i < (2 * totalItems) - 1; i += 2) {
			System.out.println(itens[i] + " " + itens[i + 1]);
		}

	}

	public static void main(String[] args) throws IOException {

		Map map = new Map(args[0]);

		if (DEBUG) {
			map.print();
			System.out.println("---------------------------------------------------------------");
		}

		int criteria = Integer.parseInt(args[1]);
		int lin = map.getStartLin();
		int col = map.getStartCol();

		int[] path = findPath(map, criteria, lin, col);
		printSolution(map, path);
	}
}
