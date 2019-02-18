package Read;
import Index.Indexer;
import com.sun.xml.internal.ws.api.ha.StickyFeature;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import Objects.*;

import Stemmer.Stemmer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Parse {
    HashMap<String, Integer> months;
    HashMap<String, String> count;
    HashMap<String, String> countNumber;
    HashMap<String, String> countingUnits;
    HashMap<Character, String> punctuationMarks;
    HashMap<String, String> stopWords;
    HashMap<String, Term> termLowerCasDictionary;
    HashMap<String, Term> termUpperCaseDictionary;
    HashMap<String, Term> dateDictionary;
    HashMap<String, Term> praseDictionary;
    HashMap<String, Term> numberDictionary;
    HashMap<String, Term> precentDictionary;
    HashMap<String, Term> pricesDictionary;
    private boolean isStemming;
    private int indexInSentenceToParse;
    private Stemmer stemmer;
    String docID, _destination, _from;
    Indexer indexer;

    /**
     * constructor
     */
    public Parse(boolean stem, String destination,String from) {
        _destination=destination;
        _from=from;
        months = new HashMap<>();
        punctuationMarks = new HashMap<>();
        termLowerCasDictionary = new HashMap<>();
        termUpperCaseDictionary = new HashMap<>();
        dateDictionary = new HashMap<>();
        praseDictionary = new HashMap<>();
        numberDictionary = new HashMap<>();
        precentDictionary = new HashMap<>();
        pricesDictionary = new HashMap<>();
        countingUnits = new HashMap<>();
        stopWords = new HashMap<>();
        count = initCount("prices");
        countNumber = initCount(" ");
        isStemming = stem;
        stemmer = new Stemmer();
        docID = "";
        indexer = new Indexer(destination);
        stopWord();
        initcountUnits();
        initMonth();
        initPunctuation();
    }

    private void initcountUnits() {
        countingUnits.put("km", "km");
        countingUnits.put("kg", "kg");
        countingUnits.put("m", "m");
        countingUnits.put("cm", "cm");
        countingUnits.put("km/h", "kmh");
        countingUnits.put("m/s", "m/s");
        countingUnits.put("g", "g");
        countingUnits.put("kw", "kw");
        countingUnits.put("mh", "mh");
        countingUnits.put("ton", "t");
        countingUnits.put("mg", "mg");
        countingUnits.put("lb", "lb");
        countingUnits.put("kilometers", "km");
        countingUnits.put("libras", "lb");
        countingUnits.put("kilograms", "km");
        countingUnits.put("grams", "g");
        countingUnits.put("kilowatts", "kw");
        countingUnits.put("megahertzs", "mh");
        countingUnits.put("centimeter", "cm");
        countingUnits.put("meters", "m");
        countingUnits.put("tons", "t");
        countingUnits.put("seconds", "s");
        countingUnits.put("hours", "h");
        countingUnits.put("minutes", "min");

    }

    private void initPunctuation() {
        punctuationMarks.put('!', "");
        punctuationMarks.put('?', "");
        punctuationMarks.put('*', "");
        punctuationMarks.put(';', "");
        punctuationMarks.put('"', "");
        punctuationMarks.put('\'', "");
        punctuationMarks.put('(', "");
        punctuationMarks.put(')', "");
        punctuationMarks.put(':', "");
        punctuationMarks.put('[', "");
        punctuationMarks.put(']', "");
        punctuationMarks.put(',', "");
        punctuationMarks.put('{', "");
        punctuationMarks.put('}', "");
        punctuationMarks.put('+', "");
        punctuationMarks.put('=', "");
        punctuationMarks.put('#', "");
        punctuationMarks.put('@', "");
        punctuationMarks.put('^', "");
        punctuationMarks.put('&', "");
        punctuationMarks.put('|', "");
        punctuationMarks.put('`', "");
        punctuationMarks.put('>', "");
        punctuationMarks.put('<', "");
        punctuationMarks.put('�', "");
        punctuationMarks.put('¥', "");

    }

    private HashMap<String, String> initCount(String type) {
        HashMap<String, String> tmp = new HashMap<>();
        tmp.put("thousand", "");
        tmp.put("million", "");
        tmp.put("billion", "");
        tmp.put("trillion", "");
        if (type.equals("prices")) {
            tmp.put("m", "");
            tmp.put("bn", "");
        }
        return tmp;
    }

    private void initMonth() {
        months.put("january", 1);
        months.put("february", 2);
        months.put("march", 3);
        months.put("april", 4);
        months.put("may", 5);
        months.put("june", 6);
        months.put("july", 7);
        months.put("august", 8);
        months.put("september", 9);
        months.put("october", 10);
        months.put("november", 11);
        months.put("december", 12);
        months.put("jan", 1);
        months.put("feb", 2);
        months.put("mar", 3);
        months.put("apr", 4);
        months.put("jun", 6);
        months.put("jul", 7);
        months.put("aug", 8);
        months.put("sep", 9);
        months.put("oct", 10);
        months.put("nov", 11);
        months.put("dec", 12);
    }

    //the main parser- every doc pass this function- in this function we separate the sections
    public void newParser(String[] word, String docId, String representativeCity, String country,String population, String currency) {

        String pop=intParser(population,"");
        String[] sentenceToParse = word;
        indexInSentenceToParse = 0;
        String wordToParse;
        docID = docId;
        for (int i = 0; i < sentenceToParse.length; i++)
            sentenceToParse[i] = hasMarks(sentenceToParse[i]);
        while (indexInSentenceToParse < sentenceToParse.length) {
            wordToParse = sentenceToParse[indexInSentenceToParse];

            if(wordToParse.equals("Dr."))
                wordToParse="Doctor";

            if(wordToParse.equals("$") || wordToParse.equals("$$") || wordToParse.equals("$$$") || wordToParse.equals("$$$$")
                    ||wordToParse.equals(" $") || wordToParse.equals("$$$$$")|| wordToParse.equals("$$$$$$$")) {
                indexInSentenceToParse++;
                continue;
            }

            //ignore empty string
            if (wordToParse.equals("")) {
                indexInSentenceToParse++;
                continue;
            }
            //words and with (-)
            if (containsChar(wordToParse, '-') && indexOfChar(wordToParse, '-') == wordToParse.length() - 1 && indexInSentenceToParse < sentenceToParse.length - 1) {
                sentenceToParse[indexInSentenceToParse + 1] = wordToParse + sentenceToParse[indexInSentenceToParse + 1];
                wordToParse = hasMarks(sentenceToParse[indexInSentenceToParse + 1]);
                sentenceToParse[indexInSentenceToParse + 1] = wordToParse;
                indexInSentenceToParse += 1;
            }
            else
                sentenceToParse[indexInSentenceToParse] = wordToParse;
            //check if the word is a stop word
            if ((stopWords.containsKey(wordToParse) && !(wordToParse.toLowerCase().equals("between") && indexInSentenceToParse + 1 < sentenceToParse.length && matchesOnlyNumbers(sentenceToParse[indexInSentenceToParse + 1])))|| (wordToParse.equals("The"))) {
                indexInSentenceToParse++;
            } else if (wordToParse.contains("--") || wordToParse.length() == 0) {
                indexInSentenceToParse++;
                continue;
            }
            //checks for words like wor-| for table
            else if (wordToParse.contains("-|")) {
                int index = indexOfChar(wordToParse, '-');
                String left = wordToParse.substring(0, index);
                String right = wordToParse.substring(index + 2);
                sentenceToParse[indexInSentenceToParse] = hasMarks(left);
                parserCheck(sentenceToParse, indexInSentenceToParse);
                sentenceToParse[indexInSentenceToParse] = hasMarks(right);
                indexInSentenceToParse = parserCheck(sentenceToParse, indexInSentenceToParse);

            }
            //check if the word contain (-) - expression
            else if (containsChar(wordToParse, '-') && indexOfChar(wordToParse, '-') > 0 && indexOfChar(wordToParse, '-') != wordToParse.length() - 1) {
                indexInSentenceToParse = expressions(indexInSentenceToParse, sentenceToParse);
            }
            //between number and number
            else if (wordToParse.toLowerCase().equals("between")) {
                indexInSentenceToParse = expressionsBet(indexInSentenceToParse, sentenceToParse);
            }
            //all the other options
            else {
                if (containsChar(wordToParse, '-') && indexOfChar(wordToParse, '-') == wordToParse.length() - 1)
                    sentenceToParse[indexInSentenceToParse] = wordToParse.substring(0, wordToParse.length() - 1);
                indexInSentenceToParse = parserCheck(sentenceToParse, indexInSentenceToParse);
            }

        }
        letterMerge();
        int docLength = termLowerCasDictionary.size()+termUpperCaseDictionary.size()+dateDictionary.size()+precentDictionary.size()+praseDictionary.size()+pricesDictionary.size()+numberDictionary.size();
        indexer.writeTermToMap(docID,representativeCity,country,pop,currency,countDocFreq(),termLowerCasDictionary,termUpperCaseDictionary,dateDictionary,pricesDictionary,
                praseDictionary,precentDictionary,numberDictionary,docLength);
        termUpperCaseDictionary.clear();
        termLowerCasDictionary.clear();
        dateDictionary.clear();
        precentDictionary.clear();
        praseDictionary.clear();
        pricesDictionary.clear();
        numberDictionary.clear();
    }

    //every doc we merge between word that show some time in large letter and some time with little letter
    //we save the big letter in the termUpperCaseDictionary
    private void letterMerge() {
        LinkedList<String>listToRemove=new LinkedList<>();
        for (Map.Entry entry: termUpperCaseDictionary.entrySet()) {
            if(termLowerCasDictionary.containsKey(((String)entry.getKey()).toLowerCase())){
                termLowerCasDictionary.get(((String)entry.getKey()).toLowerCase()).addToDocAppearacnces(((Term)entry.getValue()).getDocAppearances());
                termLowerCasDictionary.get(((String)entry.getKey()).toLowerCase()).addToDocLocations(((Term)entry.getValue()).getDocLocation());
                listToRemove.add((String)entry.getKey());
            }
        }
        for(int i=0;i<listToRemove.size();i++)
            termUpperCaseDictionary.remove(listToRemove.get(i));
    }

    //before add to the DocPosting we check doc details
    private int[] countDocFreq() {
        int max_tf = 0;
        int uniqueTerms = 0;
        int[]ans=new int[2];
        for (Map.Entry entry : numberDictionary.entrySet()) {
            if (max_tf < ((Term) entry.getValue()).getNum(docID))
                max_tf = ((Term) entry.getValue()).getNum(docID);
            if (((Term) entry.getValue()).getNum(docID) == 1)
                uniqueTerms++;
        }
        for (Map.Entry entry : dateDictionary.entrySet()) {
            if (max_tf < ((Term) entry.getValue()).getNum(docID))
                max_tf = ((Term) entry.getValue()).getNum(docID);
            if (((Term) entry.getValue()).getNum(docID) == 1)
                uniqueTerms++;
        }
        for (Map.Entry entry : precentDictionary.entrySet()) {
            if (max_tf < ((Term) entry.getValue()).getNum(docID))
                max_tf = ((Term) entry.getValue()).getNum(docID);
            if (((Term) entry.getValue()).getNum(docID) == 1)
                uniqueTerms++;
        }
        for (Map.Entry entry : praseDictionary.entrySet()) {
            if (max_tf < ((Term) entry.getValue()).getNum(docID))
                max_tf = ((Term) entry.getValue()).getNum(docID);
            if (((Term) entry.getValue()).getNum(docID) == 1)
                uniqueTerms++;
        }
        for (Map.Entry entry : pricesDictionary.entrySet()) {
            if (max_tf < ((Term) entry.getValue()).getNum(docID))
                max_tf = ((Term) entry.getValue()).getNum(docID);
            if (((Term) entry.getValue()).getNum(docID) == 1)
                uniqueTerms++;
        }
        for (Map.Entry entry: termLowerCasDictionary.entrySet()) {
            if(max_tf < ((Term)entry.getValue()).getNum(docID))
                max_tf=((Term)entry.getValue()).getNum(docID);
            if(((Term)entry.getValue()).getNum(docID)==1)
                uniqueTerms++;
        }
        for (Map.Entry entry: termUpperCaseDictionary.entrySet()) {
            if(max_tf < ((Term)entry.getValue()).getNum(docID))
                max_tf=((Term)entry.getValue()).getNum(docID);
            if(((Term)entry.getValue()).getNum(docID)==1)
                uniqueTerms++;
        }
        ans[0]=max_tf;
        ans[1]=uniqueTerms;
        return ans;
    }

    //between expression
    private int expressionsBet(int indexInSentenceToParse, String[] sentenceToParse) {
        int indexInLine=indexInSentenceToParse;
        String numLeft="";
        String fullExp=sentenceToParse[indexInSentenceToParse];
        indexInSentenceToParse++;
        int j=0;
        while (indexInLine<sentenceToParse.length){
            if(sentenceToParse[indexInLine].toLowerCase().equals("and")){
                j=indexInLine;
                break;
            }
            indexInLine++;
        }
        indexInLine=indexInSentenceToParse;
        if(j!=0) {
            int difference = j - indexInLine;
            //between number Fraction and number,between number Fraction and number Fraction
            if (difference > 1 && matchesNumber(sentenceToParse[indexInLine])) {
                if (containsChar(sentenceToParse[j - 1],'/')) {
                    numLeft = intParser(sentenceToParse[indexInLine], " ") + " " + sentenceToParse[j - 1];
                } else if (countNumber.containsKey(sentenceToParse[j - 1].toLowerCase())) {
                    numLeft = intParser(sentenceToParse[indexInLine], sentenceToParse[j - 1]);
                }
            }
            //between number and number,between number and number Fraction
            else if (difference == 1 && matchesNumber(sentenceToParse[indexInLine])) {
                numLeft = intParser(sentenceToParse[indexInLine], "");
            }

            if (j + 2 < sentenceToParse.length && (containsChar(sentenceToParse[j + 2],'/') || countNumber.containsKey(sentenceToParse[j + 2].toLowerCase()))) {
                if (matchesOnlyNumbers(sentenceToParse[j + 1])) {
                    if (containsChar(sentenceToParse[j + 2],'/')) {
                        fullExp +=" "+ numLeft + " and " + intParser(sentenceToParse[j + 1], "") + " " + sentenceToParse[j + 2];
                        indexInLine = j + 3;
                    } else if (countNumber.containsKey(sentenceToParse[j + 2].toLowerCase())) {
                        fullExp +=" "+ numLeft + " and " + intParser(sentenceToParse[j + 1], sentenceToParse[j + 2]);
                        indexInLine = j + 3;
                    }
                }
            }
            else if(j+1<sentenceToParse.length){
                if(matchesNumber(sentenceToParse[j + 1])) {
                    fullExp +=" "+ numLeft + " and " + intParser(sentenceToParse[j + 1], "");
                    indexInLine=j+2;
                }
            }
        }

        enterDic(fullExp,"prase");
        return indexInLine;

    }

    public int getDicSize(){return indexer.dicSize();}
    /**
     * A function that handles expressions
     * @param indexInSentenceToParse
     * @param sentenceToParse
     * @return indexInTheLine-the correct index in the sentence
     */
    private int expressions(int indexInSentenceToParse, String[] sentenceToParse) {
        int indexInTheLine=indexInSentenceToParse;
        int indexOfMark=indexOfChar(sentenceToParse[indexInTheLine],'-');
        String left=sentenceToParse[indexInTheLine].substring(0,indexOfMark);
        String right=sentenceToParse[indexInTheLine].substring(indexOfMark+1);
        //number Fraction-number Fraction, number Fraction-number
        if((containsChar(left,'/')|| countNumber.containsKey(left.toLowerCase()))&& matchesOnlyNumbers(right) && !containsChar(right,'%') && !containsChar(right,'$') && matchesNumber(left)){
            if(!countNumber.containsKey(left.toLowerCase()))
                left =intParser(left,"");
            right=intParser(right,"");
            if(indexInTheLine>0){
                if(matchesOnlyNumbers(sentenceToParse[indexInTheLine-1]) && !countNumber.containsKey(left.toLowerCase())) {
                    String numBefore = intParser(sentenceToParse[indexInTheLine - 1], "");
                    left=numBefore+" "+left;
                }
                else if(matchesOnlyNumbers(sentenceToParse[indexInTheLine-1]) && countNumber.containsKey(left.toLowerCase())){
                    left = intParser(sentenceToParse[indexInTheLine - 1], left);
                }
                else
                    indexInTheLine++;

            }
            if(indexInTheLine<sentenceToParse.length-1){
                if(containsChar(sentenceToParse[indexInTheLine+1],'/'))  {
                    right+=" "+ sentenceToParse[indexInTheLine+1];
                    indexInTheLine+=2;
                }
                else if(countNumber.containsKey(sentenceToParse[indexInTheLine+1].toLowerCase())){
                    right=intParser(right,sentenceToParse[indexInTheLine+1]);
                    indexInTheLine+=2;
                }
                else
                    indexInTheLine++;
            }
        }
        //number-number Fraction, number-number
        else if((matchesOnlyNumbers(left) || ((left.charAt(0)=='$' || left.charAt(0)=='%') && matchesOnlyNumbers(left.substring(1)))) && (matchesOnlyNumbers(right) || ((right.charAt(0)=='$' || right.charAt(0)=='%') && matchesOnlyNumbers(right.substring(1))))){
            if(left.charAt(0)=='$' || left.charAt(0)=='%')
                left =left.charAt(0)+intParser(left.substring(1),"");
            else
                left=intParser(left," ");
            if(indexInTheLine<sentenceToParse.length-1){
                if(containsChar(sentenceToParse[indexInTheLine+1],'/'))  {
                    right+=" "+ sentenceToParse[indexInTheLine+1];
                    indexInTheLine+=2;
                }
                else if(countNumber.containsKey(sentenceToParse[indexInTheLine+1].toLowerCase())){
                    if(right.charAt(0)=='$' || right.charAt(0)=='%')
                        right=right.charAt(0)+intParser(right.substring(1),sentenceToParse[indexInTheLine+1]);
                    else
                        right=intParser(right,sentenceToParse[indexInTheLine+1]);
                    indexInTheLine+=2;
                }
                else
                    indexInTheLine++;
            }
            else {
                indexInTheLine++;
                if(right.charAt(0)=='$' || right.charAt(0)=='%')
                    right=right.charAt(0)+intParser(right.substring(1),"");
                else
                    right = intParser(right, "");
            }
        }
        //number Fraction-word ,number-word
        else if((matchesOnlyNumbers(left) || countNumber.containsKey(left.toLowerCase())) && !containsChar(left,'%') && !containsChar(left,'$')){
            if(!countNumber.containsKey(left.toLowerCase()))
                left=intParser(left,"");
            if(indexInTheLine>0){
                if(sentenceToParse[indexInTheLine-1].length()>0 && matchesOnlyNumbers(sentenceToParse[indexInTheLine-1]) && !countNumber.containsKey(left.toLowerCase())) {
                    String numBefore = intParser(sentenceToParse[indexInTheLine - 1], "");
                    left=numBefore+" "+left;
                }
                else if(sentenceToParse[indexInTheLine-1].length()>0 && matchesOnlyNumbers(sentenceToParse[indexInTheLine-1]) && countNumber.containsKey(left.toLowerCase())){
                    left=intParser(sentenceToParse[indexInTheLine-1],left);
                }
            }

            indexInTheLine++;
        }
        else if(containsChar(left,'$') && right.matches(".*[a-z A-Z].*")) {
            left = dollarParser(left.substring(1), "dollar");
            indexInTheLine++;
        }
        //word-number, word-number Fraction
        else if(matchesOnlyNumbers(right) && !containsChar(right,'%') && !containsChar(right,'$')){
            if(indexInTheLine<sentenceToParse.length-1){
                if(containsChar(sentenceToParse[indexInTheLine+1],'/'))  {
                    right=intParser(right,"")+" "+ sentenceToParse[indexInTheLine+1];
                    indexInTheLine+=2;
                }
                else if(countNumber.containsKey(sentenceToParse[indexInTheLine+1].toLowerCase())){
                    right=intParser(right,sentenceToParse[indexInTheLine+1]);
                    indexInTheLine+=2;

                }
                else
                    indexInTheLine++;
            }
            else
                indexInTheLine++;
        }
        else
            indexInTheLine++;
        enterDic(left,"term");
        enterDic(right,"term");
        enterDic(left+"-"+right,"prase");
        return indexInTheLine;
    }

    /**
     * @param s
     * @return the string after clear punctuation Marks
     */
    private String hasMarks(String s) {
        String newWord = s;
        int diff=0;
        if(s.length()==0)
            return s;
        if(s.length()==1 && (punctuationMarks.containsKey(s.charAt(0)) || s.charAt(0)=='-'))
            newWord="";
        else if(s.length()==1 && s.charAt(0)=='/')
            newWord="";
        else if(s.length()>0 && !s.contains("-|")) {
            if (punctuationMarks.containsKey(s.charAt(0)))
                newWord = s.substring(1);
            for (int i = 0; i < s.length(); i++) {
                if (punctuationMarks.containsKey(s.charAt(i))) {
                    newWord = newWord.substring(0,i-diff)+ s.substring(i + 1);
                    diff++;
                }
            }
        }
        if (newWord.length() > 0 && (punctuationMarks.containsKey(newWord.charAt(newWord.length()-1)) || newWord.charAt(newWord.length()-1)=='.'))
            newWord = newWord.substring(0, newWord.length() - 1);
        if(newWord.length() > 0 && newWord.charAt(0)=='/')
            newWord=newWord.substring(1);
        if (newWord.length() > 0 && newWord.charAt(newWord.length()-1)=='/')
            newWord = newWord.substring(0,newWord.length()-1);
        while (newWord.length()>0 && (newWord.charAt(0)=='.' || newWord.charAt(0)=='/')) {
            int indexMarks=indexOfChar(newWord,'.');
            int indexMarks1=indexOfChar(newWord,'/');
            if(indexMarks==0 || indexMarks1==0)
                newWord = newWord.substring(1);
        }
        return newWord;
    }

    //check number,date,prices,percent,words
    private int parserCheck(String [] sentence, int index){
        boolean point=false;
        if(containsChar(sentence[index],'.')){
            int count=0;
            for(int i=0;i<sentence[index].length();i++){
                if(sentence[index].charAt(i)=='.')
                    count++;
                if(count>1){
                    point=true;
                    break;
                }
            }

        }
        //prices
        if((containsChar(sentence[index],'$') && !sentence[index].matches(".*[a-z A-Z].*") && matchesNumber(sentence[index])) ||
                (!sentence[index].matches(".*[a-z A-Z].*") && matchesOnlyNumbers(sentence[index]) && index+1<sentence.length && count.containsKey(sentence[index+1].toLowerCase()) && index+2<sentence.length &&
                        (sentence[index+2].contains("U.S.") || sentence[index+2].toLowerCase().equals("dollars")))) {
            if(containsChar(sentence[index],'/')){
                enterDic(dollarParser(sentence[index].substring(1), " ") + " Dollars","prices");
            }
            //$7500000.ALEENE
            else if(containsChar(sentence[index],'.')){
                int indexOfPoint=indexOfChar(sentence[index],'.');
                //$7500000.ALEENE
                if(!matchesNumber(sentence[index].substring(indexOfPoint+1))) {
                    String right = sentence[index].substring(indexOfPoint + 1);
                    enterDic(dollarParser(sentence[index].substring(1, indexOfPoint), " ") + " Dollars","prices");
                    if (!matchesOnlyNumbers(right))
                        wordParser(right);
                }
                //$.5464
                else {
                    if(indexOfPoint==1){
                        enterDic(dollarParser("0"+sentence[index].substring(1, indexOfPoint), " ") + " Dollars","prices");
                    }
                }
            }
            else if(index+1<sentence.length) {
                if(containsChar(sentence[index],'$')) {
                    int indexDollar = indexOfChar(sentence[index], '$');
                    if (sentence[index].charAt(indexDollar + 1) == '$')
                        indexDollar++;
                    enterDic(dollarParser(sentence[index].substring(indexDollar + 1), sentence[index + 1]) + " Dollars","prices");
                }
                else
                    enterDic(dollarParser(sentence[index],sentence[index+1]) + " Dollars","prices");
                if (containsChar(sentence[index + 1],'/') || count.containsKey(sentence[index + 1].toLowerCase()))
                    index++;
                if (index+1 <sentence.length && sentence[index + 1].equals("U.S."))
                    index++;
                if (index+1<sentence.length && sentence[index + 1].toLowerCase().contains("dollars"))
                    index++;
            }
            else
                enterDic(dollarParser(sentence[index].substring(1), "") + " Dollars","prices");
        }

        //number+percent
        else if(!sentence[index].matches(".*[a-z A-Z].*") && index+1<sentence.length && (containsChar(sentence[index],'%') || sentence[index+1].toLowerCase().equals("percent") || sentence[index+1].toLowerCase().equals("percentage"))){
            enterDic(percentParser(sentence[index],sentence[index+1]),"percent");
            index++;
        }

        //date
        else if (index+1<sentence.length &&((matchesOnlyNumbers(sentence[index]) && months.containsKey(sentence[index + 1].toLowerCase())) || (months.containsKey((sentence[index].toLowerCase())) && matchesOnlyNumbers(sentence[index+1])))) {
            enterDic(dateParser(sentence[index], hasMarks(sentence[index + 1])),"date");
        }

        else if(!sentence[index].matches(".*[a-z A-Z].*") && sentence[index].length()>0 && !point && !containsChar(sentence[index],'%')){

            if(index+1<sentence.length && containsChar(sentence[index+1],'/')) {
                enterDic(intParser(sentence[index], sentence[index + 1]),"number");
                index++;
            }
            //OUR RULE!!!!!!!!!!!!!!!!!!!!!!!
            else if(index+1<sentence.length && countingUnits.containsKey(sentence[index+1])){
                enterDic(sentence[index]+countingUnits.get(sentence[index+1]),"number");
                index+=2;
            }
            else if(sentence[index].length()>0 && matchesOnlyNumbers(sentence[index]) && !containsChar(sentence[index],'%'))
                enterDic(intParser(sentence[index],""),"number");

            else
                enterDic((sentence[index]),"term");
        }

        else
            wordParser(sentence[index]);
        index++;
        return index;
    }

    /**
     * adds items to the dictionary
     * @param s
     */
    private void enterDic(String s, String type) {
        String realType = type;
        //check the char '\'
        if(s.length()>0){
            Character first=s.charAt(0);
            int index=(int)first;
            if(index==92)
                realType=s.substring(1);
        }
        if(containsChar(s,'&')){
            int index=indexOfChar(s,'&');
            s=s.substring(0,index)+s.substring(index+1);
        }
        //
        if(s.equals("Dr."))
            s="doctor";
        if(isStemming){
            if(!matchesNumber(s) && !containsChar(s,'-') && !containsChar(s, ' ')){
                for(int i=0;i<s.length();i++)
                    stemmer.add(s.charAt(i));
                stemmer.stem();
                s=stemmer.toString();
            }
        }
        if(s.length()>0 && !s.equals(".") && !s.equals("..")) {
            if(realType.equals("term") && matchesNumber(s))
                realType = "number";
            if(realType.equals("term")) {
                if (s.charAt(0) >='A' && s.charAt(0)<='Z') {
                    if(!termUpperCaseDictionary.containsKey(s))
                        termUpperCaseDictionary.put(s, new Term(docID, indexInSentenceToParse));
                    else
                        termUpperCaseDictionary.get(s).setNum(docID, indexInSentenceToParse);

                }
                else{
                    if(!termLowerCasDictionary.containsKey(s))
                        termLowerCasDictionary.put(s, new Term(docID, indexInSentenceToParse));
                    else
                        termLowerCasDictionary.get(s).setNum(docID, indexInSentenceToParse);
                }
            }
            else if(realType.equals("date")) {
                if (!dateDictionary.containsKey(s))
                    dateDictionary.put(s, new Term(docID, indexInSentenceToParse));
                else {
                    dateDictionary.get(s).setNum(docID, indexInSentenceToParse);
                }
            }
            else if(realType.equals("prase")) {
                if (!praseDictionary.containsKey(s))
                    praseDictionary.put(s, new Term(docID, indexInSentenceToParse));
                else {
                    praseDictionary.get(s).setNum(docID, indexInSentenceToParse);
                }
            }
            else if(realType.equals("number")) {
                if (!numberDictionary.containsKey(s))
                    numberDictionary.put(s, new Term(docID, indexInSentenceToParse));
                else {
                    numberDictionary.get(s).setNum(docID, indexInSentenceToParse);
                }
            }
            else if(realType.equals("percent")) {
                if (!precentDictionary.containsKey(s))
                    precentDictionary.put(s, new Term(docID, indexInSentenceToParse));
                else {
                    precentDictionary.get(s).setNum(docID, indexInSentenceToParse);
                }
            }
            else if(realType.equals("prices")) {
                if (!pricesDictionary.containsKey(s))
                    pricesDictionary.put(s, new Term(docID, indexInSentenceToParse));
                else {
                    pricesDictionary.get(s).setNum(docID, indexInSentenceToParse);
                }
            }

        }
    }

    public int dicSize(){return indexer.dicSize();}

    /**
     * This function converts percent/percentage to %
     * @param s
     * @param s1
     * @return the string s with the char %
     */
    private String percentParser(String s, String s1) {
        return s+"%";
    }

    //this function translate prices according to the rules
    private String dollarParser(String s, String s1) {
        String ans=s;
        try {
            double num = Double.valueOf(s);
            if(num<1000000){
                ans=leftOverCheck(String.valueOf(num), "dollar");
            }
            else if (num >= 1000000) {
                num = num / 1000000;
                ans = leftOverCheck(String.valueOf(num), "dollar") + " M";
            } else if (containsChar(s1, '/')) {
                ans = s + " " + s1;
            } else if (count.containsKey(s1)) {
                if (s1.contains("billion") || s1.contains("bn")) {
                    ans = leftOverCheck(String.valueOf(num * 1000), "dollar") + " M";
                } else if (s1.equals("trillion")) {
                    ans = leftOverCheck(String.valueOf(num * 1000000), "dollar") + " M";
                } else
                    ans = leftOverCheck(String.valueOf(num), "dollar") + " M";
            }
        }catch (Exception e){

        }
        return ans;
    }

    //word parser doing nothing, only insert to dic
    private void wordParser(String s) {
        enterDic(s,"term");
    }

    /**
     * this function receives numbers (1000,1000000,1000000000) and converts them to the form of K/M/B.
     * @param number
     * @param type
     * @return string of the the number in the form of K/B/M
     */
    private String intParser(String number,String type){
        if(containsChar(number,'/')){
            return number;
        }
        try {
            double num = convertInt(Double.valueOf(number));
            if (Double.valueOf(number) >= 1000 && !countNumber.containsKey(type.toLowerCase()) && !countingUnits.containsKey(type.toLowerCase())) {
                if (Double.valueOf(number) >= 1000 && Double.valueOf(number) < 1000000)
                    number = String.valueOf(num) + "K";
                else if (Double.valueOf(number) > 1000000 && Double.valueOf(number) < 1000000000)
                    number = String.valueOf(num) + "M";
                else
                    number = String.valueOf(num) + "B";

            } else if (containsChar(type, '/')) {
                if (matchesNumber(type))
                    number = number + " " + type;
                else
                    number = number + type;
            } else if (Double.valueOf(number) < 1000) {
                number = number;
            } else {
                if (Double.valueOf(number) >= 1000)
                    number = String.valueOf(num);
                if (!type.toLowerCase().equals("trillion")) {
                    if (type.toLowerCase().equals("thousand"))
                        number += "K";
                    else
                        number += type.toUpperCase().charAt(0);
                } else {
                    number += "B";
                }
            }
            if (!containsChar(number, '/'))
                number = leftOverCheck(number, "int");

        }catch (Exception e){

        }

        return number;
    }

    //cut number according to number rules
    private double convertInt(double num){
        if (Double.valueOf(num) >= 1000 && Double.valueOf(num) < 1000000)
            num = num / 1000;
        else if (Double.valueOf(num) > 1000000 && Double.valueOf(num) < 1000000000)
            num = num / 1000000;
        else
            num = num / 1000000000;
        return num;
    }

    /**
     *This function translates date to his numeric value
     * @param firstDate
     * @param secondDate
     * @return The full date as DD-MM or MM-DD or YYYY-MM depend on how there were in the document
     */
    private String dateParser(String firstDate,String secondDate){
        String ans;
        if(months.containsKey(firstDate.toLowerCase()) && secondDate.length() > 2){
            ans = secondDate+"-"+monthConverter(firstDate);
        }
        else{
            if(months.containsKey(firstDate.toLowerCase())){
                ans = monthConverter(firstDate)+"-";
                if(secondDate.length()>0 && Integer.valueOf(secondDate) < 10)
                    secondDate= "0"+secondDate;
                ans+=secondDate;
            }
            else{
                if(firstDate.length()>0 && Integer.valueOf(firstDate) < 10)
                    firstDate= "0"+firstDate;
                ans = firstDate+"-" + monthConverter(secondDate);

            }
        }
        return ans;
    }

    /**
     * This function translate the month to it's representative number.
     * @param firstDate
     * @return The month number as a string
     */
    private String monthConverter(String firstDate) {
        int monthNumber=months.get(firstDate.toLowerCase());
        String ans;
        if(monthNumber < 10)
            ans = "0"+String.valueOf(monthNumber);
        else
            ans = String.valueOf(monthNumber);

        return ans;
    }

    /**
     * function that return true if the word is a stop word
     *
     * @return true if the word is a stop word, false otherwise
     */
    public void stopWord(){
        try {
            File chosen=new File(_from);
            File stopWordFile=null;
            File[] files=new File(chosen.getAbsolutePath()).listFiles();
            for(int i=0;i<files.length;i++)
                if(files[i].isFile() && files[i].getName().endsWith(".txt"))
                    stopWordFile=new File(files[i].getAbsolutePath());
            Document doc =  Jsoup.parse(new String(Files.readAllBytes(stopWordFile.toPath())));
            Elements elements = ((org.jsoup.nodes.Document) doc).getElementsByTag("body");
            String[] words = elements.text().split(" ");
            for(int i=0;i<words.length;i++)
                stopWords.put(words[i],words[i]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function checks if the reminder is 0. if it does it cuts the reminder from the number.
     * @param number
     * @param type
     * @return the number in it's correct form, meaning with or without reminder
     */
    private String leftOverCheck(String number, String type){
        int startIndex=indexOfChar(number,'.'),endIndex;
        double num;
        if(startIndex >0 && matchesNumber(number.substring(startIndex+1))) {
            if (type.equals("dollar")) {
                endIndex = number.length();
                num = Double.valueOf(number);
                String leftOver = number.substring(startIndex + 1, endIndex);
                if(leftOver.length()>2)
                    leftOver=leftOver.substring(0,2);
                int numberLeft = Integer.valueOf(leftOver);
                if (numberLeft == 0)
                    number = String.valueOf((int) num) + number.substring(endIndex);
            } else {
                if (startIndex != -1) {
                    if(matchesOnlyNumbers(number.substring(startIndex+1)))
                        endIndex=number.length();
                    else
                        endIndex = number.length() - 1;
                    String leftOver = number.substring(startIndex + 1, endIndex);
                    if(leftOver.length()>2)
                        leftOver=leftOver.substring(0,2);
                    int numberLeft = Integer.valueOf(leftOver);
                    if (numberLeft == 0)
                        number = number.substring(0,startIndex) + number.substring(endIndex);
                }
            }
        }
        return number;
    }

    //implementation of string function
    private boolean containsChar(String word,char symbol){

        for(int i=0;i<word.length();i++){
            if(word.charAt(i)==symbol)
                return true;
        }
        return false;
    }
    private boolean matchesNumber(String word){
        for(int i=0;i<word.length();i++){
            if(word.charAt(i)>='0' && word.charAt(i)<='9')
                return true;
        }
        return false;
    }
    private boolean matchesOnlyNumbers(String word){
        for(int i=0;i<word.length();i++){
            if(word.charAt(i)<'0' || word.charAt(i)>'9')
                return false;
        }
        return true;
    }
    private int indexOfChar(String word, char symbol){
        int index=0;
        for(int i=0;i<word.length();i++){
            if(word.charAt(i)==symbol) {
                index = i;
                break;
            }
        }
        return index;
    }

    //load files to the disc every time we choose
    public void writePostingFiles(String dirName) {
        indexer.endOfDirWrite(dirName);
        indexer.writeAll(dirName);
    }

    //the button of print dictionary
    public TreeMap<String,String> printDic() {
        return indexer.printDic();
    }

//    public TreeMap<String,Term> printDic() {
//        return indexer.printDic();
//    }

    //in the end, change from little letter to big letter
    public void toUpperCase() {
        for (Map.Entry entry: termUpperCaseDictionary.entrySet()) {
            termLowerCasDictionary.put(((String)entry.getKey()).toUpperCase(),(Term)entry.getValue());
        }
    }

    //the reset button
    public void resetDic(String destination) {
        termLowerCasDictionary.clear();
        termUpperCaseDictionary.clear();
        dateDictionary.clear();
        pricesDictionary.clear();
        precentDictionary.clear();
        praseDictionary.clear();
        numberDictionary.clear();
        indexer.resetDic(destination);

    }

    public void loadDictionary(String stem,String destination){
        indexer.loadDictionary(stem,destination);
    }

    public void mergeration() {
        indexer.mergeTermPosting();
    }

    public int numOfDoc(){return indexer.numOfDoc();}

    public HashMap<String,City> getCities(String stem,String destination) {
        return indexer.getCityPosting(stem,destination);
    }


}