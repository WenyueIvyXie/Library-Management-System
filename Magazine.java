public class Magazine extends LibraryItem{
    private int issueNumber;

    public Magazine(String itemId, String title, int issueNumber,String genre, double rating, boolean available){
        super(itemId, title, genre, rating, available);
        this.issueNumber = issueNumber;
    }

    @Override
    public int getIssueNumber(){
        return this.issueNumber;
    }
}
