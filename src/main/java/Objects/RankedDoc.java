package Objects;

public class RankedDoc {
    private String docID;
    private double rank;
    private String queryID;
    private String city;

    //the object Ranked Doc to save all relevant details together
    public RankedDoc(String id,String queryNumber, String representCity){
        docID=id;
        queryID = queryNumber;
        rank=0;
        city=representCity;
    }

    //setters and getters
    public void setRank(double rank) {
        this.rank = rank;
    }

    public double getRank() {
        return rank;
    }

    public String getDocID() {
        return docID;
    }

    public String getQueryID() {
        return queryID;
    }

    public String getCity() { return city; }
}
