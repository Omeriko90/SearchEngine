package Index;

import Objects.Doc;
import Objects.Term;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import Objects.City;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class Indexer{
    public HashMap<String,City> cityPosting;
    public HashMap<String, Doc> docPosting;
    public HashMap<String, Term> dateDictionary;
    public HashMap<String, Term> praseDictionary;
    public HashMap<String, Term> numberDictionary;
    public HashMap<String, Term> precentDictionary;
    public HashMap<String, Term> pricesDictionary;
    public HashMap<String, Term> lowerTermDictionary;
    public HashMap<String, Term> upperTermDictionary;
    public TreeMap<String,String> dicToPrint;
    public HashMap<String,String>strongEntity;
    public HashMap <String,HashMap<String,Integer>> DocEntity;
    public File chosenFolder;

    public Indexer(String path){
        DocEntity=new HashMap<>();
        cityPosting = new HashMap<>();
        docPosting = new HashMap<>();
        chosenFolder = new File(path);
        dateDictionary = new HashMap<>();
        praseDictionary = new HashMap<>();
        precentDictionary = new HashMap<>();
        pricesDictionary = new HashMap<>();
        numberDictionary = new HashMap<>();
        lowerTermDictionary=new HashMap<>();
        upperTermDictionary=new HashMap<>();
        dicToPrint=new TreeMap<>();
        strongEntity=new HashMap<>();
    }

    //update the HashMaps every doc
    public void writeTermToMap(String docID, String representativeCity, String country, String population, String currency, int[] DocCount, HashMap<String, Term> lower, HashMap<String, Term> upper, HashMap<String, Term> date, HashMap<String, Term> price, HashMap<String, Term> parse, HashMap<String, Term> precent, HashMap<String, Term> number, int docLength){
        docPosting.put(docID,new Doc(representativeCity,DocCount[0],DocCount[1],docLength));
        if(!representativeCity.equals("")) {
            if(!cityPosting.containsKey(representativeCity))
                cityPosting.put(representativeCity,new City(country.toUpperCase(),population,currency,docID) );
        }
        for (Map.Entry entry:  lower.entrySet()) {
            if(!lowerTermDictionary.containsKey(entry.getKey()))
                lowerTermDictionary.put((String)entry.getKey(),(Term)entry.getValue());
            else
                joinTerms("lower",entry);
        }
        for (Map.Entry entry: upper.entrySet()) {
            if(!upperTermDictionary.containsKey(entry.getKey()))
                upperTermDictionary.put((String)entry.getKey(),(Term)entry.getValue());
            else {
                joinTerms("upper",entry);
            }
        }
        for (Map.Entry entry: date.entrySet()) {
            if(!dateDictionary.containsKey(entry.getKey()))
                dateDictionary.put((String)entry.getKey(),(Term)entry.getValue());
            else
                joinTerms("date",entry);

        }
        for (Map.Entry entry: price.entrySet()) {
            if(!pricesDictionary.containsKey(entry.getKey()))
                pricesDictionary.put((String)entry.getKey(),(Term)entry.getValue());
            else
                joinTerms("price",entry);
        }
        for (Map.Entry entry: parse.entrySet()) {
            if(!praseDictionary.containsKey(entry.getKey()))
                praseDictionary.put((String)entry.getKey(),(Term)entry.getValue());
            else
                joinTerms("prase",entry);
        }
        for (Map.Entry entry: precent.entrySet()) {
            if(!precentDictionary.containsKey(entry.getKey()))
                precentDictionary.put((String)entry.getKey(),(Term)entry.getValue());
            else
                joinTerms("percent",entry);
        }
        for (Map.Entry entry: number.entrySet()) {
            if(!numberDictionary.containsKey(entry.getKey()))
                numberDictionary.put((String)entry.getKey(),(Term)entry.getValue());
            else
                joinTerms("number",entry);
        }
    }

    //check if the terms exits in the temporary dictionaries.
    private void joinTerms(String dictionary, Map.Entry entry) {
        switch (dictionary){
            case "lower":
                lowerTermDictionary.get(entry.getKey()).addToDocAppearacnces(((Term) entry.getValue()).getDocAppearances());
                lowerTermDictionary.get(entry.getKey()).addToDocLocations(((Term)entry.getValue()).getDocLocation());
                break;
            case "upper":
                upperTermDictionary.get(entry.getKey()).addToDocAppearacnces(((Term) entry.getValue()).getDocAppearances());
                upperTermDictionary.get(entry.getKey()).addToDocLocations(((Term)entry.getValue()).getDocLocation());
                break;
            case "date":
                dateDictionary.get(entry.getKey()).addToDocAppearacnces(((Term) entry.getValue()).getDocAppearances());
                dateDictionary.get(entry.getKey()).addToDocLocations(((Term)entry.getValue()).getDocLocation());
                break;
            case "price":
                pricesDictionary.get(entry.getKey()).addToDocAppearacnces(((Term) entry.getValue()).getDocAppearances());
                pricesDictionary.get(entry.getKey()).addToDocLocations(((Term)entry.getValue()).getDocLocation());
                break;
            case "prase":
                praseDictionary.get(entry.getKey()).addToDocAppearacnces(((Term) entry.getValue()).getDocAppearances());
                praseDictionary.get(entry.getKey()).addToDocLocations(((Term)entry.getValue()).getDocLocation());
                break;
            case "percent":
                precentDictionary.get(entry.getKey()).addToDocAppearacnces(((Term) entry.getValue()).getDocAppearances());
                precentDictionary.get(entry.getKey()).addToDocLocations(((Term)entry.getValue()).getDocLocation());
                break;
            case "number":
                numberDictionary.get(entry.getKey()).addToDocAppearacnces(((Term) entry.getValue()).getDocAppearances());
                numberDictionary.get(entry.getKey()).addToDocLocations(((Term)entry.getValue()).getDocLocation());
        }
    }

    //write to the disc the dictionaries every time we choose
    public void endOfDirWrite(String dirName) {
        HashMap<String, Term>[] TreeMap = insertToTreeMap();
        String termFolder = chosenFolder.getAbsolutePath();
        File fileWriter = new File(termFolder);
        char currentCharacter;
        if (!fileWriter.exists())
            fileWriter.mkdir();
        BufferedWriter bw = null;
        PrintWriter pw = null;
        StringBuilder data = new StringBuilder();
        for (int i = 0; i < TreeMap.length; i++) {
            if (i < 26) {
                currentCharacter = (char) (i + 97);
                try {
                    fileWriter = new File(termFolder + "\\" + currentCharacter);
                    if (!fileWriter.exists())
                        fileWriter.mkdir();
                    fileWriter = new File(termFolder + "\\" + currentCharacter + "\\" + currentCharacter + dirName + ".txt");
                    bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileWriter),"UTF-8"));
                    pw = new PrintWriter(bw);
                    for (Map.Entry entry : TreeMap[i].entrySet())
                        data.append(entry.getKey() + ": " + entry.getValue().toString() + "\n" + "----------------------------------------------------------------------------------------\n");
                    pw.print(data);
                    pw.close();
                    bw.close();
                    data.delete(0, data.length());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if(i>=26 && i<52) {
                currentCharacter = (char) (i + 39);
                try {
                    fileWriter = new File(termFolder + "\\" + currentCharacter + "\\" + currentCharacter + dirName + "Upper.txt");
                    bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileWriter),"UTF-8"));
                    pw = new PrintWriter(bw);
                    for (Map.Entry entry : TreeMap[i].entrySet())
                        data.append(entry.getKey() + ": " + entry.getValue().toString() + "\n" + "----------------------------------------------------------------------------------------\n");
                    pw.print(data);
                    pw.close();
                    bw.close();
                    data.delete(0, data.length());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                try {
                    fileWriter = new File(termFolder + "\\" + "anotherSignal");
                    if (!fileWriter.exists())
                        fileWriter.mkdir();
                    fileWriter = new File(termFolder + "\\" + "anotherSignal" + "\\" + "anotherSignal" + dirName + ".txt");
                    bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileWriter),"UTF-8"));
                    pw = new PrintWriter(bw);
                    for (Map.Entry entry : TreeMap[i].entrySet())
                        data.append(entry.getKey() + ": " + entry.getValue().toString() + "\n" + "----------------------------------------------------------------------------------------\n");
                    pw.print(data);
                    pw.close();
                    bw.close();
                    data.delete(0, data.length());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        lowerTermDictionary.clear();
        upperTermDictionary.clear();
    }

    //when we end the parse to the all corpus we do merging terms
    public void mergeTermPosting() {
        File[] folder = new File(chosenFolder.getAbsolutePath()).listFiles();
        String []cityApp=new String[3];
        cityApp[0]="";
        cityApp[1]="";
        cityApp[2]="0";
        for(int k=0;k<folder.length;k++) {
            if((folder[k].getName().length()==1 || folder[k].getName().equals("anotherSignal")))
                merging(folder[k]);
        }
        File fileWriter = new File(chosenFolder.getAbsolutePath()+"\\docPosting.txt");
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileWriter),"UTF-8"));
            PrintWriter pw = new PrintWriter(bw);
            StringBuilder data = new StringBuilder();
            for (Map.Entry entry: docPosting.entrySet()) {
                data.append(entry.getKey()+" "+entry.getValue().toString()+"\n"+"----------------------------------------------------------------------------------------\n");
            }
            pw.print(data);
            pw.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileWriter = new File(chosenFolder.getAbsolutePath()+"\\cityPosting.txt");
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileWriter),"UTF-8"));
            PrintWriter pw = new PrintWriter(bw);
            StringBuilder data = new StringBuilder();
            for (Map.Entry entry: cityPosting.entrySet()) {
                String [] tmp=((City)entry.getValue()).countOfLocation();
                if(tmp[1]!=null) {
                    if (Integer.valueOf(cityApp[2]) < Integer.valueOf(tmp[1])) {
                        cityApp[2] = tmp[1];
                        cityApp[1] = tmp[0];
                        cityApp[0] = (String) entry.getKey();
                    }
                }
                data.append(entry.getKey()+" "+entry.getValue().toString()+"\n"+"----------------------------------------------------------------------------------------\n");
            }
            pw.print(data);
            pw.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cityPosting.clear();
        docPosting.clear();
        WriteDocEntity();
        creatDictionary();
        DocEntity.clear();
    }

    //in the end we write all thw others dictionaries, because they smaller then the term we write them only in the end
    public void writeAll(String dirName) {
        String dateFile = chosenFolder.getAbsolutePath()+"\\date";
        String praseFile = chosenFolder.getAbsolutePath()+"\\parse";
        String precentFile = chosenFolder.getAbsolutePath()+"\\percent";
        String pricesFile = chosenFolder.getAbsolutePath()+"\\prices";
        String numberFile = chosenFolder.getAbsolutePath()+"\\number";
        String[] dirs = {dateFile,praseFile,precentFile,pricesFile,numberFile};
        File fileWriter;
        StringBuilder data = new StringBuilder();
        for(int i=0;i<dirs.length;i++){
            fileWriter=new File(dirs[i]);
            if(!fileWriter.exists())
                fileWriter.mkdir();
        }
        fileWriter = new File(numberFile+"\\"+"number.txt");
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileWriter),"UTF-8"));
            PrintWriter pw = new PrintWriter(bw);
            if(numberDictionary.size()>0) {
                for (Map.Entry entry : numberDictionary.entrySet())
                    data.append(entry.getKey() + ": " + entry.getValue().toString() + "\n" + "----------------------------------------------------------------------------------------\n");
                pw.print(data);
                pw.close();
                bw.close();
            }
            data = new StringBuilder();
            fileWriter = new File(dateFile + "\\" + "date.txt");
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileWriter),"UTF-8"));
            pw = new PrintWriter(bw);
            if(dateDictionary.size()>0) {
                for (Map.Entry entry : dateDictionary.entrySet())
                    data.append(entry.getKey() + ": " + entry.getValue().toString() + "\n" + "----------------------------------------------------------------------------------------\n");
                pw.print(data);
                pw.close();
                bw.close();
            }
            data = new StringBuilder();
            fileWriter=new File(praseFile+"\\"+"prase.txt");
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileWriter),"UTF-8"));
            pw = new PrintWriter(bw);
            if(praseDictionary.size()>0) {
                for (Map.Entry entry : praseDictionary.entrySet())
                    data.append(entry.getKey() + ": " + entry.getValue().toString() + "\n" + "----------------------------------------------------------------------------------------\n");
                pw.print(data);
                pw.close();
                bw.close();
            }
            data = new StringBuilder();
            fileWriter = new File(precentFile + "\\" + "percent.txt");
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileWriter),"UTF-8"));
            pw = new PrintWriter(bw);
            if(precentDictionary.size()>0) {
                for (Map.Entry entry : precentDictionary.entrySet())
                    data.append(entry.getKey() + ": " + entry.getValue().toString() + "\n" + "----------------------------------------------------------------------------------------\n");
                pw.print(data);
                pw.close();
                bw.close();
            }
            data = new StringBuilder();
            fileWriter=new File(pricesFile+"\\"+"price.txt");
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileWriter),"UTF-8"));
            pw = new PrintWriter(bw);
            if(pricesDictionary.size()>0) {
                for (Map.Entry entry : pricesDictionary.entrySet())
                    data.append(entry.getKey() + ": " + entry.getValue().toString() + "\n" + "----------------------------------------------------------------------------------------\n");
                pw.print(data);
                pw.close();
                bw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        dateDictionary.clear();
        pricesDictionary.clear();
        precentDictionary.clear();
        praseDictionary.clear();
        numberDictionary.clear();
    }

    //the reset button
    public void resetDic(String destination) {
        docPosting.clear();
        cityPosting.clear();
        dateDictionary.clear();
        pricesDictionary.clear();
        precentDictionary.clear();
        praseDictionary.clear();
        numberDictionary.clear();
        dicToPrint.clear();
        File[] filesTodelete = new File(destination).listFiles();
        for(int i=0;i<filesTodelete.length;i++) {
            if(filesTodelete[i].isDirectory()){
                File[] tmp = filesTodelete[i].listFiles();
                for(int j=tmp.length-1;j>=0;j--)
                    tmp[j].delete();
                Path p = filesTodelete[i].toPath();
                try {
                    Files.delete(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            filesTodelete[i].delete();
        }
        chosenFolder=new File(destination);
        chosenFolder.delete();
    }

    //the load dic button
    public void loadDictionary(String stem,String destination) {
        chosenFolder=new File(destination+stem);
        File[] folders = new File(chosenFolder.getAbsolutePath()).listFiles();
        for (int j = 0; j < folders.length; j++) {
            if (folders[j].isFile() && (folders[j].getName()).equals("FinalDictionary.txt")) {
                Document doc = null;
                try {
                    doc = Jsoup.parse(new String(Files.readAllBytes(folders[j].toPath())));
                    Elements elements = doc.getElementsByTag("body");
                    for (Element elem : elements) {
                        String[] lines = elem.text().split("----------------------------------------------------------------------------------------");
                        for (int k = 0; k < lines.length; k++) {
                            String[] term = lines[k].split("Number of appearances:");
                            int index1 = term[0].indexOf(':');
                            dicToPrint.put(term[0].substring(index1+1),term[1]);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //after writing the posting files we merge all to a dictionary
    public void creatDictionary(){
        File[] folders = new File(chosenFolder.getAbsolutePath()).listFiles();
        for(int j=0;j<folders.length;j++) {
            if (folders[j].isDirectory() && !folders[j].getName().equals("docsEntity")) {
                for (File filos : folders[j].listFiles()) {
                    if(filos.length()>0) {
                        Document doc = null;
                        try {
                            doc = Jsoup.parse(new String(Files.readAllBytes(filos.toPath())),"UTF-8");
                            Elements elements = doc.getElementsByTag("body");
                            for (Element elem : elements) {
                                String[] lines = elem.text().split("----------------------------------------------------------------------------------------");
                                for (int k = 0; k < lines.length; k++) {
                                    String[] term = lines[k].split("#");
                                    if(term[0].length()==0 || term[1].length()==0)
                                        continue;
                                    else {
                                        int index1 = term[0].indexOf(':'), index2 = term[0].indexOf(',');
                                        if (term[0].charAt(0) == ' ')
                                            dicToPrint.put(term[0].substring(1, index1), term[0].substring(index2+1));
                                        else
                                            dicToPrint.put(term[0].substring(0, index1), term[0].substring(index2+1));
                                    }

                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        File fileWriter=null;
        BufferedWriter bw=null;
        PrintWriter pw=null;

        try {
            fileWriter = new File(chosenFolder.getAbsolutePath()+ "\\FinalDictionary.txt");
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileWriter),"UTF-8"));
            pw = new PrintWriter(bw);
            StringBuilder data = new StringBuilder();
            for (Map.Entry entry: dicToPrint.entrySet()) {
                data.append("Term: " + (String)entry.getKey()  + "   Number of appearances: " +(String)entry.getValue() +"\n"+ "----------------------------------------------------------------------------------------\n");
            }
            pw.print(data);
            pw.close();
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int dicSize(){return dicToPrint.size();}

    //the print dic button
    public TreeMap<String,String> printDic() {
        return dicToPrint;
    }

    //implementation of treeMap with HashMap
    public HashMap<String,Term>[]insertToTreeMap(){
        HashMap<String,Term> [] tmp=new HashMap[53];
        String currentTerm="";
        char c;
        int luckyNumber=0;
        for(int i=0;i<tmp.length;i++)
            tmp[i]=new HashMap<>();
        for (Map.Entry entry:lowerTermDictionary.entrySet()) {
            currentTerm=(String)entry.getKey();
            c=currentTerm.charAt(0);
            luckyNumber =chooseTreeMap(c);
            tmp[luckyNumber].put((String)entry.getKey(),(Term)entry.getValue());
        }
        for (Map.Entry entry:upperTermDictionary.entrySet()) {
            currentTerm=(String)entry.getKey();
            c=currentTerm.charAt(0);
            luckyNumber =chooseTreeMap(c);
            tmp[luckyNumber].put((String)entry.getKey(),(Term)entry.getValue());
        }
        return tmp;
    }

    //merge the all files in the chosen folder and create the strong entity file
    public void merging(File folderPath) {
        HashMap<String,String>mergedTerms=new HashMap<>();
        File[] files = new File(folderPath.getAbsolutePath()).listFiles();
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".txt") && file.length()>0) {
                Document doc = null;
                try {
                    doc = Jsoup.parse(new String(Files.readAllBytes(file.toPath())),"UTF-8");
                    Elements elements = doc.getElementsByTag("body");
                    for (Element elem : elements) {
                        //array of line- lines in this specific doc
                        String[] lines = elem.text().split("----------------------------------------------------------------------------------------");
                        for (int i = 0; i < lines.length; i++) {
                            System.out.println("File for loop");
                            String[] term = lines[i].split("#");
                            String currentTerm,details,updateNumbers;
                            int colonIndex = term[0].indexOf(':');
                            currentTerm = term[0].substring(0, colonIndex);
                            if(currentTerm.charAt(0)==' ')
                                currentTerm=currentTerm.substring(1);
                            if(mergedTerms.containsKey(currentTerm.toLowerCase()) || mergedTerms.containsKey(" "+currentTerm.toLowerCase())){
                                updateNumbers = mergedTerms.get(currentTerm.toLowerCase());
                                details=detailsUpdate(term,updateNumbers)+term[1];
                                mergedTerms.put(currentTerm.toLowerCase(),details);

                            }
                            else if(mergedTerms.containsKey(currentTerm) || mergedTerms.containsKey(" "+currentTerm)){
                                updateNumbers = mergedTerms.get(currentTerm);
                                details=detailsUpdate(term,updateNumbers)+term[1];
                                mergedTerms.put(currentTerm,details);
                            }
                            else{
                                details = term[0].substring(colonIndex+1)+"#"+term[1];
                                mergedTerms.put(currentTerm,details);
                            }
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            file.delete();
        }
        try {
            File fileWriter,fileWriter2,fileWriterFolder;
            BufferedWriter bw,bw2;
            PrintWriter pw,pw2;
            StringBuilder data,data2;
            fileWriter = new File(folderPath + "\\posting.txt");
            fileWriterFolder=new File(folderPath+"\\Entity");
            if (!fileWriterFolder.exists())
                fileWriterFolder.mkdir();
            fileWriter2=new File(folderPath+"\\Entity\\UpperTerms.txt");
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileWriter),"UTF-8"));
            bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileWriter2),"UTF-8"));
            pw = new PrintWriter(bw);
            pw2 = new PrintWriter(bw2);
            data = new StringBuilder();
            data2 = new StringBuilder();
            String updateNumbers="";
            String details="";
            String term[];
            int numOfcollections=0;
            for (Map.Entry entry: mergedTerms.entrySet()) {
                String changeToBigLetter=(String)entry.getKey();
                char c=changeToBigLetter.charAt(0);
                while (c==' ') {
                    changeToBigLetter = changeToBigLetter.substring(1);
                    c=changeToBigLetter.charAt(0);
                }
                if(c>='A' && c<='Z') {
                    changeToBigLetter = changeToBigLetter.toUpperCase();
                    //add for the EntityPart
                    if(!strongEntity.containsKey(changeToBigLetter))
                        strongEntity.put(changeToBigLetter,(String)entry.getValue());
                    else {
                        updateNumbers = strongEntity.get(changeToBigLetter);
                        term=((String)entry.getValue()).split("#");
                        if(term[0].charAt(0)==' ')
                            term[0]=term[0].substring(1);
                        term[0]=changeToBigLetter+": "+term[0];
                        details=detailsUpdate(term,updateNumbers)+term[1];
                        strongEntity.replace(changeToBigLetter,strongEntity.get(changeToBigLetter),details);
                    }
                    AddTermEntityForEveryDoc(changeToBigLetter,strongEntity.get(changeToBigLetter));
                    data2.append(changeToBigLetter + ": " + entry.getValue() + "\n" + "----------------------------------------------------------------------------------------\n");
                    //AddTermEntityForEveryDoc();
                }
                data.append(changeToBigLetter + ": " + entry.getValue() + "\n" + "----------------------------------------------------------------------------------------\n");
                numOfcollections++;
                if(numOfcollections==7200){
                    pw.print(data);
                    pw2.print(data2);
                    numOfcollections=0;
                    data.delete(0,data.length());
                    data2.delete(0,data2.length());
                }
            }
            pw.print(data);
            if(data2.length()>0)
                pw2.print(data2);
            else {
                fileWriterFolder.delete();
                fileWriter2.delete();
            }
            strongEntity.clear();
            pw.close();
            pw2.close();
            bw.close();
            bw2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //AddTermEntityForEveryDoc(folderPath);
    }

    //updating the number of documents and appearances of the term and adding the docID+locations
    private String detailsUpdate(String[] term, String updateNumbers) {
        int colonIndex = term[0].indexOf(':');
        int ampersandIndex = term[0].indexOf(',');
        int numOfDocs = Integer.valueOf(term[0].substring(colonIndex+2,ampersandIndex));
        int numOfApp = Integer.valueOf(term[0].substring(ampersandIndex+1));
        int ampersandIndex2 = updateNumbers.indexOf(',');
        int hashIndex = updateNumbers.indexOf('#');
        String numDocsFromMerged = updateNumbers.substring(0,ampersandIndex2);
        String numAppFromMerged = updateNumbers.substring(ampersandIndex2+1,hashIndex);
        if(numDocsFromMerged.charAt(0)==' ')
            numDocsFromMerged=numDocsFromMerged.substring(1);
        numOfDocs+=Integer.valueOf(numDocsFromMerged);
        numOfApp+=Integer.valueOf(numAppFromMerged);

        return String.valueOf(numOfDocs)+","+String.valueOf(numOfApp)+updateNumbers.substring(hashIndex);
    }

    //pass only one time after all the strong entity and make Inverted file for all doc we write all his strong entities
    private void AddTermEntityForEveryDoc(String term, String doc) {
            String files=doc;
            String[] docs = files.split("#");
            String [] everyFile=docs[1].split(" ");
            for(int i=0;i<everyFile.length;i++){
                int index=everyFile[i].indexOf(':');
                String currentDocId=everyFile[i].substring(0,index);
                String numOfAppreanceInDoc=everyFile[i].substring(index+1);
                while(numOfAppreanceInDoc.charAt(0)==' ')
                    numOfAppreanceInDoc=numOfAppreanceInDoc.substring(1);
                while (numOfAppreanceInDoc.charAt(numOfAppreanceInDoc.length()-1)==' ')
                    numOfAppreanceInDoc=numOfAppreanceInDoc.substring(0,numOfAppreanceInDoc.length()-1);
                if (DocEntity.containsKey(currentDocId)) {
                    if (DocEntity.get(currentDocId).containsKey(term)) {
                        int tempNum = DocEntity.get(currentDocId).get(term);
                        DocEntity.get(currentDocId).replace(term, tempNum, tempNum + Integer.valueOf(numOfAppreanceInDoc));
                    } else
                        DocEntity.get(currentDocId).put(term, Integer.valueOf(numOfAppreanceInDoc));
                } else {
                    HashMap<String, Integer> newHash = new HashMap<>();
                    newHash.put(term, Integer.valueOf(numOfAppreanceInDoc));
                    DocEntity.put(currentDocId, newHash);
                }
            }
    }

    //write to the disk the final file of the strong entities
    private void WriteDocEntity(){
        File docFile=new File(chosenFolder.getAbsolutePath()+"\\docsEntity");
        if(!docFile.exists())
           docFile.mkdir();
        File docFileToWrite=new File(chosenFolder.getAbsolutePath()+"\\docsEntity\\docs.txt");
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(docFileToWrite),"UTF-8"));
            PrintWriter pw = new PrintWriter(bw);
            StringBuilder data = new StringBuilder();
            for (Map.Entry entry: DocEntity.entrySet()) {
                data.append(entry.getKey()+"#"+toStringDocsEntity((HashMap<String,Integer>)entry.getValue())+"\n"+"----------------------------------------------------------------------------------------\n");
            }
            pw.print(data);
            pw.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // bring the most five strong entity for every file and this five only write to the disc
    private String toStringDocsEntity(HashMap<String,Integer> DocsTerms){
        String forPrint="";
        int[]numOfApp=new int[5];
        String[]term=new String[5];
        int min=1000;
        HashMap<String,Integer>temp=new HashMap<>();
        int currentSel=0;
        for (Map.Entry entry:DocsTerms.entrySet()) {
            min=(Integer) entry.getValue();
            break;
        }
        for (Map.Entry entry:DocsTerms.entrySet()) {
            if(currentSel<5) {
                if((Integer) entry.getValue()<min)
                    min=(Integer) entry.getValue();
                numOfApp[currentSel] = (Integer) entry.getValue();
                term[currentSel] = (String) entry.getKey();
                currentSel++;
            }
            else
                temp.put((String) entry.getKey(),(Integer) entry.getValue());
        }
        if(temp.size()>0){
            for (Map.Entry entry:temp.entrySet()) {
                for(int i=0;i<5;i++) {
                    if ((Integer)entry.getValue()>min){
                        while (numOfApp[i]>(Integer)entry.getValue() || numOfApp[i]==(Integer)entry.getValue())
                            i++;
                        numOfApp[i]=(Integer)entry.getValue();
                        term[i]=(String)entry.getKey();
                        min=(Integer)entry.getValue();
                        break;
                    }
                }
                for (int i=0;i<5;i++){
                   if(numOfApp[i]<min)
                       min=numOfApp[i];
                }
            }
        }
        for (int j=0;j<numOfApp.length;j++) {
            if(numOfApp[j]==0)
                break;
            forPrint=forPrint+term[j]+":"+(String.valueOf(numOfApp[j]))+", ";
        }
        return forPrint;
    }

    // sorted function to the tree map
    private int chooseTreeMap(char c) {
        int luckyNumber = 52;
        if(c>='A' && c<='Z')
            luckyNumber = (int)c-39;
        else if(c>='a' && c<='z')
            luckyNumber=(int)c-97;
        return luckyNumber;
    }

    //for the alert in the end of the program
    public int numOfDoc(){
        int tmp=docPosting.size();
        return tmp;
    }

    //A function that allows to fetch data for user filtering by cities
    public HashMap<String, City> getCityPosting(String stem,String destination) {
        if(cityPosting.isEmpty()){
            chosenFolder=new File(destination+stem);
            File[] folders = new File(chosenFolder.getAbsolutePath()).listFiles();
            for (int j = 0; j < folders.length; j++) {
                if (folders[j].isFile() && (folders[j].getName()).equals("cityPosting.txt")) {
                    Document doc = null;
                    try {
                        doc = Jsoup.parse(new String(Files.readAllBytes(folders[j].toPath())));
                        Elements elements = doc.getElementsByTag("body");
                        for (Element elem : elements) {
                            String[] lines = elem.text().split("----------------------------------------------------------------------------------------");
                            for (int k = 0; k < lines.length; k++) {
                                String[] term = lines[k].split("The country:");
                                cityPosting.put(term[0],null);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return cityPosting;
    }

    //getters
    public HashMap<String, Term> getDateDictionary() {
        return dateDictionary;
    }

    public HashMap<String, Term> getLowerTermDictionary() {
        return lowerTermDictionary;
    }

    public HashMap<String, Term> getPrecentDictionary() {
        return precentDictionary;
    }

    public HashMap<String, Term> getNumberDictionary() {
        return numberDictionary;
    }

    public HashMap<String, Term> getPraseDictionary() {
        return praseDictionary;
    }

    public HashMap<String, Term> getPricesDictionary() {
        return pricesDictionary;
    }

    public HashMap<String, Term> getUpperTermDictionary() {
        return upperTermDictionary;
    }
}


