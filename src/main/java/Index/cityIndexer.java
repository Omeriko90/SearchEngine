package Index;

import Objects.City;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.HashMap;


public class cityIndexer {
    private org.json.simple.parser.JSONParser toParse;
    public HashMap<String,City> cityDic;

    //constructor
    public cityIndexer(){
        cityDic=new HashMap<>();
    }

    //connection API to the cityAPI bring all the capital city exists
    public void connectionApi() {
        OkHttpClient client=new OkHttpClient();
        String url="https://restcountries.eu/rest/v2/all?fielss=capital;name;population,currency";
        Request request=new Request.Builder().url(url).build();
        Response response=null;
        try {
            response=client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Object object=null;
        toParse=new org.json.simple.parser.JSONParser();
        try {
            object=toParse.parse(response.body().string());
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(object!=null){
            String capitalCity="";
            String country="";
            String currency="";
            String population="";
            Object[] parser=((JSONArray)object).toArray();
            for(Object O:parser){
                capitalCity=(String)((JSONObject) O).get("capital");
                country=(String)((JSONObject) O).get("name");
                population=(String)((JSONObject) O).get("population").toString();
                JSONArray array=((JSONArray)((JSONObject) O).get("currencies"));
                for(Object obj: array)
                    currency=(String)((JSONObject) obj).get("code");
                cityDic.put(capitalCity.toUpperCase(),new City(country,population,currency,""));
            }

        }

    }

    //getter
    public HashMap<String,City> getCityPos(){return cityDic;}


}
