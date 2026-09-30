import java.util.ArrayList;
public class Inventory {
    private ArrayList<LibraryItem> items;
    private int nextidNum;
    protected ArrayList<Reservation> reservations = new ArrayList<>();
    protected ArrayList<UseRequest> requests = new ArrayList<>();

    public Inventory(ArrayList<LibraryItem> items){
        this.items = items;
        this.nextidNum = items.size();
    }

    //Search items by String criteria, ex. author, title, genre
    public ArrayList<LibraryItem> search(String criteria){
        ArrayList<LibraryItem> searchResults = new ArrayList<>();
        for (LibraryItem item : items){
            String author = item.getAuthor();
            if (item.getTitle().contains(criteria) || item.getGenre().contains(criteria)
                    || (author != null && author.contains(criteria))) {
                    searchResults.add(item);
            }
        }
        return searchResults;
    }
    //Search items by rating
    public ArrayList<LibraryItem> search(double rating){
        ArrayList<LibraryItem> searchResults = new ArrayList<>();
        for (LibraryItem item : items){
            if (item.getRating() >= rating){ //Display any item with rating greater than or equal to user's request
                searchResults.add(item);
            }
        }
        return searchResults;
    }
    //Search items by issue number(typically magazine)
    public ArrayList<LibraryItem> search(int issueNumber){
        ArrayList<LibraryItem> searchResults = new ArrayList<>();
        for (LibraryItem item : items){
            if (item.getIssueNumber() == issueNumber){
                searchResults.add(item);
            }
        }
        return searchResults;
    }


    public void addNewItem(LibraryItem item){
        item.setItemId(generateUniqueId(item));
        items.add(item);
    }

    private String generateUniqueId(LibraryItem item){
        String letter = "";
        int number;

        if (item instanceof AudioBook){
            letter = "AB";
        }
        else if (item instanceof Book){
            letter = "B";
        }
        else if (item instanceof Magazine){
            letter = "Ma";
        }
        else if (item instanceof Movie){
            letter = "Mo";
        }
        number = nextidNum++;
        return letter+String.valueOf(number);
    }

    public void removeItem(LibraryItem item, UseRequest request) {
        if (request.isApproved() && request.getPurpose().equals(UsePurpose.DISPOSE)) {
            items.remove(item);
        }
        else if (request.isApproved() &&
                (request.getPurpose().equals(UsePurpose.REPAIR)
            || request.getPurpose().equals(UsePurpose.RENT_TO_CUSTOMER)
            || request.getPurpose().equals(UsePurpose.PERSONAL_USE))){
            item.setAvailability(false);
        }
    }

    public void returnItem(LibraryItem item){
        Reservation match = null;
        for (Reservation reservation : reservations){
            if (item.equals(reservation.getItem()) && reservation.getStatus().equals("pending")){
                match = reservation;
                break;
            }
        }
        if (match != null){
            match.setStatus("held");
        }
        else{
            item.setAvailability(true);
        }
    }

    public void addRepairedItem(LibraryItem item){
        returnItem(item);
    }

    public LibraryItem findItemById(String itemId) {
        for (LibraryItem item : items) {
            if (item.getItemId().equalsIgnoreCase(itemId)) {
                return item;
            }
        }
        return null;
    }

    public ArrayList<UseRequest> getPendingRequests() {
        ArrayList<UseRequest> pendingRequests = new ArrayList<>();
        for (UseRequest request : requests) {
            if (request.getStatus().equals("Pending")) {
                pendingRequests.add(request);
            }
        }
        return pendingRequests;
    }
}
