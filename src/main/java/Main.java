import java.io.InputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;

import java.util.List;
import java.util.Arrays;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpEntity;
import org.apache.http.Header;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;

import sylvain.juge.sncfstation.TrainRecord;
import sylvain.juge.sncfstation.TrainRecordParser;
import sylvain.juge.sncfstation.AsciiTable;

public class Main {

    private static class TrainStationDataFetcher {
        private static final String URL = "http://www.gares-en-mouvement.com/fr/frpno/horaires-temps-reel/dep/";
        private String downloadDepartureBoard() {
            // TODO : cache http client once and reuse it when possible
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(URL);
            try {
                HttpResponse response = client.execute(httpGet);
                HttpEntity entity = response.getEntity();
                if( null == entity ){
                    throw new RuntimeException("no entity in response");
                }
                Header encodingHeader = entity.getContentEncoding();
                String encoding = null != encodingHeader ? encodingHeader.getValue().toUpperCase() : "UTF-8";
                return EntityUtils.toString(entity,encoding);
            } catch (IOException e){
                throw new RuntimeException(e);
            } finally {
                client.getConnectionManager().shutdown();
            }
        }
    }

    public static void main(String[] args){

        String xml = null;
        if( 0 < args.length && "offline".equals(args[0])){
            System.out.println("Using Offline mode");
            xml = offlineFile();
            if( null == xml ){
                throw new RuntimeException("offline mode not available");
            }
        } else {
            TrainStationDataFetcher fetcher = new TrainStationDataFetcher();
            xml = fetcher.downloadDepartureBoard();
        }
        TrainRecordParser parser = new TrainRecordParser();
        AsciiTable table = AsciiTable.newDefault();
        for(TrainRecord record:parser.parse(xml)){
            table.addRow(record.getNumber(),record.getCity());
        }
        for(String row:table.getRows()){
            System.out.println(row);
        }
    }

    private static String offlineFile(){
        File offlineFile = new File("offline.html");
        if(!offlineFile.exists()){
            return null;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(offlineFile));
            String line  = null;
            StringBuilder stringBuilder = new StringBuilder();
            String ls = System.getProperty("line.separator");
            while( ( line = reader.readLine() ) != null ) {
                stringBuilder.append( line );
                stringBuilder.append( ls );
            }
            return stringBuilder.toString();
        } catch(IOException e){
            throw new RuntimeException(e);
        }
    }


    
    // first dummy implementation
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
