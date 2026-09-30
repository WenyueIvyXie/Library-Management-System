import java.time.LocalDate;
public class Loan {
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private LibraryItem item;

    public Loan(LibraryItem item, int year, int month, int date){
        this.item = item;
        this.loanDate = LocalDate.of(year, month, date);
        this.dueDate = loanDate.plusDays(14);
    }

    public void close(int year, int month, int day){
        this.returnDate = LocalDate.of(year, month, day);
    }
    public boolean isOpen(){
        return returnDate == null;
    }

    public LibraryItem getItem(){
        return item;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public static String loanMessage(Loan loan) {
        return "Loan created. Loan date: " + loan.getLoanDate()
                + ", due date: " + loan.getDueDate();
    }
}
