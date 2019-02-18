/**
 * This class represent a single document and stores relevant information on the document for future use when creating/updating the posting file.
 * The information that is being stored is:
 * 1.The frequency of the term that appears the most in the document
 * 2.The number of unique terms in the document (unique term is a term that appears only once in the document)
 * 3.The name of the city that represents the country the document came from.
 */

package Objects;

import java.io.Serializable;

public class Doc implements Serializable {
    private int max_tf;
    private int uniqueTerm;
    private int docLength;
    private String representativeCity;


    //the object Doc to save all relevant details together
    public Doc(String city, int maxTF, int numOfUT, int length) {
        max_tf = maxTF;
        uniqueTerm = numOfUT;
        representativeCity = city;
        docLength = length;
    }

    /**
     * getter for the number of the frequent term in the document
     *
     * @return integer
     */
    public int getMax_tf() {
        return max_tf;
    }

    /**
     * setter for the the number of the frequent term in the document
     *
     * @param max_tf
     */
    public void setMax_tf(int max_tf) {
        this.max_tf = max_tf;
    }

    /**
     * getter for the number of the unique terms in the document
     *
     * @return integer
     */
    public int getUniqueTerm() {
        return uniqueTerm;
    }

    /**
     * setter for the number of the unique terms in the document
     *
     * @param uniqueTerm
     */
    public void setUniqueTerm(int uniqueTerm) {
        this.uniqueTerm = uniqueTerm;
    }

    /**
     * getter for the name of the city that represents the country the document came from
     *
     * @return String
     */
    public String getRepresentativeCity() {
        return representativeCity;
    }

    /**
     * setter for the city name that represents the country the document came from
     *
     * @param representativeCity
     */
    public void setRepresentativeCity(String representativeCity) {
        this.representativeCity = representativeCity;
    }

    public int getLength() {
        return docLength;
    }

    //to string implements
    public String toString() {
        String ans = "";
        if(!representativeCity.equals("<f p=\"104\"></f>") && representativeCity.length()!=0)
            ans = String.valueOf(representativeCity)+'#'+" max term frequency: "+String.valueOf(max_tf)+" number of unique term: "+String.valueOf(uniqueTerm)+" Document length: "+String.valueOf(docLength);
        else
            ans = "none#"+" max term frequency: "+String.valueOf(max_tf)+" number of unique term: "+String.valueOf(uniqueTerm)+" Document length: "+String.valueOf(docLength);
        return ans;
    }
}



