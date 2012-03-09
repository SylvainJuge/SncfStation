import java.io.InputStream;
import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;

public class Main {
    // -- features
    // TODO : parse whole table and display it as ascii
    // -- enhancements
    // TODO : allow for another station
    // TODO : allow for departure and arrivals
    // TODO : do a statistical analysis of which train arrives at which platform
    private static final String URL = "http://www.gares-en-mouvement.com/fr/frpno/horaires-temps-reel/dep/";

    public static void main(String[] args){
        HttpClient client = new DefaultHttpClient();
        try {
            print(doRequest(client, URL));
        } finally {
            client.getConnectionManager().shutdown();
        }
    }


    private static InputStream doRequest(HttpClient client, String url) {
        HttpGet httpGet = new HttpGet(URL);
        try {
            HttpResponse response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if( null == entity ){
                throw new RuntimeException("no entity in response");
            }
            return entity.getContent();
        } catch (IOException e){
            throw new RuntimeException(e);
        }
        // TODO : detect encoding from response
    }

    private static void print(InputStream input){

        int read = 0;
        do {
            try {
                read = input.read();
            } catch(IOException e){
                throw new RuntimeException(e);
            }
            if( 0 <= read ){
                System.out.write(read);
            }
        } while( 0 <= read );
    }

}
