package Read;

import Index.cityIndexer;
import Objects.City;
import Objects.Term;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

public class ReadFile {
    public File fileReader;
    public Parse parser;
    public cityIndexer cityPos;
    public HashMap<String,City> cities;
    public HashMap<String,String> lango;


    public ReadFile(String path,Boolean stem,String destination,String from){
        this.fileReader = new File(path);
        this.parser = new Parse(stem,destination,from);
        this.cityPos=new cityIndexer();
        cityPos.connectionApi();;
        this.cities=cityPos.getCityPos();
        lango=new HashMap<>();
    }

    /**
     * read file, get details about the file and send to the parser
     */
    public void  readFiles(){
        LinkedList<String> words = new LinkedList<>();
        try {
            Document doc =  Jsoup.parse(new String(Files.readAllBytes(fileReader.toPath())),"UTF-8");
            Elements elements =  doc.getElementsByTag("DOC");
            for (Element element: elements) {
                String docID = element.getElementsByTag("DOCNO").text();
                String city=element.getElementsByTag("F").toString();
                String title = element.getElementsByTag("TI").text();
                String city2="", city3="", city4="";
                String cit[];
                String language="";
                String LAN[];
                if(city.length()>0) {
                    int indexLaN=city.indexOf("<f p=\"105\">");
                    int indexF=0;
                    if(indexLaN>0)
                        indexF=city.indexOf("</f>", city.indexOf("<f p=\"105\">"));
                    int index1=city.indexOf("<f p=\"104\">");
                    int index2=city.indexOf("<f p=\"104\">", city.indexOf("</f>"));
                    if(index1>0 && index2>0)
                        city2 = city.substring(city.indexOf("<f p=\"104\">", city.indexOf("</f>")));
                    else
                        city2="";
                    if(city2.length()>15){
                        city2=city2.substring(city2.indexOf("\n "),city2.indexOf(" \n"));
                        city2=city2.replaceAll("\n","");
                        cit=city2.split(" ");
                        city2=cit[2].toUpperCase();
                        if(cit.length>4) {
                            city4 = cit[4].toUpperCase();
                            city3 = cit[3].toUpperCase();
                        }
                        else if(cit.length>3)
                            city3=cit[3].toUpperCase();
                    }
                    if(indexLaN>0 && indexF>0) {
                        language = city.substring(indexLaN, city.indexOf("</f>",indexF));
                        language=language.substring(language.indexOf("\n "),language.indexOf(" \n"));
                        language=language.replaceAll("\n","");
                        LAN=language.split(" ");
                        int i=0;
                        while (i<LAN.length){
                            if(LAN[i].length()==0)
                                i++;
                            else{
                                language=LAN[i];
                                break;
                            }
                        }
                        if(!lango.containsKey(language.toLowerCase()))
                            lango.put(language,"");
                    }

                }
                String represtCity =city2;
                if(cities.containsKey(city2+" "+city3+" "+city4))
                    represtCity=city2+" "+city3+" "+city4;
                else if(cities.containsKey(city2+" "+city3))
                    represtCity=city2+" "+city3;
                else
                    represtCity=city2;
                String text = element.getElementsByTag("TEXT").text() +" "+ title;
                String[] wordsToParse = text.split(" ");
                System.out.println(docID);
                if(represtCity.length()>0) {
                    if (cities.containsKey(represtCity.toUpperCase()))
                        parser.newParser(wordsToParse, docID, represtCity, cities.get(represtCity).getCountry(), cities.get(represtCity).getPopulation(), cities.get(represtCity).getCurrency());
                    else
                        parser.newParser(wordsToParse, docID, represtCity, "", "", "");
                }
                else
                    parser.newParser(wordsToParse, docID, represtCity,"","","");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public int dicSize(){return parser.dicSize();}

    public HashMap<String, String> getLango() {
        return lango;
    }

    public TreeMap<String, String> printDic(){
        return parser.printDic();
    }

//    public TreeMap<String, Term> printDic(){
//        return parser.printDic();
//    }


    public void toUpperCase() {
        parser.toUpperCase();
    }

    public void writeAll(String dirID) {
        parser.writePostingFiles(dirID);
    }

    public void resetDic(String destination) {
        parser.resetDic(destination);
    }

    public void loadDictionary(String stem,String destination){
        parser.loadDictionary(stem,destination);
    }
    public void setPath(String path){
        fileReader= new File(path);
    }

    public void mergeration() {
        parser.mergeration();
    }

    public int numOfDoc(){return parser.numOfDoc();}

    public void clearCity(){
        cities.clear();
    }

    public int getDicSize(){return parser.getDicSize();}

    public HashMap<String,City> getcities(String stem,String destination) {
        return parser.getCities(stem,destination);
    }
}
