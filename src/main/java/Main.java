import java.io.InputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpEntity;
import org.apache.http.Header;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;

import org.apache.commons.lang.StringEscapeUtils;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

public class Main {
    private static final XMLInputFactory XML_INPUT_FACTORY = XMLInputFactory.newInstance();
    static {
        XML_INPUT_FACTORY.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES,false);
        XML_INPUT_FACTORY.setProperty(XMLInputFactory.IS_VALIDATING,false);
        XML_INPUT_FACTORY.setProperty(XMLInputFactory.SUPPORT_DTD,false);
        XML_INPUT_FACTORY.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,true);
    }

    private static final String URL = "http://www.gares-en-mouvement.com/fr/frpno/horaires-temps-reel/dep/";

    public static void main(String[] args){

        String xml = null;
        if( 0 < args.length && "offline".equals(args[0])){
            System.out.println("Using Offline mode");
            xml = offlineFile();
            if( null == xml ){
                throw new RuntimeException("offline mode not available");
            }
        } else {
            HttpClient client = new DefaultHttpClient();
            try {
                xml = doRequest(client, URL);
            } finally {
                client.getConnectionManager().shutdown();
            }
        }
        printXml(xml);
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

    private static String doRequest(HttpClient client, String url) {
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
        }
    }

    private static void printXml(String xml){
        String unescapedHtml = StringEscapeUtils.unescapeHtml(xml);
        // we have to remove '&' chars from stream for easy parsing (not relevant in terms of "data")
        unescapedHtml = unescapedHtml.replaceAll("&",""); 
        InputStream input = new ByteArrayInputStream(unescapedHtml.getBytes());

        boolean inTable = false;
        boolean inRow = false;
        String cellType = null;

        try {
            XMLEventReader reader = XML_INPUT_FACTORY.createXMLEventReader(input);
            while(reader.hasNext()){
                XMLEvent event = reader.nextEvent();
                if(event.isStartElement()){
                    StartElement start = event.asStartElement();
                    if(tagMatch(start,"table","tab_horaires_tps_reel")){
                        inTable = true;
                    } else if(inTable && tagMatch(start,"tr")){
                        inRow = true;
                    } else if( inRow && tagMatch(start,"td")){
                        cellType = getTagClass(start);
                    }
                }
                if(event.isCharacters() && null != cellType){
                    Characters text = event.asCharacters();
                    String data = text.getData().trim();
                    if(!data.startsWith("h")){
                        System.out.println(String.format("%s \"%s\"",cellType,data));
                    }
                }
                if(event.isEndElement()){
                    EndElement end = event.asEndElement();
                    if(tagMatch(end,"table")){
                        inTable = false;
                    } else if( inTable && tagMatch(end,"tr")){
                        inRow = false;
                    } else if( inRow && tagMatch(end,"td")){
                        cellType = null;
                    }
                }
            }
        } catch( XMLStreamException e){
            throw new RuntimeException(e);
        }
    }

    private static boolean tagMatch(EndElement end, String tag){
        return tag.equals(end.getName().getLocalPart());
    }

    private static boolean tagMatch(StartElement start, String tag){
        return tag.equals(start.getName().getLocalPart());
    }

    private static boolean tagMatch(StartElement start, String tag, String classAttribute){
        String clazz = getTagClass(start);
        return tagMatch(start,tag) && classAttribute.equals(clazz);
    }

    private static String getTagClass(StartElement start){
        Attribute clazz = start.getAttributeByName(new QName("class"));
        return null != clazz ? clazz.getValue() : null;
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
