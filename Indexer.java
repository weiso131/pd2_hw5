
import java.io.IOException;
import java.io.Serializable;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.File;
import java.util.ArrayList; 

public class Indexer{
    public ArrayList<index_double> Frequency = new ArrayList<>();
    public Indexer[] children = new Indexer[26];

}

class utility{
    public static int binarySearch(ArrayList<index_double> Frequency, int bookIndex){
        int l = 0, r = Frequency.size(), lr;

        while (r > l + 1){
            lr = (l + r) / 2;

            if (Frequency.get(lr).bookIndex <= bookIndex)
                l = lr;
            else
                r = lr;
        }
        lr = (l + r) / 2;

        if (Frequency.get(lr).bookIndex == bookIndex) return lr;
        else if (Frequency.get(l).bookIndex == bookIndex) return l;

        return -1;
    }
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


class Trie {
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

    public double search(String word, int bookIndex) {
        Indexer node = root;
        
        for (char c : word.toCharArray()) {
            node = node.children[c - 'a'];
            if (node == null) {
                return 0;
            }
        }
        int index = utility.binarySearch(node.Frequency, bookIndex);
        if (index != -1)
            return node.Frequency.get(index).value;
        return 0;
    }

    public void dfs(Indexer node, String path){
        for (int i = 0;i < 26;i++){
            if (node.children[i] != null)
                dfs(node.children[i], path + (char)(i +  (int)'a'));
        }

        if (node.Frequency.size() != 0){
            try (FileOutputStream fileOut = new FileOutputStream(path + ".ser");
                ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
                out.writeObject(node.Frequency);
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
        try (FileOutputStream fileOut = new FileOutputStream(fileName + "/_totalWords.ser");
                ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
                out.writeObject(totalWords);
            } catch (IOException i) {
                i.printStackTrace();
            }
        dfs(root, fileName + "/");
    }
    
}