import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TFIDFSearch {
    public static ArrayList<Trie> docSet = new ArrayList<Trie>();
    public static Trie document = new Trie();
    public static int n = -1;
    public static void main(String[] args){

        File data_dir = new File("data_dir");

        String fileList[] = data_dir.list();

        for (String s : fileList){
            if (s.equals("BigTrie")){
                document.deSerialTotal(s);
                continue;
            }
            docSet.add(new Trie());
            docSet.get(Integer.parseInt(s)).deSerialTotal(s);
        }
            

        document.deSerialWord("pppp", "0");
        System.out.println(document.search("pppp"));
        
        System.out.println(docSet.get(0).totalWords);


        getInputFile(args[1]);
    }
    public static void getInputFile(String fileName){


        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null){
                if (n == -1){
                    n = Integer.parseInt(line);
                    continue;
                }
                ArrayList<TFIDFData> ans = splitProcess(line);

                for (int i = 0;i < ans.size();i++)
                    System.out.println(String.format("number:%d, TFIDF:%f", ans.get(i).number, ans.get(i).TFIDF));

                
            }
                
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<TFIDFData> splitProcess(String line){
        String[] query = line.split(" ");
        
        String mod = "OR";

        if (query.length > 1)
            mod = query[1];//AND or OR

        if (mod.charAt(0) == 'A'){
            return AND(query);
        }
        else
            return AND(query);
    }

    public static ArrayList<TFIDFData> AND(String[] query){
        ArrayList<TFIDFData> result = new ArrayList<TFIDFData>();

        for (int i = 0;i < docSet.size();i++){
            double TFIDF = 0;
            for (int j = 0;j < query.length;j += 2){
                double TF = docSet.get(i).search(query[j]), IDF = document.search(query[j]);


                if (TF == 0){
                    docSet.get(i).deSerialWord(query[j], Integer.toString(i));
                    TF = docSet.get(i).search(query[j]);
                }
                    
                if (TF <= 0){
                    TFIDF = 0;
                    break;
                }
                if (IDF == 0){
                    document.deSerialWord(query[j], "BigTrie");
                    IDF = document.search(query[j]);
                }

                TFIDF += (TF / docSet.get(i).totalWords) * (docSet.size() / IDF);
            }

            result.add(new TFIDFData(i, TFIDF));
        }
        

        Comparator<TFIDFData> nameComparator = new Comparator<TFIDFData>() {
            @Override
            public int compare(TFIDFData p1, TFIDFData p2) {
                return Double.compare(p2.TFIDF, p1.TFIDF);
            }
        };

        Collections.sort(result, nameComparator);
        return result;
    }
}

class TFIDFData {
    int number;
    double TFIDF;

    public TFIDFData(int number, double TFIDF){
        this.number = number;
        this.TFIDF = TFIDF;
    }
}
