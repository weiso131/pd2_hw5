
import java.io.IOException;
import java.io.Serializable;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.File;
import java.util.ArrayList; 
import java.io.DataOutputStream;

public class Indexer{
    boolean use = false;
    public ArrayList<index_double> Frequency = new ArrayList<>();
    public Indexer[] children = new Indexer[26];

}
class index_double implements Serializable {
    private static final long serialVersionUID = 1L;
    int bookIndex;
    double value;
    public index_double(int index, double value){
        this.value = value;
        this.bookIndex = index;
    }

}

class TrieToSerial {
    
    Indexer root = new Indexer();
    ArrayList<Double> totalWords = new ArrayList<>();

    public void insert(String word, double value, int index) {
        Indexer node = root;
        for (char c : word.toCharArray()) {
            if (node.children[c - 'a'] == null) {
                node.children[c - 'a'] = new Indexer();
            }
            node = node.children[c - 'a'];
        }
        if (node.Frequency.size() == 0 || node.Frequency.get(node.Frequency.size() - 1).bookIndex != index )
            node.Frequency.add(new index_double(index,0 ));
        double ori = node.Frequency.get(node.Frequency.size() - 1).value;
        node.Frequency.get(node.Frequency.size() - 1).value = ori + value;
        if (value == 1){
            if (index + 1 > totalWords.size())
                totalWords.add(0.0);
            totalWords.set(index, totalWords.get(index) + 1);
        }
    }

    

    public void dfs(Indexer node, String path){
        for (int i = 0;i < 26;i++){
            if (node.children[i] != null)
                dfs(node.children[i], path + (char)(i +  (int)'a'));
        }
        ArrayList<index_double> TFIDFS = new ArrayList<index_double>();

        for (index_double id : node.Frequency){
            double TF = id.value / totalWords.get(id.bookIndex);
            double IDF = Math.log(((double)totalWords.size()) / ((double)node.Frequency.size()));
            TFIDFS.add(new index_double(id.bookIndex, TF * IDF));
        }

        if (node.Frequency.size() != 0){
            try (FileOutputStream fileOut = new FileOutputStream(path + ".ser");
                ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
                out.writeObject(TFIDFS.toArray(new index_double[0]));
            } catch (IOException i) {
                i.printStackTrace();
            }
        }
    }

    public void toSerial(String fileName){
        File singleDir = new File(fileName);
        if (!singleDir.exists()){
            singleDir.mkdirs();
        }
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(fileName + "/_totalWords.dat"))) {
            dos.writeInt(totalWords.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
        dfs(root, fileName + "/");
    }

}

class FastTrieNode{
    boolean use = false;
    public index_double[] Frequency = null;
    public FastTrieNode[] children = new FastTrieNode[26];

}
class FastTrie{
    FastTrieNode root = new FastTrieNode();
    int totalBook = 0;

    public index_double[] deSerial(String fileName, String word){
        index_double[] data = null;

        try (FileInputStream fileIn = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(fileIn)) {
            data = (index_double[]) in.readObject();
        } catch (IOException i) {
            data = new index_double[0];
        } catch (ClassNotFoundException c) {
        }
        FastTrieNode node = root;
        for (char c : word.toCharArray()) {
            if (node.children[c - 'a'] == null) {
                node.children[c - 'a'] = new FastTrieNode();
            }
            node = node.children[c - 'a'];
        }

        node.Frequency = data;
        node.use = true;
        return data;
    }
    public index_double[] search(String path, String word) {
        FastTrieNode node = root;
        char[] wordArray = word.toCharArray();
        for (int i = 0;i < wordArray.length;i++) {
            char c = wordArray[i];
            node = node.children[c - 'a'];
            if (node == null || (node.use == false && i == wordArray.length - 1)) {
                return deSerial(path, word);
            }
                
        }
        return node.Frequency;
    }
}
