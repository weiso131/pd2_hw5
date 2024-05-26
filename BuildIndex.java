import java.io.BufferedReader;
import java.io.FileReader;

import java.io.IOException;
import java.util.ArrayList;

import java.io.File;


public class BuildIndex {
    
    public static ArrayList<Trie> docSet = new ArrayList<Trie>();
    public static Trie document = new Trie();
    public static long startTime = System.currentTimeMillis();

    public static void main(String[] args) {
        docSet = getDocString(args[0]);
        System.out.println(docSet.get(0).totalWords);
        
        //build dir 
        String path = "data_dir";
        File singleDir = new File(path);
        if (singleDir.exists())
            deleteDirectory(singleDir);//delete old data
        singleDir.mkdir();
        

        document.toSerial(String.format("%s/%s", path, "BigTrie"));
        for (int i = 0;i < docSet.size();i++)
            docSet.get(i).toSerial(String.format("%s/%d", path, i));

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

    public static ArrayList<Trie> getDocString(String fileName) {
        ArrayList<String> lines = readLine(fileName);

        
        for (int i = 0; i < lines.size(); i++) {
            
            String newLine = lines.get(i).toLowerCase().replaceAll("[^a-z]", " ");
            String[] words = newLine.split(" ");


            if ((i / 5) + 1 > docSet.size())
                docSet.add(new Trie());

            for (String word : words) {
                if (word.length() == 0)
                    continue;

                if (docSet.get(i / 5).search(word) == 0){
                    document.insert(word, 1);
                }

                docSet.get(i / 5).insert(word, 1);
            }
        }
        return docSet;
    }
    private static void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) { // Ensure it's not null to avoid NullPointerException
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file); // Recursive call for sub-directory
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }
}


