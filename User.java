import java.util.ArrayList;
public class User {
    private String userId;
    private String name;
    protected Inventory inventory;
    private ArrayList<Loan> loans = new ArrayList<>();
    private ArrayList<Reservation> reservations = new ArrayList<>();

    public User(String userId, String name, Inventory inventory) {
        this.userId = userId;
        this.name = name;
        this.inventory = inventory;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getName() {
        return this.name;
    }

    public Loan borrowItem(LibraryItem item, int year, int month, int day) {
        if (item.isAvailable()) {
            Loan loan = new Loan(item, year, month, day);
            addLoan(loan);
            item.setAvailability(false);
            return loan;
        }
        Reservation held = findHeldReservation(item);
        if (held != null) {
            Loan loan = new Loan(item, year, month, day);
            addLoan(loan);
            held.setStatus("fulfilled");
            return loan;
        } else {
            Reservation reservation = new Reservation(this, item, year, month, day);
            addReservation(reservation);
            addReservationToInventory(reservation);
            return null;
        }
    }

    public void addLoan(Loan l) {
        loans.add(l);
    }

    public void addReservation(Reservation r) {
        reservations.add(r);
    }

    private Reservation findHeldReservation(LibraryItem item) {
        for (Reservation r : reservations) {
            if (r.getItem().equals(item) && r.getStatus().equals("held")) {
                return r;
            }
        }
        return null;
    }

    public void returnItem(LibraryItem item, int year, int month, int day) {
        for (Loan l : loans) {
            if (l.getItem().equals(item) && l.isOpen()) {
                l.close(year, month, day);
                inventory.returnItem(item);
                return;
            }
        }
    }

    public boolean hasOpenLoan(LibraryItem item) {
        for (Loan loan : loans) {
            if (loan.getItem().equals(item) && loan.isOpen()) {
                return true;
            }
        }
        return false;
    }
    public void addReservationToInventory(Reservation r){
        inventory.reservations.add(r);
    }
}
