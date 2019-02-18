package sample;

import javafx.beans.property.SimpleStringProperty;

public class DictionaryDisplayer {

    private final SimpleStringProperty term;
    private final SimpleStringProperty numOfApp;



    public DictionaryDisplayer(String t, String num){

        this.term = new SimpleStringProperty(t);
        this.numOfApp = new SimpleStringProperty(num);

    }

    public String getTerm() { return term.get(); }

    public SimpleStringProperty term() { return term; }

    public String getNumOfApp() {
        return numOfApp.get();
    }

    public SimpleStringProperty numOfApp() {
        return numOfApp;
    }
}
