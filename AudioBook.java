public class AudioBook extends LibraryItem{
    private String author;
    private String narrator;

    public AudioBook(String itemId, String title, String author,String narrator, String genre, double rating, boolean available){
        super(itemId, title, genre, rating, available);
        this.author = author;
        this.narrator = narrator;
    }

    @Override
    public String getAuthor(){
        return this.author;
    }

    public String getNarrator() {
        return this.narrator;
    }
}
