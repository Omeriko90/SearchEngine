package Index;

import Objects.Doc;
import Objects.RankedDoc;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Ranker {
    private HashMap<String,String> query;
    private String path;
    private HashMap<String,HashMap<String,String>> terms;
    private HashMap<String, Doc> docs;
    private HashMap<String,String[]> postingDocs;
    private boolean stemUsed;
    private HashMap<String,String> termsFromPosting;
    private HashMap<String,RankedDoc> rankedDocs;
    private boolean fromQueryTitel;
    public static double advl;

    public Ranker(HashMap<String, String> query, String to, boolean stem,boolean title) {
        advl=0;
        rankedDocs=new HashMap<>();
        this.query = query;
        postingDocs = new HashMap<>();
        terms = new HashMap<>();
        docs = new HashMap<>();
        fromQueryTitel=title;
        path = to;
        stemUsed=stem;
        termsFromPosting = new HashMap<>();
        loadDocsDetails();
    }

    public HashMap<String, Doc> getDocs() {
        return docs;
    }

    public double getAdvl() {
        return advl;
    }

    /**
     * this function brings the information we save on the documents when the inverted index is created for future use.
     */
    private void loadDocsDetails() {
        if(stemUsed)
            path=path+"\\Stemming";
        else
            path=path+"\\WithOutStemming";
        File[] files = new File(path).listFiles();
        int index = 0;
        Document document=null;
        while(!files[index].getName().equals("docPosting.txt")) {
            index++;
        }
        try {
            document = Jsoup.parse(new String(Files.readAllBytes(files[index].toPath())),"UTF");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements elements = document.getElementsByTag("body");
        String[] allDocs = elements.get(0).text().split("----------------------------------------------------------------------------------------");
        String[] details = allDocs[0].split("#");
        String[] termDetails = details[1].split(" ");
        docs.put(details[0].substring(0, details[0].indexOf(' ')), new Doc(details[0].substring(details[0].indexOf(' ',1) + 1), Integer.valueOf(termDetails[4]), 0, Integer.valueOf(termDetails[termDetails.length - 1])));
        advl=advl+Integer.valueOf(termDetails[termDetails.length - 1]);
        for(int i=1;i<allDocs.length;i++){
            details = allDocs[i].split("#");
            termDetails = details[1].split(" ");
            advl=advl+Integer.valueOf(termDetails[termDetails.length - 1]);
            docs.put(details[0].substring(1,details[0].indexOf(' ',1)),new Doc(details[0].substring(details[0].indexOf(' ',1) + 1),Integer.valueOf(termDetails[4]),0,Integer.valueOf(termDetails[termDetails.length-1])));
        }
        advl=advl/docs.size();
    }

    /**
     * this function finds the posting file for each word in the query.
     */
    public void searchDocs(){
        File[] file = new File(path).listFiles();
        File[] postingFile=null;
        Document document = null;
        int index = 0;

        for (Map.Entry entry: query.entrySet()) {
            String term = (String) entry.getKey();
            switch (((String)entry.getValue())){
                case "date":
                    try {
                        postingFile = file[6].listFiles();
                        if(!postingDocs.containsKey("date"))
                            document = Jsoup.parse(new String(Files.readAllBytes(postingFile[1].toPath())), "UTF-8");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "number":
                    try {
                        postingFile = file[19].listFiles();
                        if(!postingDocs.containsKey("number"))
                            document = Jsoup.parse(new String(Files.readAllBytes(postingFile[1].toPath())),"UTF-8");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "parse":
                    try {
                        postingFile = file[22].listFiles();
                        if(!postingDocs.containsKey("parse"))
                            document = Jsoup.parse(new String(Files.readAllBytes(postingFile[1].toPath())),"UTF-8");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "percent":
                    try {
                        postingFile = file[23].listFiles();
                        if(!postingDocs.containsKey("percent"))
                            document = Jsoup.parse(new String(Files.readAllBytes(postingFile[1].toPath())),"UTF-8");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "price":
                    try {
                        postingFile = file[24].listFiles();
                        if(!postingDocs.containsKey("prices"))
                            document = Jsoup.parse(new String(Files.readAllBytes(postingFile[1].toPath())),"UTF-8");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                    default:
                        char firstLetter = ((String)entry.getKey()).toLowerCase().charAt(0);
                        index = 0;
                        while(index<file.length && file[index].getName().charAt(0) != firstLetter)
                            index++;
                        try {
                            String dirName = term.substring(0,1).toLowerCase();
                            postingFile = file[index].listFiles();
                            if(!postingDocs.containsKey(dirName)) {
                                document = Jsoup.parse(new String(Files.readAllBytes(postingFile[1].toPath())), "UTF-8");
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
            }

            findWord(term,document,postingFile[1].getName(),file[index].getName());
        }
        readyToRank();
    }

    //Look for the words that appear in the query in the posting files
    private void findWord(String term, Document document, String name,String dir){
        String[] words = new String[0];
        if(!postingDocs.containsKey(dir)){
            Elements elements = document.getElementsByTag("body");
            for (Element elem : elements)
                words = elem.text().split("----------------------------------------------------------------------------------------");
            postingDocs.put(dir,words);
        }
        else
            words = postingDocs.get(dir);

        for (int i = 0; i < words.length; i++) {
            int colonIndex = words[i].indexOf(':');
            int hashMarkIndex = words[i].indexOf('#');
            if (name.equals("parse.txt")) {
                if (words[i].substring(1, colonIndex).contains(term))
                    termsFromPosting.put(words[i].substring(0, colonIndex), words[i].substring(hashMarkIndex + 1));
            }
            if (term.equals(words[i].substring(1, colonIndex)) || term.toUpperCase().equals(words[i].substring(1, colonIndex)) || term.toLowerCase().equals(words[i].substring(1, colonIndex))) {
                termsFromPosting.put(words[i].substring(0, colonIndex), words[i].substring(hashMarkIndex + 1));
                break;
            }
        }
    }

    //After you have found the words in the posting files, get all files details to the rank calculate
    private void readyToRank(){
        terms.clear();
        for (Map.Entry entry: termsFromPosting.entrySet()) {
            String term = (String) entry.getKey();
            String[] termDetails = ((String)entry.getValue()).split(" ");
            terms.put(term,new HashMap<>());
            for(int i=0; i< termDetails.length;i++){
                int docIdIndex = termDetails[i].indexOf(':');
                String docId=termDetails[i].substring(0,docIdIndex);
                terms.get(term).put(docId,termDetails[i].substring(docIdIndex+1));
            }
        }
        termsFromPosting.clear();
    }

    /**
     *
     * @return rank the documents according to BM25 calculate
     * @param queryNum
     */
    public HashMap<String,RankedDoc> rankingDocs(String queryNum){
        HashMap<String,RankedDoc> temp = new HashMap<>();
        for (Map.Entry entry: terms.entrySet()) {
            HashMap<String,String> termsDoc = (HashMap<String, String>) entry.getValue();
            for (Map.Entry doc: termsDoc.entrySet()) {
                RankedDoc rankedDoc = new RankedDoc((String)doc.getKey(), queryNum,docs.get(doc.getKey()).getRepresentativeCity());
                String numOfAppString=termsDoc.get(rankedDoc.getDocID());
                while (numOfAppString.charAt(0)==' ')
                    numOfAppString.substring(1);
                while (numOfAppString.charAt(numOfAppString.length()-1)==' ')
                    numOfAppString.substring(0,numOfAppString.length()-1);
                double numOfApp = Double.valueOf(numOfAppString);
                double numerator = Math.log((docs.size() + 1) /termsDoc.size());
                double k=1.2;
                double b=0.6;
                double wordWightInQuery=1;
                double docLength=docs.get(rankedDoc.getDocID()).getLength();
                double finalRank=wordWightInQuery*(((k+1)*numOfApp)/(numOfApp+k*(1-b+b*(docLength/advl))))*numerator;
                if(fromQueryTitel)
                    finalRank=0.7*finalRank;
                else
                    finalRank=0.3*finalRank;
                rankedDoc.setRank(finalRank);

                if(temp.containsKey(rankedDoc.getDocID()) || temp.containsKey(" "+rankedDoc.getDocID())){
                    double rank = temp.get(rankedDoc.getDocID()).getRank();
                    rank+=rankedDoc.getRank();
                    temp.get(rankedDoc.getDocID()).setRank(rank);
                }
                else {
                    temp.put(rankedDoc.getDocID(), rankedDoc);
                }
            }
        }
        rankedDocs=temp;

        return rankedDocs;
    }

    //bring strong entity according to the final best docs
    public HashMap<String,String> bringStrongEntity(String to, boolean stem) {
        HashMap<String,String>strongEntity=new HashMap<>();
        if(stem)
            to=to+"\\Stemming";
        else
            to=to+"\\WithOutStemming";
        File docEntity=new File(to+"\\docsEntity");
        File[] folders = new File(docEntity.getAbsolutePath()).listFiles();
        for (int j = 0; j < folders.length; j++) {
            if (folders[j].isFile() && (folders[j].getName()).equals("docs.txt")) {
                Document doc = null;
                try {
                    doc = Jsoup.parse(new String(Files.readAllBytes(folders[j].toPath())));
                    Elements elements = doc.getElementsByTag("body");
                    for (Element elem : elements) {
                        String[] lines = elem.text().split("----------------------------------------------------------------------------------------");
                        for (int k = 0; k < lines.length; k++) {
                            String[] docsDetail = lines[k].split("#");
                            strongEntity.put(docsDetail[0],docsDetail[1]);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        HashMap<String,String>finalEntity=new HashMap<>();
            for (Map.Entry entry:rankedDocs.entrySet()) {
                if(strongEntity.containsKey(" "+rankedDocs.get(entry.getKey()).getDocID()))
                    finalEntity.put(rankedDocs.get(entry.getKey()).getDocID(),strongEntity.get(" "+rankedDocs.get(entry.getKey()).getDocID()));
                else if(strongEntity.containsKey(rankedDocs.get(entry.getKey()).getDocID()))
                    finalEntity.put(rankedDocs.get(entry.getKey()).getDocID(),strongEntity.get(rankedDocs.get(entry.getKey()).getDocID()));
            }


        return finalEntity;
    }
    public void setQuery(HashMap<String,String> newQuery){ query=newQuery;}

    public void setTitle(boolean title) {
        fromQueryTitel = title;
    }
}
