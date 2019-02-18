/**
 * This class represent a single term a stores a relevant information on the specific term for future use when creating/updating the posting file.
 * The information that is being stored is:
 * 1.The number of documents the term appears in.
 * 2.A hash map where the keys are the document ID and the values are the number of appearances of the term in the document (for mapping
 * the documents the terms appear in and the number of appearence per document).
 */

package Objects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class Term implements Serializable {
    private HashMap<String,Integer> docAppearances;
    private HashMap<String,LinkedList<Integer>> docLocation;

//default constructor
    public Term(){
        docAppearances = new HashMap<>();
        docLocation = new HashMap<>();
    }
    public Term(String docId,int location){
        docAppearances = new HashMap<>();
        docLocation = new HashMap<>();
        docAppearances.put(docId,1);
        docLocation.put(docId,(new LinkedList<Integer>()));
        docLocation.get(docId).add(location);
    }

    /**
     * Adds new document to the hash map
     * @param docId
     */
    private void add(String docId,int location){
        docAppearances.put(docId,1);
        docLocation.put(docId,new LinkedList<Integer>());
        docLocation.get(docId).add(location);
    }

    /**
     * Updates the number of appearances of a term in the document. if the document doesn't exists in the hash map it turns to the add
     * function.
     * @param docId
     */
    public void setNum(String docId,int location){
        if(!docAppearances.containsKey(docId))
            add(docId,location);
        else{
            int numberOfAppearances = docAppearances.get(docId);
            numberOfAppearances++;
            docAppearances.replace(docId,numberOfAppearances);
            docLocation.get(docId).add(location);
        }

    }

    /**
     * getter for the number of appearances of a the term in a specific document. if the document doesn't exists in the hash map the function returns 0
     * @param docId
     * @return integer
     */
    public Integer getNum(String docId){
        if(docAppearances.containsKey(docId))
            return docAppearances.get(docId);
        return 0;
    }

    /**
     * returns the document ID's that is in the hash map
     * @return Hash map
     */
    public HashMap<String,Integer> getDocAppearances(){
        return docAppearances;
    }
    public HashMap<String,LinkedList<Integer>> getDocLocation(){return docLocation;}
    public LinkedList<Integer> getLocations(String docID){
        return docLocation.get(docID);
    }
    public boolean hasDoc(String docId){return docAppearances.containsKey(docId);}
    /**
     * returns the number of documents the term appeared in
     * @return integer
     */
    public int numOfDocs(){
        return docAppearances.size();
    }

    /**
     * prints the objects fields in the format #numOfdocs | docID:#numOfAppearances
     * @return String
     */
    public String toString(){
        String term = String.valueOf(numOfDocs())+","+String.valueOf(totalApp())+"#";
        for (Map.Entry entry: docAppearances.entrySet()) {
            term+=entry.getKey()+":"+entry.getValue()+" ";
            //term+=entry.getKey()+":"+entry.getValue()+"-Locations-";
//            LinkedList<Integer> locations = getLocations((String)entry.getKey());
//            for(int i=0;i<locations.size();i++) {
//                term += locations.get(i);
//                if(i!=locations.size()-1)
//                    term+=",";
//                else
//                    term+=", ";
//            }
        }
        return term;
    }

    //return the total appearance in the corpus
    public int totalApp(){
        int totalCount=0;
        for (Map.Entry entry: docAppearances.entrySet()) {
            totalCount+=(int)entry.getValue();
        }
        return totalCount;
    }

    //merge term if the term exist, add to the list of doc the new doc, the num of appearance and the specific location
    public void mergingTerms(String docID,int numOfAppearances,String[] locations){
        if(!docAppearances.containsKey(docID))
            docAppearances.put(docID,numOfAppearances);
        else{
            int tmp=docAppearances.get(docID);
            tmp+=numOfAppearances;
            docAppearances.replace(docID,tmp);
        }
        if(!docLocation.containsKey(docID))
            docLocation.put(docID,new LinkedList<>());

        for(int i=0;i<locations.length;i++) {
            if(matchesOnlyNumbers(locations[i]))
                docLocation.get(docID).add(Integer.valueOf(locations[i]));
        }
    }

    //using for the merge letter add the num of appearance of the big letter to the small letter
    public void addToDocAppearacnces(HashMap<String,Integer> termToMerge){
        for (Map.Entry entry : termToMerge.entrySet()) {
            if(docAppearances.containsKey(entry.getKey())){
                int tmp=docAppearances.get(entry.getKey());
                tmp+=(int)entry.getValue();
                docAppearances.replace((String)entry.getKey(),tmp);
            }
            else
                docAppearances.put((String)entry.getKey(),(Integer)entry.getValue());
        }
    }

    //using for the merge letter add the location in the doc of the big letter to the small letter
    public void addToDocLocations(HashMap<String,LinkedList<Integer>> termToMerge){
//        for (Map.Entry entry : termToMerge.entrySet()) {
//            if(docLocation.containsKey(entry.getKey())){
//                LinkedList<Integer> tmp = (LinkedList<Integer>) entry.getValue();
//                for(int i=0;i<tmp.size();i++)
//                    docLocation.get(entry.getKey()).add(tmp.get(i));
//            }
//            else
//                docLocation.put((String)entry.getKey(),(LinkedList<Integer>) entry.getValue());
//        }
    }


    //implement string function if the string contain only number
    private boolean matchesOnlyNumbers(String word){
        for(int i=0;i<word.length();i++){
            if(word.charAt(i)<'0' || word.charAt(i)>'9')
                return false;
        }
        return true;
    }
}