package sample;

import javafx.beans.property.SimpleStringProperty;

public class EntityDisplayer {


        private final SimpleStringProperty docId;
        private final SimpleStringProperty entityTerms;



        public EntityDisplayer(String docId, String terms){

            this.docId = new SimpleStringProperty(docId);
            this.entityTerms = new SimpleStringProperty(terms);

        }

        public String getDocId() { return docId.get(); }

        public SimpleStringProperty docId() { return docId; }

        public String getEntityTerms() {
            return entityTerms.get();
        }

        public SimpleStringProperty entityTerms() {
            return entityTerms;
        }


    }


