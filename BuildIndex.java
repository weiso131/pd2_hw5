import java.io.BufferedReader;
import java.io.FileReader;

import java.io.IOException;
import java.util.ArrayList;



public class BuildIndex {
    
    public static TrieToSerial document = new TrieToSerial();
    public static long startTime = System.currentTimeMillis();

    public static void main(String[] args) {
        
        getDocString(args[0]);

        System.out.println(document.totalWords.get(4));
        
        document.toSerial(args[0].substring(0, args[0].length() - 4));

    }

    public static ArrayList<String> readLine(String fileName) {
        ArrayList<String> lines = new ArrayList<String>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null)
                lines.add(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static void getDocString(String fileName) {
        ArrayList<String> lines = readLine(fileName);

        
        for (int i = 0; i < lines.size(); i++) {
            
            String newLine = lines.get(i).toLowerCase().replaceAll("[^a-z]", " ");
            String[] words = newLine.split(" ");

            int index = (i / 5);

            for (String word : words) {
                if (word.length() == 0)
                    continue;

                document.insert(word, 1, index);
            }
        }
    }
    
}


