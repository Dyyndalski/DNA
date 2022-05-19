import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {

    static double ALPHA = 1.0d;
    static double BETA = 1.0d;

    public static void main(String[] args) throws IOException {

        //File folder = new File("src/main/resources/1-negatywne-losowe");
        //File folder = new File("src/main/resources/2-negatywne-powtorzenia");
        File folder = new File("src/main/resources/3-poztywne-losowe");
        //File folder = new File("src/main/resources/4-pozytywne-przeklamania");


        List<File> files = Arrays.asList(folder.listFiles());
        for(File file : files) {
            System.out.println(file.getName());
            String fileName = file.getName().toString();

            String[] split = fileName.split("\\.");
            String[] split1 = split[1].split("\\-|\\+");


            int LENGTH_OF_SEQ = Integer.valueOf(split1[0]) + 9;
            int WORD_LENGTH = 10;
            int WORDS_NUMBER = LENGTH_OF_SEQ - WORD_LENGTH + 1;

            List<String> strings = readFromFile(file);
            int wordSize = strings.get(0).length();
            int size = strings.size();
            int[][] costMatrix = getCostMatrix(strings);
            double[][] feromonMatrix = startFillFeromonArray(size);

            List<Ant> ants = new ArrayList<>();
            Random random = new Random();

            Ant maxAnt = new Ant();
            maxAnt.setSize(0);

            long startTime = System.currentTimeMillis();

            for (int x = 0; x < 3; x++) {
                for (int i = 0; i < 20; i++) {
                    int randomStartVertex = random.nextInt(size);
                    Ant mrufka = mrufka(costMatrix, feromonMatrix, randomStartVertex, wordSize, LENGTH_OF_SEQ);

                    ants.add(mrufka);
                }

                List<Ant> sortedAntsByLength = countRanking(ants);

                Ant ant = sortedAntsByLength.get(0);

                if (ant.getSize() > maxAnt.getSize()) {
                    maxAnt.setSize(ant.getSize());
                    maxAnt.setHistory(ant.getHistory());
                    maxAnt.setLength(ant.getLength());
                }

                updateFeromon(sortedAntsByLength, feromonMatrix, costMatrix, WORDS_NUMBER);
                ants.clear();
            }
            //System.out.println("Najlepsza mrufka: " + maxAnt);
            //drawWords(maxAnt.getHistory(), costMatrix, strings);

            long stop=System.currentTimeMillis();
            System.out.println("Czas wykonania (w milisekundach): "+(stop-startTime));

            long timeOfAlgorythm = stop - startTime;


            //formatowanie pliku
            FileWriter resultFile = new FileWriter(file.getParentFile().getName(), true);
            BufferedWriter out = new BufferedWriter(resultFile);
            out.write("nazwa pliku: " + file.getName() + "\n");
            out.write("time: " + String.valueOf((double)timeOfAlgorythm / 1000) + "\n");
            out.write("długość sekwencji: " + maxAnt.getLength() + "\n");
            out.write("liczba wyrazów: " + maxAnt.getSize() + "\n");
            out.write("\n" + "\n");
            out.close();
        }
    }


    private static List<Ant> countRanking(List<Ant> ants) {
        Collections.sort(ants, new SortByLength());

        return ants;
    }

    private static void updateFeromon(List<Ant> ants, double[][] feromon, int[][] costMatrix, int WORDS_NUMBER) {

        for(int i = 0; i < feromon.length; i++){
            for(int j = 0; j < feromon.length; j++){
                feromon[i][j] *= 0.5;
            }
        }

        for(Ant ant : ants){
            for(int i = 0; i < ant.getSize()-1; i++){
                feromon[ant.getHistory().get(i)][ant.getHistory().get(i+1)] *= 1.0 + (ant.getSize() / (double)WORDS_NUMBER);
            }
        }
    }

    private static Ant mrufka(int[][] koszt, double[][] feromon, int actualVertex, int dlugoscSlowa, int LENGTH_OF_SEQ) {
        Ant ant = new Ant();

        int size = feromon.length;
        boolean[] visited = new boolean[size];
        Arrays.fill(visited, false);

        ant.setHistory(new ArrayList<>());
        ant.setLength(dlugoscSlowa);

        while(true) {
            visited[actualVertex] = true;

            ant.addToHistory(actualVertex);
            ant.setSize(ant.getHistory().size());

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


            if (nextVertex == -1) {
                for (int i = 0; i < visited.length; i++) {
                    if (!visited[i]) {
                        nextVertex = i;
                        break;
                    }
                }
            }

            if (ant.getLength() + koszt[actualVertex][nextVertex] <= LENGTH_OF_SEQ) {
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
        int matrix[][] = new int[800][800];

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
        return costs >= 10 ? 10 : costs;
        //return costs;
    }

    /**
     * Function to read words from file line by line
     *
     * @param file
     * @return List<String>results
     */
    private static List<String> readFromFile(File file) {
        List<String> result = new ArrayList<>();

        try {
            Scanner myReader = new Scanner(file);

            while (myReader.hasNextLine()) {
                result.add(myReader.nextLine());
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return result;
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
}