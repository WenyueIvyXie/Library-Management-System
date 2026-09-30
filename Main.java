import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Inventory inventory = loadInventory();
                new LibraryGUI(inventory).setVisible(true);
            } catch (IOException | RuntimeException e) {
                JOptionPane.showMessageDialog(null,
                        "The inventory could not be loaded:\n" + e.getMessage(),
                        "Startup error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static Inventory loadInventory() throws IOException {
        String inputFileName = "inventory.txt";
        File inputFile = new File(inputFileName);
        int fileLineCount = InventoryLoader.countLines(inputFile);
        ArrayList<LibraryItem> items = new ArrayList<>();

        //Variables that store existing inventory
        String[] types = new String[fileLineCount];
        String[] itemIds = new String[fileLineCount];
        String[] titles = new String[fileLineCount];
        String[] genres = new String[fileLineCount];
        double[] ratings = new double[fileLineCount];
        boolean[] availabilities = new boolean[fileLineCount];
        String[] extra1 = new String[fileLineCount];
        String[] extra2 = new String[fileLineCount];
        InventoryLoader.load(types, itemIds, titles, genres, ratings, availabilities, extra1, extra2, inputFile);
        buildNewItem(items, fileLineCount, types, itemIds, titles, genres, ratings, availabilities, extra1, extra2);

        return new Inventory(items);
    }
    //------------------------------------------------------------------------------
    public static void buildNewItem(ArrayList<LibraryItem> items, int fileLineCount, String[] types, String[] itemIds, String[] titles, String[] genres,
                                    double[] ratings, boolean[] availabilities, String[] extra1, String[] extra2){
        for (int i = 0; i<fileLineCount; i++){
            if (types[i].equals("Book")){
                items.add(new Book(itemIds[i], titles[i], extra1[i], genres[i], ratings[i], availabilities[i]));
            }
            else if (types[i].equals(("AudioBook"))){
                items.add(new AudioBook(itemIds[i], titles[i], extra1[i], extra2[i], genres[i], ratings[i], availabilities[i]));
            }
            else if (types[i].equals("Magazine")){
                items.add(new Magazine(itemIds[i], titles[i], Integer.parseInt(extra1[i]), genres[i], ratings[i], availabilities[i]));
            }
            else if (types[i].equals("Movie")){
                items.add(new Movie(itemIds[i], titles[i], extra1[i], genres[i], ratings[i], availabilities[i]));
            }
        }
    }


}
