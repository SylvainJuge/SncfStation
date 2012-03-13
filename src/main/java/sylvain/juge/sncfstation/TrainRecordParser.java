package sylvain.juge.sncfstation;

import java.util.List;
import java.util.ArrayList;

import java.io.InputStream;
import java.io.ByteArrayInputStream;

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

public class TrainRecordParser {

    private static final XMLInputFactory XML_INPUT_FACTORY = XMLInputFactory.newInstance();
    static {
        XML_INPUT_FACTORY.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES,false);
        XML_INPUT_FACTORY.setProperty(XMLInputFactory.IS_VALIDATING,false);
        XML_INPUT_FACTORY.setProperty(XMLInputFactory.SUPPORT_DTD,false);
        XML_INPUT_FACTORY.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,true);
    }

    public List<TrainRecord> parse(String xhtml){
        String unescapedHtml = StringEscapeUtils.unescapeHtml(xhtml);
        // we have to remove '&' chars from stream for easy parsing (not relevant in terms of "data")
        unescapedHtml = unescapedHtml.replaceAll("&",""); 
        InputStream input = new ByteArrayInputStream(unescapedHtml.getBytes());

        boolean inTable = false;
        boolean inRow = false;
        String cellType = null;

        String city = null;
        String number = null;
        String time = null;
        String platform = null;
        List<TrainRecord> result = new ArrayList<TrainRecord>();

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
                        city = null;
                        number = null;
                        time = null;
                        platform = null;
                    } else if( inRow && tagMatch(start,"td")){
                        cellType = getTagClass(start);
                    }
                }
                if(event.isCharacters() && null != cellType){
                    Characters text = event.asCharacters();
                    String data = text.getData().trim();
                    if(!data.startsWith("h")){
                        // TODO : rewrite this with a jdk7 switch with strings literals
                        if(cellType.endsWith("heure")){
                            // TODO : make sure that time is propertly formatted and padded with zeros if necessary
                            if( null == time ){
                                time = data;
                            } else {
                                time = time + ":" + data;
                            }
                        } else if(cellType.endsWith("originedestination")){
                            city = data;
                        } else if(cellType.endsWith("numero")){
                            number = data;
                        } else if(cellType.endsWith("voie")){
                            platform = data;
                        }
                    }
                }
                if(event.isEndElement()){
                    EndElement end = event.asEndElement();
                    if(tagMatch(end,"table")){
                        inTable = false;
                    } else if( inTable && tagMatch(end,"tr")){
                        inRow = false;
                        // TODO : terminate or cancel current TrainRecord
                        if( null != city && null != number && null != time ){
                            result.add(new TrainRecord(city,number,time,platform));
                        }
                    } else if( inRow && tagMatch(end,"td")){
                        cellType = null;
                    }
                }
            }
        } catch( XMLStreamException e){
            throw new RuntimeException(e);
        }
        return result;
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
}
