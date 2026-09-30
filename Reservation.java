import java.time.LocalDate;
public class Reservation {
    private LocalDate requestDate;
    private User user;
    private LibraryItem item;
    private String status;

    public Reservation(User user, LibraryItem item, int year, int month, int day){
        this.user = user;
        this.item = item;
        this.requestDate = LocalDate.of(year, month, day);
        this.status = "pending";
    }

    public String getStatus(){
        return this.status;
    }
    public void setStatus(String status){
        this.status = status;
    }
    public LibraryItem getItem(){
        return this.item;
    }
    public User getUser(){
        return this.user;
    }


}
