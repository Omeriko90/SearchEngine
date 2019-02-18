package Objects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class City implements Serializable {
    String country;
    String population;
    String currency;
    HashMap<String,LinkedList<Integer>> docAppAndLocation;

    //Consolidates the total number of documents retrieved
    public City(String cou, String pop, String curr, String docId) {
        country=cou;
        population=pop;
        currency=curr;
        docAppAndLocation=new HashMap<>();
        LinkedList<Integer> newLoc=new LinkedList<>();
        newLoc.add(1);
        docAppAndLocation.put(docId,newLoc);
    }

    //getters
    public String getCountry() {
        return country;
    }

    public String getPopulation() {
        return population;
    }

    public String getCurrency() {
        return currency;
    }

    //to string implement
    public String toString(){
        String docValue="";
        for (Map.Entry entry:docAppAndLocation.entrySet()) {
            docValue=docValue+"docId:" +(String)entry.getKey();
            for(int i=0;i<((LinkedList<Integer>)entry.getValue()).size();i++)
                docValue+=" ,"+((LinkedList<Integer>)entry.getValue()).get(i);
        }
        String print="The country: "+country+" The population: "+population+" The currency: "+currency+" "+docValue;
        return print;
    }

    public HashMap<String, LinkedList<Integer>> getDocAppAndLocation() {
        return docAppAndLocation;
    }

    //function add values city to the HashMap
    public void addLocation(String docId,String[]location){
        if(docAppAndLocation.containsKey(docId)) {
            for (int i = 0; i < location.length; i++) {
                if (matchesOnlyNumbers(location[i]))
                    docAppAndLocation.get(docId).add(Integer.valueOf(location[i]));
            }
        }
        else{
            LinkedList<Integer> loc=new LinkedList<>();
            for (int i = 0; i < location.length; i++) {
                if (matchesOnlyNumbers(location[i]))
                    loc.add(Integer.valueOf(location[i]));
            }
            docAppAndLocation.put(docId,loc);
        }
    }

    //our implements to string function
    private boolean matchesOnlyNumbers(String word){
        for(int i=0;i<word.length();i++){
            if(word.charAt(i)<'0' || word.charAt(i)>'9')
                return false;
        }
        return true;
    }

    //to return the city app most in doc
    public String[] countOfLocation(){
        String[] maxAppInDoc=new String[2];
        maxAppInDoc[1]="0";
        for (Map.Entry entry:docAppAndLocation.entrySet()) {
            if(((LinkedList<Integer>)entry.getValue()).size()>Integer.valueOf(maxAppInDoc[1])) {
                maxAppInDoc[1] = String.valueOf(((LinkedList<Integer>) entry.getValue()).size());
                maxAppInDoc[0]=(String)entry.getKey();
            }
        }
        return maxAppInDoc;
    }

}
