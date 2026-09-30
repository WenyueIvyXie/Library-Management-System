public class Book extends LibraryItem{
    private String author;

    public Book(String itemId, String title, String author,String genre, double rating, boolean available){
        super(itemId, title,genre, rating, available);
        this.author = author;
    }

    @Override
    public String getAuthor(){
        return this.author;
    }
}
