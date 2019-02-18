package Index;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

public class Semantic{
    private org.json.simple.parser.JSONParser toParse;
    public HashMap<String,Integer>semanticWords;

    //contractor
    public Semantic(){
        semanticWords=new HashMap<>();
    }

    //Hash include the words from the origin query and the words return from the api connection
    public HashMap<String, Integer> getSemanticWords() {
        return semanticWords;
    }

    //the connection with the api for bring the semantic words when the user choose that.
    //we limit the number of the return semantic words for the api to be 10
    public void getSemanticWordsApi(LinkedList<String> words){
        OkHttpClient client=new OkHttpClient();
        for(int i=0;i<words.size();i++) {
            if ((words.get(i)).length() > 0) {
                String url = "https://api.datamuse.com/words?ml=" + words.get(i);
                //semanticWords.put(words.get(i),1);
                Request request = new Request.Builder().url(url).build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Object object = null;
                toParse = new org.json.simple.parser.JSONParser();
                try {
                        object = toParse.parse(response.body().string());
                } catch (ParseException e) {
                        e.printStackTrace();
                } catch (IOException e) {
                        e.printStackTrace();
                }


                if (object != null) {
                Object[] parser = ((JSONArray) object).toArray();
                int limitSemantic=0;
                    for (Object O : parser) {
                        if(limitSemantic==3)
                          break;
                         else {
                            semanticWords.put(((String) ((JSONObject) O).get("word")), 1);
                             limitSemantic++;
                         }
                    }
                }
            }
        }
    }
}
