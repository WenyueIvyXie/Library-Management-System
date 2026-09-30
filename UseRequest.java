public class UseRequest {
    private User customer;
    private UsePurpose purpose;
    private String status = "Pending";
    private LibraryItem item;
    private Employee requester;

    public UseRequest(LibraryItem item, UsePurpose purpose, Employee requester, User customer){
        this.item = item;
        this.purpose = purpose;
        this.requester = requester;
        this.customer = customer;
    }


    public boolean isApproved(){
        return status.equals("Approved");
    }
    public UsePurpose getPurpose(){
        return this.purpose;
    }
    public LibraryItem getItem(){
        return this.item;
    }
    public Employee getRequester(){
        return this.requester;
    }

    public User getCustomer() {
        return customer;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status){
        this.status = status;
    }

    @Override
    public String toString() {
        return item.getTitle() + " - " + purpose + " (requested by "
                + requester.getName() + ")";
    }
}
