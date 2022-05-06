import java.io.File;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        List<String> strings = readFromFile("/Users/kacperszaur/Desktop/DNA/src/main/java/data.txt");

    }

    /**
     * Function which iterate in words matrix and create natrix of costs using "getCosts" function
     * @param words
     * @return matrix of cost
     */
    private static int[][] getCostMatrix(List<String> words){
        int matrix[][] = new int[200][200];

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