package Read;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import Index.Ranker;
import Objects.Doc;
import Objects.RankedDoc;
import Objects.Term;
import Index.Semantic;
import javafx.scene.control.Alert;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Searcher {

    public HashMap<String, HashMap<String,Term>>queries;
    public Parse parse;
    public HashMap<String,String>termQueries;
    public HashMap<String,String>termNarrative;
    public HashMap<String,String>termDescription;
    public File fileReader;
    public Semantic Semantic;
    private LinkedList<RankedDoc[]> fiftyBestDocs;
    private Ranker ranker;
    private String pathTo;

    //The central function of retrieval where everything is done for both a single query and a query file
    public Searcher(boolean stem, String query, String from, String to,String toQueriesResult, boolean checkSemantic,LinkedList<String> cityFilter, String type){
        termQueries=new HashMap<>();
        termDescription=new HashMap<>();
        termNarrative=new HashMap<>();
        Semantic=new Semantic();
        fiftyBestDocs = new LinkedList<>();
        pathTo=toQueriesResult;
        ranker = new Ranker(termQueries,to,stem,true);
        HashMap<String,Integer>semanticTerm=new HashMap<>();
        int queryNum = (int)(Math.random() * 50 + 1);
        //when the user choose to type the query
        if(type.equals("typeQuery")) {
            parse = new Parse(stem, to, from);
            String [] queryToParse=query.split(" ");
            String[] queryToParseSemantic=new String[1];
            if(checkSemantic) {
                semanticTerm=chooseSemantic(queryToParse);
                queryToParseSemantic = new String[semanticTerm.size()];
                int i = 0;
                for (Map.Entry entry : semanticTerm.entrySet()) {
                    queryToParseSemantic[i] = (String) entry.getKey();
                    i++;
                }
            }
            parse.newParser(queryToParse,String.valueOf(queryNum),"","","","");
            addDictionary();
            ranker.setQuery(termQueries);
            ranker.setTitle(true);
            ranker.searchDocs();
            HashMap<String,RankedDoc> ranks = ranker.rankingDocs(String.valueOf(queryNum));
            termQueries.clear();
            for (int k=0;k<queryToParseSemantic.length;k++)
                termQueries.put(queryToParseSemantic[k],"semantic");
            ranker.setQuery(termQueries);
            ranker.setTitle(false);
            ranker.searchDocs();
            HashMap<String,RankedDoc> ranksSemantic = ranker.rankingDocs(String.valueOf(queryNum));
            ranks=joinRanking(ranks,ranksSemantic);
            HashMap<String,RankedDoc> temp = new HashMap<>();
            if(cityFilter.size()>0) {
                for (Map.Entry entry : ranks.entrySet()) {
                    if (cityFilter.contains(((RankedDoc) entry.getValue()).getCity()))
                        temp.put((String) entry.getKey(), (RankedDoc) entry.getValue());
                }

                ranks = temp;
            }
            getBestDocs(ranks);
        }
        //when the user choose to check file of queries
        else{
            fileReader=new File(query);
            try {
                Document doc = Jsoup.parse(new String(Files.readAllBytes(fileReader.toPath())), "UTF-8");
                Elements elements = doc.getElementsByTag("top");
                for (Element element : elements) {
                    parse = new Parse(stem, to, from);
                    String [] narrative=element.text().split("Narrative:");
                    String [] text=narrative[0].split(" ");
                    String queryNumber = "";
                    String queryTitle = "";
                    String queryDescription="";
                    LinkedList<String> queryNarrative=new LinkedList<>();
                    for (int i=0;i<text.length;i++) {
                        if (text[i].equals("Number:"))
                            queryNumber = text[i + 1];
                        int j = i + 3;
                        if (j < text.length) {
                            while (j<text.length &&!text[j].equals("\r\n\r\n") && !text[j].contains("\r\n\r\n") && !text[j].equals("\n\n") && !text[j].equals("\n") && !text[j].contains("\n")) {
                                queryTitle = queryTitle + " " + text[j];
                                j++;
                            }
                            if (j<text.length && text[j].contains("\r\n\r\n") && !text[j].equals("\r\n\r\n") && text[j].contains("\n") && !text[j].equals("\n")) {
                                int index = text[j].indexOf("\r");
                                queryTitle = queryTitle + text[j].substring(0, index);
                            }
                            while (j<text.length && !text[j].equals("Description:")  )
                                j++;
                            j = j + 1;
                            while (j < text.length) {
                                queryDescription = queryDescription + " " + text[j];
                                j++;
                            }
                            String[] relevant = new String[0];
                            if(narrative.length>1)
                                relevant = narrative[1].split("relevant");
                            boolean relevantNarrative = true;
                            for (int k = 0; k < relevant.length; k++) {
                                String[] tempRel = relevant[k].split(" ");
                                for (int m = 0; m < tempRel.length; m++) {
                                    if (tempRel[m].equals("not") || tempRel[m].equals("non")) {
                                        relevantNarrative = false;
                                        break;
                                    }
                                    if (relevantNarrative) {
                                        for (int y = 0; y < tempRel[m].length(); y++) {
                                            if (!queryNarrative.contains(tempRel[m]))
                                                queryNarrative.add(tempRel[m]);
                                        }
                                    }
                                }
                            }
                            break;
                        }
                    }
                    String [] queryNarrativeToParse=new String[queryNarrative.size()];
                    for(int i=0;i<queryNarrative.size();i++)
                        queryNarrativeToParse[i]=queryNarrative.get(i);
                    String [] queryToParse=queryTitle.split(" ");
                    String[] queryToParseSemantic=new String[1];
                    if(checkSemantic) {
                        semanticTerm=chooseSemantic(queryToParse);
                        queryToParseSemantic = new String[semanticTerm.size()];
                        int i = 0;
                        for (Map.Entry entry : semanticTerm.entrySet()) {
                            queryToParseSemantic[i] = (String) entry.getKey();
                            i++;
                        }
                    }
                    String [] queryDescriptionToPars=queryDescription.split(" ");
                    parse.newParser(queryToParse,queryNumber,"","","","");
                    addDictionary();
                    ranker.setQuery(termQueries);
                    ranker.setTitle(true);
                    ranker.searchDocs();
                    HashMap<String,RankedDoc> ranks = ranker.rankingDocs(queryNumber);
                    termQueries.clear();
                    for (int k=0;k<queryToParseSemantic.length;k++)
                        termQueries.put(queryToParseSemantic[k],"semantic");
                    ranker.setQuery(termQueries);
                    ranker.setTitle(false);
                    ranker.searchDocs();
                    HashMap<String,RankedDoc> ranksSemantic = ranker.rankingDocs(queryNumber);
                    ranks=joinRanking(ranksSemantic,ranks);
                    HashMap<String,RankedDoc> temp = new HashMap<>();
                        if(cityFilter.size()>0) {
                        for (Map.Entry entry : ranks.entrySet()) {
                            if (cityFilter.contains("  "+((RankedDoc) entry.getValue()).getCity()) ||
                                    cityFilter.contains(" "+((RankedDoc) entry.getValue()).getCity())||
                                    cityFilter.contains(((RankedDoc) entry.getValue()).getCity()))
                                temp.put((String) entry.getKey(), (RankedDoc) entry.getValue());
                        }
                        ranks = temp;
                    }
                    temp.clear();
                    termQueries.clear();
                    parse.newParser(queryNarrativeToParse,queryNumber,"","","","");
                    addDictionary();
                    ranker.setQuery(termQueries);
                    ranker.setTitle(false);
                    ranker.searchDocs();
                    HashMap<String,RankedDoc> ranksNarrative = ranker.rankingDocs(queryNumber);
                    if(cityFilter.size()>0) {
                        for (Map.Entry entry : ranksNarrative.entrySet()) {
                            if (cityFilter.contains("  "+((RankedDoc) entry.getValue()).getCity()) ||
                                    cityFilter.contains(" "+((RankedDoc) entry.getValue()).getCity())||
                                    cityFilter.contains(((RankedDoc) entry.getValue()).getCity()))
                                temp.put((String) entry.getKey(), (RankedDoc) entry.getValue());
                        }
                        ranksNarrative = temp;
                    }
                    temp.clear();
                    termQueries.clear();
                    parse.newParser(queryDescriptionToPars,queryNumber,"","","","");
                    addDictionary();
                    ranker.setQuery(termQueries);
                    ranker.searchDocs();
                    HashMap<String,RankedDoc> ranksDocs = ranker.rankingDocs(queryNumber);
                    if(cityFilter.size()>0) {
                        for (Map.Entry entry : ranksDocs.entrySet()) {
                            if (cityFilter.contains(((RankedDoc) entry.getValue()).getCity()))
                                temp.put((String) entry.getKey(), (RankedDoc) entry.getValue());
                        }

                        ranksDocs = temp;
                    }
                    termQueries.clear();
                    ranksDocs=mergeDescriptionAndNarrative(ranksDocs,ranksNarrative);
                    ranks=joinRanking(ranks,ranksDocs);
                    getBestDocs(ranks);
                }


            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        writeToDisk();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Your docs are in the chosen location");
        alert.showAndWait();
    }

    private HashMap<String, RankedDoc> mergeDescriptionAndNarrative(HashMap<String, RankedDoc> ranksDocs, HashMap<String, RankedDoc> ranksNarrative) {
        HashMap<String, RankedDoc> merge=new HashMap<>();
        for (Map.Entry entry:ranksDocs.entrySet()) {
            if(ranksNarrative.containsKey(entry.getKey())) {
                RankedDoc ranksDoc=(RankedDoc)entry.getValue();
                RankedDoc rankNarrative=ranksNarrative.get(entry.getKey());
                if(ranksDoc.getRank()>rankNarrative.getRank())
                    merge.put((String)entry.getKey(),ranksDoc);
                else
                    merge.put((String)entry.getKey(),rankNarrative);
            }
        }
        return merge;
    }

    //merge the doc return from the title of the query and from the description
    private HashMap<String, RankedDoc> joinRanking(HashMap<String, RankedDoc> ranks, HashMap<String, RankedDoc> ranksDocs) {
        for (Map.Entry entry: ranks.entrySet()) {
            if(ranksDocs.containsKey(entry.getKey())){
                double rank = ranksDocs.get(entry.getKey()).getRank();
                rank+=((RankedDoc)entry.getValue()).getRank();
                ranksDocs.get(entry.getKey()).setRank(rank);
            }
            else
                ranksDocs.put((String)entry.getKey(),(RankedDoc)entry.getValue());
        }
        return ranksDocs;
    }

    //Limit to the top 50 documents
    private void getBestDocs(HashMap<String, RankedDoc> ranksDocs) {
        RankedDoc[] bestDocs = new RankedDoc[50];
        for(int i=0;i<bestDocs.length;i++)
            bestDocs[i] = new RankedDoc(" "," "," ");
        double minRanked=0;
        int minIndex=0;
        int firstCycle=0;
        for (Map.Entry rankEntry: ranksDocs.entrySet()) {
            if(((RankedDoc)rankEntry.getValue()).getRank()>minRanked){
                bestDocs[minIndex] = (RankedDoc)rankEntry.getValue();
                minRanked=bestDocs[minIndex].getRank();
                for(int j=0;j<bestDocs.length;j++){
                    if(bestDocs[j].getRank()<minRanked){
                        minRanked=bestDocs[j].getRank();
                        minIndex=j;
                        firstCycle++;
                        if(firstCycle<50)
                            break;
                    }
                }
            }
        }
        fiftyBestDocs.add(bestDocs);
    }

    //write the query results to the disk to the choosen folder
    private void writeToDisk() {
        File file = new File(pathTo+"\\queriesResults.txt");
        StringBuilder data = new StringBuilder();
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8"));
            PrintWriter pw = new PrintWriter(bw);
            if(fiftyBestDocs!= null){
                for(int j=0;j<fiftyBestDocs.size();j++){
                    RankedDoc[] query = fiftyBestDocs.get(j);
                    for(int k=0; k<query.length;k++){
                        if(!query[k].getDocID().equals(" "))
                            data.append(query[k].getQueryID() + " " + String.valueOf(0) + " " + query[k].getDocID() + " " + query[k].getRank() + " " + String.valueOf(42.38) + " " + "mt\n");
                    }
                }
            }
            pw.print(data);
            pw.close();
            bw.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //the connection to the api for bring semantic words
    private HashMap<String, Integer> chooseSemantic(String [] queryToParse){
        LinkedList<String>wordsToSemantic=new LinkedList<>();
        HashMap<String,Integer> semanticTerm=new HashMap<>();
        for (int i = 0; i < queryToParse.length; i++)
            wordsToSemantic.add(queryToParse[i]);
        Semantic.getSemanticWordsApi(wordsToSemantic);
        semanticTerm = Semantic.getSemanticWords();
        return semanticTerm;
    }

    //function add the dictionaries create after parsing and merge to one dictionary
    private void addDictionary(){
            for (Map.Entry entry : parse.indexer.getDateDictionary().entrySet())
                termQueries.put((String) entry.getKey(), "date");
            for (Map.Entry entry : parse.indexer.getLowerTermDictionary().entrySet())
                termQueries.put((String) entry.getKey(), "lower");
            for (Map.Entry entry : parse.indexer.getNumberDictionary().entrySet())
                termQueries.put((String) entry.getKey(), "number");
            for (Map.Entry entry : parse.indexer.getPraseDictionary().entrySet())
                termQueries.put((String) entry.getKey(), "parse");
            for (Map.Entry entry : parse.indexer.getPrecentDictionary().entrySet())
                termQueries.put((String) entry.getKey(), "percent");
            for (Map.Entry entry : parse.indexer.getPricesDictionary().entrySet())
                termQueries.put((String) entry.getKey(), "price");
            for (Map.Entry entry : parse.indexer.getUpperTermDictionary().entrySet())
                termQueries.put((String) entry.getKey(), "upper");
    }

    //connection function from the controller to the ranker to bring the strong entity
    public HashMap<String,String>bringStrongEntity(String to, boolean stem) {
        return ranker.bringStrongEntity(to,stem);
    }
}
