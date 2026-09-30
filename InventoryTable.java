import java.awt.BorderLayout;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/** Displays the library inventory in a JTable, like the teacher's Table example. */
public class InventoryTable extends JFrame {
    private final Inventory inventory;
    private final String[] columns = {"ID", "Type", "Title", "Creator / Issue",
            "Narrator", "Genre", "Rating", "Available"};
    private final DefaultTableModel model = new DefaultTableModel(columns, 0) {


        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private final ArrayList<LibraryItem> displayedItems = new ArrayList<>();

    public InventoryTable(Inventory inventory) {
        this.inventory = inventory;
        setTitle("Library Inventory");
        setSize(1000, 360);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        add(new JScrollPane(table), BorderLayout.CENTER);
        update();
    }

    public void update() {
        update(inventory.search(""));
    }

    public void update(ArrayList<LibraryItem> items) {
        model.setRowCount(0);
        displayedItems.clear();

        for (LibraryItem item : items) {
            displayedItems.add(item);
            String creator = "";
            String narrator = "";

            if (item instanceof Magazine) {
                creator = "Issue " + item.getIssueNumber();
            } else if (item instanceof Movie) {
                creator = item.getAuthor();
            } else if (item instanceof AudioBook) {
                creator = item.getAuthor();
                narrator = ((AudioBook) item).getNarrator();
            } else {
                creator = item.getAuthor();
            }

            model.addRow(new Object[] {item.getItemId(), item.getClass().getSimpleName(),
                    item.getTitle(), creator, narrator, item.getGenre(), item.getRating(),
                    item.isAvailable() ? "Yes" : "No"});
        }
    }

    public LibraryItem getSelectedItem() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return null;
        }
        return displayedItems.get(row);
    }
}
