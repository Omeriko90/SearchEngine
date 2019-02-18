package sample;
import Read.ReadFile;
import Read.Searcher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import Objects.City;
import javafx.stage.Stage;
import Objects.Term;
import javax.swing.*;
import java.io.*;
import java.util.*;



public class Controller {
    public javafx.scene.control.Button btnQueries;
    public javafx.scene.control.Button btnFolderForQueriesResult;
    public javafx.scene.control.TextField folderToQueryResult;
    public javafx.scene.control.TextField from;
    public javafx.scene.control.TextField save;
    public javafx.scene.control.CheckBox stemming;
    public javafx.scene.control.ChoiceBox language;
    public javafx.scene.control.TextField QuerieFile;
    public javafx.scene.control.Label type;
    public javafx.scene.control.Label file;
    public javafx.scene.control.Label city;
    public javafx.scene.control.Label lblToQuerieResult;
    public javafx.scene.control.Button btnRunQueries;
    public javafx.scene.control.CheckBox checkEntity;
    public javafx.scene.control.CheckBox checkSemantic;
    public javafx.scene.control.Button browseQuery;
    public javafx.scene.control.TextField txtTypeQuerie;
    public javafx.scene.control.MenuButton cityChooser;
    public ReadFile readFile;
    public Searcher searcher;


    @FXML
    public TableView table = new TableView();


    public void fromBrowse(){
        DirectoryChooser fromChooser = new DirectoryChooser();
        from.setText(fromChooser.showDialog(null).getAbsolutePath());
    }


    public void saveBrowse(ActionEvent actionEvent) {
        DirectoryChooser saveChooser = new DirectoryChooser();
        save.setText(saveChooser.showDialog(null).getAbsolutePath());
    }

    public void Run(ActionEvent actionEvent) {
        if(from.getText().length()==0 || save.getText().length()==0){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("Something is empty look it up");
            alert.show();
        }
        else {
            long start=System.nanoTime();
            boolean stem = stemming.isSelected();
            String destination = save.getText();
            if(stem)
                destination+="\\Stemming";
            else
                destination+="\\WithOutStemming";
            readFile = new ReadFile(" ",stem,destination,from.getText());
            File[] folder = new File(from.getText()).listFiles();
            int dirId=1;
            for(int i=0; i< folder.length ;i++) {
                if(folder[i].isDirectory()) {
                    File[] tmp = folder[i].listFiles();
                    readFile.setPath(tmp[0].getAbsolutePath());
                    readFile.readFiles();
                }
                if(folder.length-i>15) {
                    if (i % 15 == 0 && i>0) {
                        readFile.writeAll(String.valueOf(dirId)+String.valueOf(stem));
                        dirId++;
                    }
                }
            }
            int numOfFiles = readFile.numOfDoc();
            readFile.writeAll(String.valueOf(dirId));
            readFile.clearCity();
            readFile.mergeration();
            long end=System.nanoTime();
            long elapsedTime = end - start;
            double seconds = (double)elapsedTime / 1_000_000_000.0;
            language();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("num of file: "+numOfFiles+" num of uniqe terms: "+readFile.dicSize() +" in total time: "+String.valueOf(seconds)+" seconds");
            alert.show();
            btnQueries.setDisable(false);
            btnQueries.setVisible(true);
        }

    }


    public void reset(ActionEvent actionEvent) {
        String stem;
        if(stemming.isSelected())
            stem="\\Stemming";
        else
            stem="\\WithOutStemming";
        String destination = save.getText();
        destination+=stem;
        if(readFile==null){
            readFile = new ReadFile(" ",stemming.isSelected(),destination,from.getText());
        }
        readFile.resetDic(destination);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("all files deleted");
        alert.show();
    }

    public void loadDictionary(ActionEvent actionEvent) {
        String stem;
        if(stemming.isSelected())
            stem="\\Stemming";
        else
            stem="\\WithOutStemming";
        String destination = save.getText();
        if(readFile==null){
            destination+=stem;
            readFile = new ReadFile(" ",stemming.isSelected(),destination,from.getText());
            destination=save.getText();
        }
        readFile.loadDictionary(stem,destination);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("finish update dictionary, dictionary size: "+readFile.getDicSize());
        alert.show();
    }

    //show all possible language in corpus
    private void language(){
        language.setVisible(true);
        HashMap<String,String> LAN=new HashMap<>();
        LAN=readFile.getLango();
        ArrayList<String> items=new ArrayList<>();
        items.add("Choose language");
        for (Map.Entry entry:LAN.entrySet()) {
            if (matchesNumber((String)entry.getKey()))
                continue;
            else
                items.add((String)entry.getKey());
        }
        language.setItems(FXCollections.observableArrayList(items));
    }



    private boolean matchesNumber(String word){
        for(int i=0;i<word.length();i++){
            if(word.charAt(i)>='0' && word.charAt(i)<='9')
                return true;
        }
        return false;
    }

    public void printDIc(ActionEvent actionEvent) {
        TreeMap<String,String> forPrint =readFile.printDic();
        DisplayWindowDic(forPrint);

    }

    public void runPartTwo(ActionEvent actionEvent){
        type.setVisible(true);
        file.setVisible(true);
        city.setVisible(true);
        txtTypeQuerie.setVisible(true);
        btnQueries.setVisible(true);
        btnQueries.setDisable(false);
        QuerieFile.setVisible(true);
        checkEntity.setVisible(true);
        checkSemantic.setVisible(true);
        browseQuery.setVisible(true);
        btnRunQueries.setVisible(true);
        btnFolderForQueriesResult.setDisable(false);
        btnFolderForQueriesResult.setVisible(true);
        cityChooser.setVisible(true);
        cityChooser.setDisable(false);
        folderToQueryResult.setVisible(true);
        lblToQuerieResult.setVisible(true);
        cities();

    }

    public void runQueries(ActionEvent actionEvent){
        //just for check need to bring pass from save and boolean from stemming
        String passTo=save.getText();
        String passFrom=from.getText();
        LinkedList<String> cityFilter=cityChooses();
        if(txtTypeQuerie.getText().length()==0 && QuerieFile.getText().length()==0){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("You must enter a query or query file");
            alert.show();
        }
        else {
            String query = "";
            if (txtTypeQuerie.getText().length() > 0)
               searcher = new Searcher(stemming.isSelected(),txtTypeQuerie.getText(),passFrom,passTo,folderToQueryResult.getText(), checkSemantic.isSelected(),cityFilter, "typeQuery");
            else
               searcher = new Searcher(stemming.isSelected(),QuerieFile.getText(),passFrom,passTo,folderToQueryResult.getText(),checkSemantic.isSelected(),cityFilter,"fileQuery");
        }
        if(checkEntity.isSelected())
            bringStrongEntity(save.getText(),stemming.isSelected());
    }

    private void bringStrongEntity(String to,boolean stem) {
        HashMap<String,String>strongEntityPerDoc=new HashMap<>();
        strongEntityPerDoc=searcher.bringStrongEntity(to,stem);
        DisplayWindow(strongEntityPerDoc);
    }

    private ObservableList<EntityDisplayer> getData(HashMap<String,String>allrecords) {
        ObservableList<EntityDisplayer> rec = FXCollections.observableArrayList();
        for (Map.Entry entry:allrecords.entrySet()) {
            rec.add(new EntityDisplayer((String)entry.getKey(),(String)entry.getValue()));
        }
        return rec;
    }

    private ObservableList<DictionaryDisplayer> getDataDic(TreeMap<String,String>allrecords) {
        ObservableList<DictionaryDisplayer> rec = FXCollections.observableArrayList();
        for (Map.Entry entry:allrecords.entrySet()) {
            rec.add(new DictionaryDisplayer((String)entry.getKey(),(String)entry.getValue()));
        }
        return rec;
    }


    public void fromQuery(){
        FileChooser fromChooser = new FileChooser();
        QuerieFile.setText(fromChooser.showOpenDialog(null).getAbsolutePath());
    }

    public void toQuery(){
        DirectoryChooser fromChooser = new DirectoryChooser();
        folderToQueryResult.setText(fromChooser.showDialog(null).getAbsolutePath());
    }

    //show all possible cities in corpus
    private void cities(){
        HashMap<String, City> LAN;
        String stem="";
        String passTo=save.getText();
        String passFrom=from.getText();
        if(stemming.isSelected())
            stem="\\Stemming";
        else
            stem="\\WithOutStemming";
        if(readFile==null){
            readFile = new ReadFile(" ",true,passTo,passFrom);
        }
        LAN=readFile.getcities(stem,passTo);
        final ObservableList<MenuItem> items=FXCollections.observableArrayList();
        for (Map.Entry entry:LAN.entrySet()) {
            if (matchesNumber((String)entry.getKey()))
                continue;
            else {
                CheckMenuItem m=new CheckMenuItem((String) entry.getKey());
                items.add(m);
            }
        }
        cityChooser.getItems().setAll(items);

    }

    private LinkedList<String> cityChooses(){
        LinkedList<String>cityFilter=new LinkedList<>();
        int numberOfFilter=cityChooser.getItems().size();
        String city="";
        for(int i=0;i<numberOfFilter;i++) {
            if(((CheckMenuItem)cityChooser.getItems().get(i)).isSelected()) {
                city = (cityChooser.getItems().get(i)).getText();
                while (city.charAt(0)==' ')
                    city=city.substring(1);
                while (city.charAt(city.length()-1)==' ')
                    city=city.substring(0,city.length()-1);
                cityFilter.add(city);
            }
        }
        return cityFilter;
    }

    public void DisplayWindow(HashMap<String,String>strongEntityPerDoc) {
        try {
            Stage stage = new Stage();
            Scene scene = new Scene(new Group());
            stage.setTitle("Entity ");
            stage.setWidth(1000);
            stage.setHeight(700);
            final Label lable = new Label("Entity per document");
            lable.setFont(new Font("Gabriola", 24));
            table.setEditable(false);

            TableColumn docId = new TableColumn("doc number");
            docId.setMinWidth(200);
            docId.setCellValueFactory(new PropertyValueFactory<EntityDisplayer, String>("docId"));

            TableColumn entityTerms = new TableColumn("Entity terms");
            entityTerms.setMinWidth(600);
            entityTerms.setCellValueFactory(new PropertyValueFactory<EntityDisplayer, String>("entityTerms"));

            table.setItems(getData(strongEntityPerDoc));
            table.getColumns().addAll(docId, entityTerms);
            table.setMinHeight(800);

            final VBox vbox = new VBox();
            vbox.setSpacing(20);
            vbox.setPadding(new Insets(20, 0, 0, 10));
            vbox.getChildren().addAll(lable, table);

            ((Group) scene.getRoot()).getChildren().addAll(vbox);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void DisplayWindowDic(TreeMap<String,String> dictionary ){
        try {
            Stage stage = new Stage();
            Scene scene = new Scene(new Group());
            stage.setTitle("Dictionary ");
            stage.setWidth(600);
            stage.setHeight(800);
            final Label lable = new Label("Final Dictionary");
            lable.setFont(new Font("Gabriola", 24));
            table.setEditable(false);

            TableColumn term = new TableColumn("Term");
            term.setMinWidth(250);
            term.setCellValueFactory(new PropertyValueFactory<DictionaryDisplayer, String>("term"));

            TableColumn numOfApp = new TableColumn("Number of appearances");
            numOfApp.setMinWidth(250);
            numOfApp.setCellValueFactory(new PropertyValueFactory<DictionaryDisplayer, String>("numOfApp"));

            table.setItems(getDataDic(dictionary));
            table.getColumns().addAll(term, numOfApp);
            table.setMinHeight(800);

            final VBox vbox = new VBox();
            vbox.setSpacing(20);
            vbox.setPadding(new Insets(20, 0, 0, 10));
            vbox.getChildren().addAll(lable, table);

            ((Group) scene.getRoot()).getChildren().addAll(vbox);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
