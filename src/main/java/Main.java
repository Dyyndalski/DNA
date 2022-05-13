import java.io.File;
import java.util.*;

public class Main {

    final static int LENGTH_OF_SEQ = 500;
    final static double ALPHA = 1.0d;
    final static double BETA = 1.0d;

    public static void main(String[] args) {
        List<String> strings = readFromFile("src/main/resources/Data.txt");
        int wordSize = strings.get(0).length();
        int size = strings.size();
        int[][] costMatrix = getCostMatrix(strings);
        double[][] feromonMatrix = startFillFeromonArray(size);

        List<Ant> ants = new ArrayList<>();
        Random random = new Random();

        for (int z = 0; z < 100; z++) {
            for (int i = 0; i < 10; i++) {
                int randomStartVertex = random.nextInt(size);
                Ant mrufka = mrufka(costMatrix, feromonMatrix, randomStartVertex, wordSize);

                ants.add(mrufka);

                //FUNCTION TO DRAW SEQUNCES ONE UNDER ANOTHER
                drawWords(mrufka.getHistory(), costMatrix, strings);
            }

            //FUNCTION TO CREATE RANKING OF ANTS - FIRST PLACE HAVE ANT WHICH HAVE THE LONGEST LETTER IN SEQUENCE
            //usunac komentarz zeby zobaczyc co i jak (nizej)
            List<Ant> sortedAntsByLength = countRanking(ants);
//        sortedAntsByLength.stream().forEach(ant -> {
//            System.out.println(ant);
//        });

            //TODO - UPDATE FEROMONU
            //updateFeromon(history, feromonMatrix, costMatrix);


            //TODO - CZYSZCZENIE LISTY MRÓWEK KIEDY PRZEJĄ GRAF
            ants.clear();
        }
    }

    private static List<Ant> countRanking(List<Ant> ants) {
        Collections.sort(ants, new SortByLength());

        return ants;
    }

    private static void drawWords(List<Integer> mrufka, int[][] costMatrix, List<String> strings) {

        int counter = 0;
        int numberOfSkip = 0;

        System.out.println(strings.get(mrufka.get(0)));
        for (int a = 1; a < mrufka.size(); a++) {

            numberOfSkip = costMatrix[mrufka.get(a - 1)][mrufka.get(a)];
            counter += numberOfSkip;

            for (int v = 0; v < counter; v++) {
                System.out.print(" ");
            }
            System.out.println(strings.get(mrufka.get(a)));
        }
    }


    private static void updateFeromon(List<List<Integer>> history, double[][] feromon, int[][] costMatrix) {

        for (List<Integer> historia : history) {
            for (int j = 0; j < historia.size() - 1; j++) {
                feromon[historia.get(j)][historia.get(j + 1)] *= 1 + (0.1 / costMatrix[historia.get(j)][historia.get(j + 1)]);
            }
        }
    }

    private static Ant mrufka(int[][] koszt, double[][] feromon, int actualVertex, int dlugoscSlowa) {
        Ant ant = new Ant();

        int size = feromon.length;
        boolean[] visited = new boolean[size];
        Arrays.fill(visited, false);

        ant.setHistory(new ArrayList<>());
        ant.setLength(dlugoscSlowa);

        while (true) {
            visited[actualVertex] = true;

            ant.addToHistory(actualVertex);

            for (int i = 0; i < size; i++) {
                if (koszt[actualVertex][i] == -1 || koszt[actualVertex][i] == 0) {
                    visited[i] = true;
                }
            }

            boolean flag = true;
            for (Boolean visit : visited) {
                if (!visit)
                    flag = false;
            }
            if (flag) {
                return ant;
            }

            int nextVertex = generateNextVertex(actualVertex, feromon, koszt, visited);


            //TODO - CHYBA DO WYJEBANIA
//            if (nextVertex == -1) {
//                for (int i = 0; i < visited.length; i++) {
//                    if (!visited[i]) {
//                        nextVertex = i;
//                        break;
//                    }
//                }
//            }

            if (ant.getLength() + koszt[actualVertex][nextVertex] < LENGTH_OF_SEQ) {
                ant.updateLength(koszt[actualVertex][nextVertex]);
                actualVertex = nextVertex;
            } else {
                break;
            }

        }
        return ant;
    }

    private static int generateNextVertex(int actualVertex, double[][] feromon, int[][] costs, boolean[] visited) {
        double randomNumber = Math.random();
        double mianownik = 0d;
        double probability = 0d;

        int size = feromon.length;

        for (int i = 0; i < size; i++) {
            if (!visited[i]) {
                mianownik += Math.pow(feromon[actualVertex][i], ALPHA) * Math.pow(1.0d / costs[actualVertex][i], BETA);
            }
        }

        for (int i = 0; i < size; i++) {
            if (!visited[i]) {
                probability += Math.pow(feromon[actualVertex][i], ALPHA) * Math.pow(1.0d / costs[actualVertex][i], BETA);
                if (probability / mianownik >= randomNumber) {
                    return i;
                }
            }
        }
        return -1;
    }


    /**
     * Function which iterate in words matrix and create natrix of costs using "getCosts" function
     *
     * @param words
     * @return matrix of cost
     */
    private static int[][] getCostMatrix(List<String> words) {
        int matrix[][] = new int[400][400];

        for (int i = 0; i < words.size(); i++) {
            for (int j = 0; j < words.size(); j++) {
                if (i == j)
                    continue;
                matrix[i][j] = getCost(words.get(i), words.get(j));
            }
        }
        return matrix;
    }

    /**
     * Function to start create and fill the feromon matrix by 0.5 value
     *
     * @param size
     * @return float matrix filled by 0.5 value
     */
    private static double[][] startFillFeromonArray(int size) {
        double feromon[][] = new double[size][size];
        for (double element[] : feromon) {
            Arrays.fill(element, 0.5d);
        }
        return feromon;
    }

    /**
     * Function to count costs beside two words
     * For example ABCD and CDAB cost is 2
     *
     * @param s  - first word
     * @param s1 - second word
     * @return int costs
     */
    private static int getCost(String s, String s1) {
        int costs = 1;
        int counter = 0;
        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) != s1.charAt(counter)) {
                if (counter != 0)
                    i--;
                counter = 0;
                costs = i + 1;
                continue;
            }
            counter++;
        }
        return costs == 10 ? -1 : costs;
    }

    /**
     * Function to read words from file line by line
     *
     * @param filePath
     * @return List<String>results
     */
    private static List<String> readFromFile(String filePath) {
        List<String> result = new ArrayList<>();

        try {
            File file = new File(filePath);
            Scanner myReader = new Scanner(file);

            while (myReader.hasNextLine()) {
                result.add(myReader.nextLine());
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return result;
    }
}