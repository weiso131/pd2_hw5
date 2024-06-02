import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class TFIDFSearch {

    public static int n = -1;
    public static String bookName;
    
    public static TrieToSerial document = new TrieToSerial();
    public static void main(String[] args){


        bookName = args[0];
        ArrayList<Double> totalWords = new ArrayList<>();
        try (FileInputStream fileIn = new FileInputStream(bookName + "/_totalWords.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn)) {
            totalWords = (ArrayList<Double>) in.readObject();
        } catch (IOException i) {
            i.printStackTrace();
            return;
        } catch (ClassNotFoundException c) {
            return;
        }

        document.totalWords = totalWords;


        getInputFile(args[1]);

        
    }
    public static void getInputFile(String fileName){

        StringBuilder output = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null){
                if (n == -1){
                    n = Integer.parseInt(line);
                    continue;
                }
                ArrayList<TFIDFData> ans = splitProcess(line);

                

                for (int i = 0;i < Math.min(n, ans.size());i++){
                    output.append(ans.get(i).number);
                    output.append(" ");
                }
                    
                for (int i = 0;i < n - ans.size();i++){
                    output.append("-1 ");
                }
                output.append("\n");
            }
                
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))) {
            writer.write(output.toString());
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
            return ANDOR(query, 1);
        }
        else
            return ANDOR(query, 0);
    }

    public static ArrayList<TFIDFData> ANDOR(String[] query, int useAND){
        ArrayList<TFIDFData> ans = new ArrayList<>();
        double value[] = new double[document.totalWords.size()];
        int add_count[] = new int[document.totalWords.size()];
        int count = useAND * (query.length / 2 + 1);

        for (int i = 0;i < query.length;i++){
            if (i % 2 == 0){
                StringBuilder sb = new StringBuilder();
                sb.append(bookName);
                sb.append("/");
                sb.append(query[i]);
                sb.append(".ser");
                ArrayList<index_double> searchData = document.search(sb.toString(), query[i]);

                for (index_double id : searchData){
                    value[id.bookIndex] += id.value;
                    add_count[id.bookIndex]++;
                    
                    
                    if (add_count[id.bookIndex] == count && useAND == 1)
                        ans.add(new TFIDFData(id.bookIndex, value[id.bookIndex]));
                }

            }
        }
        if (useAND == 0)
            for (int i = 0;i < value.length;i++)
                if (add_count[i] != 0)
                    ans.add(new TFIDFData(i, value[i]));
        Comparator<TFIDFData> TFIDFComparator = new Comparator<TFIDFData>() {
            @Override
            public int compare(TFIDFData p1, TFIDFData p2) {
                if (Double.compare(p2.TFIDF, p1.TFIDF) == 0)
                    return Integer.compare(p1.number, p2.number);
                return Double.compare(p2.TFIDF, p1.TFIDF);
            }
        };
        Collections.sort(ans, TFIDFComparator);
        
        return ans;
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
