public class Movie extends LibraryItem{
    private String director;

    public Movie(String itemId, String title, String director,String genre, double rating, boolean available){
        super(itemId,title, genre, rating, available);
        this.director = director;
    }

    @Override
    public String getAuthor(){
        return this.director;
    }
}
