public abstract class LibraryItem {
    private String itemId;
    private String title;
    private String genre;
    private double rating;
    private boolean available;

    public LibraryItem(String itemId, String title, String genre, double rating, boolean available){
        this.itemId = itemId;
        this.title = title;
        this.genre = genre;
        this.rating = rating;
        this.available = available;
    }

    public String getTitle(){
        return this.title;
    }
    public String getGenre(){
        return this.genre;
    }
    public double getRating(){
        return this.rating;
    }
    public String getAuthor(){
        return null;
    }
    public int getIssueNumber() {
        return -1;
    }
    public String getItemId(){
        return this.itemId;
    }
    public void setItemId(String id){
        this.itemId = id;
    }
    public boolean isAvailable(){
        return this.available;
    }
    public void setAvailability(boolean available){
        this.available = available;
    }
}
