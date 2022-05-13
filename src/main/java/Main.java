import net.andreinc.mockneat.MockNeat;
import net.andreinc.mockneat.unit.objects.Probabilities;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        List<String> strings = readFromFile("src/main/resources/Data.txt");
        int wordSize = strings.get(0).length();

        int size = strings.size();

        int[][] costMatrix = getCostMatrix(strings);
        float[][] feromonMatrix = startFillFeromonArray(size);
        float[][] probabilityMatrix = startFillPrawdopodobienstwo(costMatrix, feromonMatrix, size);

//        for(int i = 0; i < size; i++) {
//            for (int j = 0; j < size; j++) {
//                System.out.print(costMatrix[i][j] + "   ");
//            }
//            System.out.println();
//        }

        List<List<Integer>> history = new ArrayList<List<Integer>>();
        Random random = new Random();


        for (int i = 0; i < 10; i++) {
            int randomStartVertex = random.nextInt(size);
            List<Integer> mrufka = mrufka(costMatrix, feromonMatrix, randomStartVertex, wordSize);
            history.add(mrufka);

            for (int a = 0; a < mrufka.size()-1; a++) {
                System.out.println(strings.get(randomStartVertex));
                int przesuniecie = costMatrix[randomStartVertex][mrufka.get(a + 1)];
                for (int v = 0; v < przesuniecie; v++) {
                    System.out.print(" ");
                }
                System.out.println(strings.get(a + 1));
                //System.out.println();
            }


        }
    }




//            int max = 0;
//            int index = 0;
//            for(int z = 0; z < 100; z++) {
//                    if(history.get(z).size() > max){
//                        max = history.get(z).size();
//                        index = z;
//                }
//            }
//            System.out.println(history.get(index));
//            updateFeromon(history, feromonMatrix, costMatrix);
//        }

        //znajdowanie najdluzszej historii


    private static void updateFeromon(List<List<Integer>> history, float[][] feromon, int[][] costMatrix) {

        for(List <Integer> historia : history){
            for(int j = 0; j < historia.size()-1; j++){
                feromon[historia.get(j)][historia.get(j + 1)] *= 1 + (0.1 / costMatrix[historia.get(j)][historia.get(j + 1)]);
            }
        }
    }

    private static List<Integer> mrufka(int[][] koszt, float[][] feromon, int actualVertex, int dlugoscSlowa){
        int size = feromon.length;

        boolean[] visited = new boolean[size];
        Arrays.fill(visited, false);


        List<Integer> historia = new ArrayList<>();

        int aktualnaDlugoscSekwencji = dlugoscSlowa;

        int koncowaDlugoscSekwencji = 200;




        while(true){
            visited[actualVertex] = true;
            historia.add(actualVertex);

            for(int i = 0; i < size; i++){
                if(koszt[actualVertex][i] == -1 || koszt[actualVertex][i] == 0){
                    visited[i] = true;
                }
            }

            boolean flag = true;
            for(Boolean visit : visited){
                if(!visit)
                    flag = false;
            }
            if(flag)
                return historia;

            int nextVertex = generateNextVertex(actualVertex, feromon, koszt, visited);


            //TODO
            if (nextVertex == -1) {
                for (int i = 0; i < visited.length; i++) {
                    if (!visited[i]) {
                        nextVertex = i;
                        break;
                    }
                }
            }

            if(aktualnaDlugoscSekwencji + koszt[actualVertex][nextVertex] < koncowaDlugoscSekwencji) {
                aktualnaDlugoscSekwencji += koszt[actualVertex][nextVertex];
                actualVertex = nextVertex;
            }else{
                break;
            }

        }
        return historia;
    }

    private static int generateNextVertex(int actualVertex, float[][] feromon, int[][] costs, boolean[] visited) {
        double ALPHA = 1d;
        double BETA = 1d;

        double randomNumber = Math.random();
        double mianownik = 0d;
        double probability = 0d;

        int size = feromon.length;

        for(int i = 0; i < size; i++){
            if(!visited[i]) {
                mianownik += Math.pow(feromon[actualVertex][i], ALPHA) * Math.pow(1.0d / costs[actualVertex][i], BETA);
            }
        }

        for(int i = 0; i < size; i++){
            if(!visited[i]) {
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
     * @param words
     * @return matrix of cost
     */
    private static int[][] getCostMatrix(List<String> words){
        int matrix[][] = new int[400][400];

        for(int i = 0; i < words.size(); i++){
            for(int j = 0; j < words.size(); j++){
                if(i == j)
                    continue;
                matrix[i][j] = getCost(words.get(i), words.get(j));
            }
        }
        return matrix;
    }

    /**
     * Function to start create and fill the feromon matrix by 0.5 value
     * @param size
     * @return float matrix filled by 0.5 value
     */
    private static float[][] startFillFeromonArray(int size){
        float feromon[][] = new float[size][size];
        for(float element[] : feromon){
            Arrays.fill(element, 0.5F);
        }
        return feromon;
    }

    /**
     * Function to count start probability
     * @param costs
     * @param feromon
     * @param size
     * @return matrix with prawdopodobienstwo
     */
    private static float[][] startFillPrawdopodobienstwo(int[][] costs, float [][] feromon, int size){
        float [][] prawdopodobienstwo = new float[size][size];
        for(int i = 0; i < size; i++){
            float mianownik = countMianownik(costs, feromon, i, size);
            for(int j = 0; j < size; j++){
                if(costs[i][j] == -1){
                    prawdopodobienstwo[i][j] = 0;
                    continue;
                }
                prawdopodobienstwo[i][j] = (costs[i][j] * feromon[i][j]) / mianownik;
            }
        }
        return prawdopodobienstwo;
    }

    /**
     * Function to count denominator to help count probability function
     * @param costs
     * @param feromon
     * @param i
     * @param size
     * @return float
     */
    private static float countMianownik(int[][] costs, float [][] feromon, int i, int size){
        float mianownik = 0f;
        for (int j = 0; j < size; j++) {
            if (costs[i][j] == -1) {
                continue;
            }
            mianownik += costs[i][j] * feromon[i][j];
        }
        return mianownik;
    }

    /**
     * Function to count costs beside two words
     * For example ABCD and CDAB cost is 2
     * @param s - first word
     * @param s1 - second word
     * @return int costs
     */
    private static int getCost(String s, String s1) {
        int costs = 1;
        int counter = 0;
        for(int i = 1; i < s.length(); i++){
            if(s.charAt(i) != s1.charAt(counter)){
                if(counter != 0)
                    i--;
                counter = 0;
                costs = i+1;
                continue;
            }
            counter++;
        }
        return costs == 10 ? -1 : costs;
    }

    /**
     * Function to read words from file line by line
     * @param filePath
     * @return List<String>results
     */
    private static List<String> readFromFile(String filePath){
        List <String> result = new ArrayList<>();

        try {
            File file = new File(filePath);
            Scanner myReader = new Scanner(file);

            while(myReader.hasNextLine()){
                result.add(myReader.nextLine());
            }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        return result;
    }
}