
import java.io.IOException;
import java.io.Serializable;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.File;

public class Indexer implements Serializable {
    private static final long serialVersionUID = 1L;
    public double Frequency = 0;
    public Indexer[] children = new Indexer[26];

}
class Trie {
    Indexer root = new Indexer();
    double totalWords = 0;

    public void insert(String word, double value) {
        Indexer node = root;
        for (char c : word.toCharArray()) {
            if (node.children[c - 'a'] == null) {
                node.children[c - 'a'] = new Indexer();
            }
            node = node.children[c - 'a'];
        }
        node.Frequency += value;
        if (value == 1)
            totalWords++;
    }

    public double search(String word) {
        Indexer node = root;
        
        for (char c : word.toCharArray()) {
            node = node.children[c - 'a'];
            if (node == null) {
                return 0;
            }
        }
        return node.Frequency;
    }

    public void dfs(Indexer node, String path){
        
        for (int i = 0;i < 26;i++) {
            if (node.children[i] != null)
                dfs(node.children[i], path + (char)(i + 97));
        }
        if (node.Frequency != 0){
            try (DataOutputStream out = new DataOutputStream(new FileOutputStream(path + ".dat"))) {
                out.writeDouble(node.Frequency);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
            
    }
    
    public void toSerial(String path){
        File singleDir = new File(path);
        if (!singleDir.exists())
            singleDir.mkdir();
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(path + "/_totalWord.dat"))) {
            out.writeDouble(totalWords);
        } catch (IOException e) {
            e.printStackTrace();
        }
        dfs(root, path + "/");
    }

    public void deSerialWord(String word, String TrieName) {
        /*
         * Be careful, the word not exist will insert -1
         */
        StringBuilder sb = new StringBuilder();
        sb.append("data_dir/");
        sb.append(TrieName);
        sb.append("/");
        sb.append(word);
        sb.append(".dat");

        String fileName = sb.toString();

        
        try (DataInputStream in = new DataInputStream(new FileInputStream(fileName))) {
            double value = in.readDouble();

            insert(word, value);
        } catch (IOException e) {
            insert(word, -1);
        }
    }
    public void deSerialTotal(String TrieName) {
        StringBuilder sb = new StringBuilder();
        sb.append("data_dir/");
        sb.append(TrieName);
        sb.append("/_totalWord.dat");
        String fileName = sb.toString();
        try (DataInputStream in = new DataInputStream(new FileInputStream(fileName))) {
            totalWords = in.readDouble();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}