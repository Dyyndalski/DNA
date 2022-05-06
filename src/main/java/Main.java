import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        int size = 100;

        int koszt[][] = new int[size][size];
        int feromon[][] = new int[size][size];
        int prawdopodobienstwo[][] = new int[size][size];

        List<String> strings = readFromFile("/Users/kacperszaur/Desktop/DNA/src/main/java/data.txt");

        int[][] costMatrix = getCostMatrix(strings);

        System.out.println(strings.size());

        for(int i = 0; i < strings.size(); i++){
            for(int j = 1; j < strings.size(); j++) {
                System.out.print(costMatrix[i][j]);
            }
            System.out.println();
        }

    }

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