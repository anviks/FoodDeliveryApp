package com.fujitsu.trialtask.fooddelivery.xml_reading;

import com.fujitsu.trialtask.fooddelivery.repositories.WeatherDataRepository;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * A utility class for parsing a weather report XML file using the SAX (Simple API for XML) parser.
 */
public class XMLParser {

    /**
     * Creates a {@link SAXParser} and a {@link SAXParserFactory} object and reads weather reports to a database using {@link WeatherXMLHandler}.
     * If any exception occurs during the parsing process, a stack trace is printed to the console.
     *
     * @param path the path of the weather report file
     */
    public static void parseXML(Path path, WeatherDataRepository weatherDataRepository) {
        try {
            File file = new File(path.toUri());
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            WeatherXMLHandler handler = new WeatherXMLHandler(weatherDataRepository);
            saxParser.parse(file, handler);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }
}

