import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

/** Role-selection controller*/
public class LibraryGUI extends JFrame implements ActionListener {
    private final LibraryContext context;
    private final InventoryTable table;
    private final JButton customerButton = new JButton("Customer");
    private final JButton employeeButton = new JButton("Employee");
    private final JButton masterButton = new JButton("Master Librarian");
    private final CustomerGUI customerGUI;
    private final EmployeeGUI employeeGUI;
    private final MasterGUI masterGUI;

    public LibraryGUI(Inventory inventory) {
        context = new LibraryContext(inventory);
        table = new InventoryTable(inventory);
        customerGUI = new CustomerGUI(this, context, table);
        employeeGUI = new EmployeeGUI(this, context, table);
        masterGUI = new MasterGUI(this, context, table);

        setTitle("Library Management System");
        setSize(200, 180);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        add(new JLabel("Choose your role:"));
        add(customerButton);
        add(employeeButton);
        add(masterButton);

        customerButton.addActionListener(this);
        employeeButton.addActionListener(this);
        masterButton.addActionListener(this);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == customerButton) openRole(customerGUI);
        if (e.getSource() == employeeButton) openRole(employeeGUI);
        if (e.getSource() == masterButton) openRole(masterGUI);
    }

    private void openRole(RoleGUI role) {
        setVisible(false);
        role.open();
    }

    static void message(JFrame frame, String text) {
        JOptionPane.showMessageDialog(frame, text);
    }
}

/** Stores people and temporary customer hand-ins while the program runs. */
class LibraryContext {
    final Inventory inventory;
    private final Map<String, User> customers = new HashMap<>();
    private final Map<String, Employee> employees = new HashMap<>();
    private final Map<LibraryItem, User> customerHandIns = new HashMap<>();

    LibraryContext(Inventory inventory) {
        this.inventory = inventory;
    }

    User customer(String name) {
        return customers.computeIfAbsent(name.toLowerCase(),
                key -> new User("C" + (customers.size() + 1), name, inventory));
    }

    Employee employee(String name) {
        return employees.computeIfAbsent(name.toLowerCase(),
                key -> new Employee("E" + (employees.size() + 1), name, inventory));
    }

    MasterLibrarian librarian(String name) {
        return new MasterLibrarian("M1", name, inventory);
    }

    Employee existingEmployee(String name) {
        return employees.get(name.toLowerCase());
    }

    void handIn(User customer, LibraryItem item) {
        customerHandIns.put(item, customer);
    }

    User takeCustomerHandIn(LibraryItem item, String customerName) {
        User customer = customerHandIns.get(item);
        if (customer != null && customer.getName().equalsIgnoreCase(customerName)) {
            customerHandIns.remove(item);
            return customer;
        }
        return null;
    }
}

/** Common search controls for each role window. */
abstract class RoleGUI extends JFrame implements ActionListener {
    protected final LibraryContext context;
    protected final InventoryTable table;
    private final LibraryGUI roleMenu;
    protected final JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    protected final JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    protected final JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    protected final JPanel row4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private final JTextField searchField = new JTextField(14);
    private final JComboBox<String> searchType = new JComboBox<>(
            new String[] {"Title / Genre / Author", "Minimum Rating", "Magazine Issue"});
    private final JButton searchButton = new JButton("Search");
    private final JButton showAllButton = new JButton("Show All");
    private final JButton logoutButton = new JButton("Logout");

    RoleGUI(String title, LibraryGUI roleMenu, LibraryContext context, InventoryTable table) {
        this.roleMenu = roleMenu;
        this.context = context;
        this.table = table;
        setTitle(title);
        setSize(760, 180);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new FlowLayout());

        row1.add(searchType);
        row1.add(searchField);
        row1.add(searchButton);
        row1.add(showAllButton);
        row1.add(logoutButton);
        add(row1);
        add(row2);
        add(row3);
        add(row4);

        searchButton.addActionListener(this);
        showAllButton.addActionListener(this);
        logoutButton.addActionListener(this);
        table.update();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchButton) search();
        if (e.getSource() == showAllButton) table.update();
        if (e.getSource() == logoutButton) logout();
        handleRoleAction(e);
    }

    void open() {
        table.update();
        table.setVisible(true);
        setVisible(true);
    }

    private void logout() {
        setVisible(false);
        table.setVisible(false);
        roleMenu.setVisible(true);
    }

    private void search() {
        String searchText = searchField.getText().trim();
        try {
            if (searchType.getSelectedIndex() == 1) { //If user choose to search by rating
                table.update(context.inventory.search(Double.parseDouble(searchText)));
            } else if (searchType.getSelectedIndex() == 2) { //If user choose to search by magazine issue number
                table.update(context.inventory.search(Integer.parseInt(searchText)));
            } else { //If user choose to search by title, author, genre
                table.update(context.inventory.search(searchText));
            }
        } catch (NumberFormatException e) {
            LibraryGUI.message(this, "Enter a number for rating or magazine issue.");
        }
    }

    protected LibraryItem selectedItem() {
        LibraryItem item = table.getSelectedItem();
        if (item == null) LibraryGUI.message(this, "Select an item in the inventory table first.");
        return item;
    }

    protected void processReturn(String returnType, String name) {
        LibraryItem item = selectedItem();
        if (item == null) return;
        if (returnType.equals("Repaired item")) {
            context.inventory.addRepairedItem(item);
            LibraryGUI.message(this, "Repaired item returned to inventory.");
        } else if (name.trim().isEmpty()) {
            LibraryGUI.message(this, "Enter the customer or employee name.");
        } else if (returnType.equals("Customer return")) {
            User customer = context.takeCustomerHandIn(item, name.trim());
            if (customer == null) {
                LibraryGUI.message(this, "This customer has not handed in this item.");
                return;
            }
            closeLoan(customer, item);
        } else {
            User employee = context.existingEmployee(name.trim());
            if (employee == null) {
                LibraryGUI.message(this, "This employee has not been recorded yet.");
                return;
            }
            closeLoan(employee, item);
        }
        table.update();
    }

    private void closeLoan(User user, LibraryItem item) {
        LocalDate today = LocalDate.now();
        user.returnItem(item, today.getYear(), today.getMonthValue(), today.getDayOfMonth());
        LibraryGUI.message(this, "Return processed.");
    }

    protected abstract void handleRoleAction(ActionEvent e);
}

class CustomerGUI extends RoleGUI {
    private final JTextField nameField = new JTextField(10);
    private final JButton borrowButton = new JButton("Borrow / Reserve");
    private final JButton handInButton = new JButton("Return Item to Employee");

    CustomerGUI(LibraryGUI roleMenu, LibraryContext context, InventoryTable table) {
        super("Customer", roleMenu, context, table);
        setSize(600, 180);
        row2.add(new JLabel("Customer name:"));
        row2.add(nameField);
        row2.add(borrowButton);
        row2.add(handInButton);
        borrowButton.addActionListener(this);
        handInButton.addActionListener(this);
    }

    @Override
    protected void handleRoleAction(ActionEvent e) {
        if (e.getSource() == borrowButton) borrow();
        if (e.getSource() == handInButton) handIn();
    }

    private void borrow() {
        String name = nameField.getText().trim();
        LibraryItem item = selectedItem();
        if (name.isEmpty() || item == null) {
            if (name.isEmpty()) LibraryGUI.message(this, "Enter the customer name.");
            return;
        }
        LocalDate today = LocalDate.now();
        Loan loan = context.customer(name).borrowItem(item, today.getYear(),
                today.getMonthValue(), today.getDayOfMonth());
        LibraryGUI.message(this, loan == null ? "Item unavailable: reservation created."
                : Loan.loanMessage(loan));
        table.update();
    }

    private void handIn() {
        String name = nameField.getText().trim();
        LibraryItem item = selectedItem();
        if (name.isEmpty() || item == null) {
            if (name.isEmpty()) LibraryGUI.message(this, "Enter the customer name.");
            return;
        }
        User customer = context.customer(name);
        if (!customer.hasOpenLoan(item)) {
            LibraryGUI.message(this, "This customer does not have the selected item on loan.");
            return;
        }
        context.handIn(customer, item);
        LibraryGUI.message(this, "Item handed to an employee for processing.");
    }
}

class EmployeeGUI extends RoleGUI {
    private final JTextField employeeField = new JTextField(8);
    private final JTextField customerField = new JTextField(8);
    private final JTextField returnNameField = new JTextField(8);
    private final JComboBox<UsePurpose> purposeBox = new JComboBox<>(UsePurpose.values());
    private final JComboBox<String> returnTypeBox = new JComboBox<>(
            new String[] {"Customer return", "Employee return", "Repaired item"});
    private final JButton requestButton = new JButton("Submit Use Request");
    private final JButton returnButton = new JButton("Process Return");

    EmployeeGUI(LibraryGUI roleMenu, LibraryContext context, InventoryTable table) {
        super("Employee", roleMenu, context, table);
        row2.add(new JLabel("Employee:")); row2.add(employeeField);
        row2.add(purposeBox);
        row2.add(new JLabel("Rental customer:")); row2.add(customerField);
        row2.add(requestButton);
        row3.add(returnTypeBox);
        row3.add(new JLabel("Returner:")); row3.add(returnNameField);
        row3.add(returnButton);
        requestButton.addActionListener(this);
        returnButton.addActionListener(this);
    }

    @Override
    protected void handleRoleAction(ActionEvent e) {
        if (e.getSource() == requestButton) submitRequest();
        if (e.getSource() == returnButton) processReturn(
                (String) returnTypeBox.getSelectedItem(), returnNameField.getText());
    }

    private void submitRequest() {
        String employeeName = employeeField.getText().trim();
        LibraryItem item = selectedItem();
        if (employeeName.isEmpty() || item == null) {
            if (employeeName.isEmpty()) LibraryGUI.message(this, "Enter the employee name.");
            return;
        }
        User customer = null;
        if (purposeBox.getSelectedItem() == UsePurpose.RENT_TO_CUSTOMER) {
            String customerName = customerField.getText().trim();
            if (customerName.isEmpty()) {
                LibraryGUI.message(this, "Enter the rental customer name.");
                return;
            }
            customer = context.customer(customerName);
        }
        //Create a new employee in the database and submit a request for that employee
        context.employee(employeeName).requestUse(item,
                (UsePurpose) purposeBox.getSelectedItem(), customer);
        LibraryGUI.message(this, "Use request submitted.");
    }
}

class MasterGUI extends RoleGUI {
    private final JTextField librarianField = new JTextField(8);
    private final JTextField returnNameField = new JTextField(8);
    private final JTextField titleField = new JTextField(8);
    private final JTextField genreField = new JTextField(7);
    private final JTextField ratingField = new JTextField(4);
    private final JTextField detailField = new JTextField(8);
    private final JTextField narratorField = new JTextField(8);
    private final JComboBox<String> returnTypeBox = new JComboBox<>(
            new String[] {"Customer return", "Employee return", "Repaired item"});
    private final JComboBox<String> itemTypeBox = new JComboBox<>(
            new String[] {"Book", "Magazine", "Movie", "AudioBook"});
    private final JComboBox<UseRequest> requestBox = new JComboBox<>();
    private final JButton returnButton = new JButton("Process Return");
    private final JButton approveButton = new JButton("Approve Request");
    private final JButton rejectButton = new JButton("Reject Request");
    private final JButton refreshRequestsButton = new JButton("Refresh Requests");
    private final JButton addButton = new JButton("Add New Item");

    MasterGUI(LibraryGUI roleMenu, LibraryContext context, InventoryTable table) {
        super("Master Librarian", roleMenu, context, table);
        setSize(400, 180);
        row2.add(new JLabel("Librarian:")); row2.add(librarianField);
        row2.add(returnTypeBox); row2.add(new JLabel("Returner:")); row2.add(returnNameField); row2.add(returnButton);
        row3.add(new JLabel("Request:")); row3.add(requestBox); row3.add(approveButton); row3.add(rejectButton);
        row3.add(refreshRequestsButton);
        row4.add(new JLabel("New item:")); row4.add(itemTypeBox);
        row4.add(titleField); row4.add(genreField); row4.add(ratingField); row4.add(detailField); row4.add(narratorField); row4.add(addButton);
        returnButton.addActionListener(this);
        approveButton.addActionListener(this);
        rejectButton.addActionListener(this);
        refreshRequestsButton.addActionListener(this);
        addButton.addActionListener(this);
        refreshRequests();
        setSize(1050, 230);
    }

    @Override
    void open() {
        refreshRequests();
        super.open();
    }

    @Override
    protected void handleRoleAction(ActionEvent e) {
        if (e.getSource() == returnButton) processReturn(
                (String) returnTypeBox.getSelectedItem(), returnNameField.getText());
        if (e.getSource() == approveButton) decide(true);
        if (e.getSource() == rejectButton) decide(false);
        if (e.getSource() == refreshRequestsButton) refreshRequests();
        if (e.getSource() == addButton) addItem();
    }

    private MasterLibrarian librarian() {
        String name = librarianField.getText().trim();
        if (name.isEmpty()) {
            LibraryGUI.message(this, "Enter the master librarian name.");
            return null;
        }
        return context.librarian(name);
    }

    private void decide(boolean approve) {
        MasterLibrarian librarian = librarian();
        UseRequest request = (UseRequest) requestBox.getSelectedItem();
        if (librarian == null || request == null) return;
        if (approve) {
            LocalDate today = LocalDate.now();
            Loan loan = librarian.approve(request, today.getYear(), today.getMonthValue(), today.getDayOfMonth());
            LibraryGUI.message(this, loan == null ? "Request approved." : Loan.loanMessage(loan));
        } else {
            librarian.reject(request);
            LibraryGUI.message(this, "Request rejected.");
        }
        refreshRequests();
        table.update();
    }

    private void refreshRequests() {
        requestBox.removeAllItems();
        for (UseRequest request : context.inventory.getPendingRequests()) {
            requestBox.addItem(request);
        }
    }

    private void addItem() {
        MasterLibrarian librarian = librarian();
        if (librarian == null) return;
        try {
            String type = (String) itemTypeBox.getSelectedItem();
            String title = titleField.getText().trim();
            String genre = genreField.getText().trim();
            String detail = detailField.getText().trim();
            double rating = Double.parseDouble(ratingField.getText().trim());
            if (title.isEmpty() || genre.isEmpty() || detail.isEmpty()) throw new IllegalArgumentException();
            LibraryItem item;
            if (type.equals("Book")) item = new Book("", title, detail, genre, rating, true);
            else if (type.equals("Magazine")) item = new Magazine("", title,
                    Integer.parseInt(detail), genre, rating, true);
            else if (type.equals("Movie")) item = new Movie("", title, detail, genre, rating, true);
            else item = new AudioBook("", title, detail, narratorField.getText().trim(), genre, rating, true);
            librarian.addNewItem(item);
            LibraryGUI.message(this, "New available item added: " + item.getItemId());
            table.update();
        } catch (IllegalArgumentException e) {
            LibraryGUI.message(this, "Enter title, genre, rating, and creator or magazine issue.");
        }
    }
}
