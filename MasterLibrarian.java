import java.util.ArrayList;

public class MasterLibrarian extends Employee{
    public MasterLibrarian(String userId, String name, Inventory inventory){
        super(userId, name, inventory);
    }

    public Loan approve(UseRequest request, int year, int month, int day){
        if (!request.getStatus().equals("Pending")) return null;
        request.setStatus("Approved");
        inventory.removeItem(request.getItem(), request);

        if (request.getPurpose() == UsePurpose.PERSONAL_USE){
            User u = request.getRequester();
            Loan loan = new Loan(request.getItem(), year, month, day);
            u.addLoan(loan);
            return loan;
        } else if (request.getPurpose() == UsePurpose.RENT_TO_CUSTOMER){
            User c = request.getCustomer();
            Loan loan = new Loan(request.getItem(), year, month, day);
            c.addLoan(loan);
            return loan;
        }
        return null;
    }

    public void reject(UseRequest request){
        if(!request.getStatus().equals("Pending")) return;
        request.setStatus("Rejected");
    }

    public void addNewItem(LibraryItem item){
        inventory.addNewItem(item);
    }

}
