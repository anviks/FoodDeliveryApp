package com.fujitsu.trialtask.fooddelivery.xml_reading;

import com.fujitsu.trialtask.fooddelivery.entities.WeatherData;
import com.fujitsu.trialtask.fooddelivery.repositories.WeatherDataRepository;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;

/**
 * This class is responsible for parsing XML data and mapping it to WeatherData objects.
 * It extends DefaultHandler, which is a base class for SAX2 event handlers that provides default implementations for most methods.
 * It contains instance variables that are used as flags to indicate which element is currently being parsed.
 * The startElement() method is used to handle the start of an element.
 * The characters() method is used to handle the content of an element.
 * The endElement() method is used to handle the end of an element.
 * If the location of the parsed data matches one of the specified stations, the parsed WeatherData object is saved to the repository.
 */
public class WeatherXMLHandler extends DefaultHandler {
    private final WeatherDataRepository weatherDataRepository;
    private boolean station, name, wmocode, airTemperature, windSpeed, phenomenon;
    private long timestamp;
    private WeatherData weatherData;
    private final List<String> stations = List.of("Tallinn-Harku", "Tartu-Tõravere", "Pärnu");

    public WeatherXMLHandler(WeatherDataRepository weatherDataRepository) {
        this.weatherDataRepository = weatherDataRepository;
    }

    /**
     * Handles the start of an element by setting the appropriate flag based on the element's qName.
     * If the qName is "observations", its "timestamp" attribute value is parsed and stored in the instance variable "timestamp".
     *
     * @param uri        the Namespace URI, or the empty string if the element has no Namespace URI or if Namespace processing is not being performed
     * @param localName  the local name (without prefix), or the empty string if Namespace processing is not being performed
     * @param qName      the qualified name (with prefix), or the empty string if qualified names are not available
     * @param attributes the attributes attached to the element; if there are no attributes, it will be an empty Attributes object
     * @see org.xml.sax.ContentHandler#startElement(String, String, String, Attributes)
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        switch (qName) {
            case "station" -> station = true;
            case "name" -> name = true;
            case "wmocode" -> wmocode = true;
            case "airtemperature" -> airTemperature = true;
            case "windspeed" -> windSpeed = true;
            case "phenomenon" -> phenomenon = true;
            case "observations" -> timestamp = Long.parseLong(attributes.getValue("timestamp"));
        }
    }

    /**
     * Handles the content of an element by setting the appropriate value in the current WeatherData object
     * based on the element's tag.
     * If the current element is "station" and no WeatherData object has been created yet,
     * a new WeatherData object is created and its timestamp is set.
     *
     * @param ch     the characters from the XML document
     * @param start  the start position in the array
     * @param length the number of characters to read from the array
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
    @Override
    public void characters(char[] ch, int start, int length) {
        String string = new String(ch, start, length);
        if (name) {
            weatherData.setLocation(string);
        } else if (wmocode) {
            weatherData.setWmocode(Integer.parseInt(string));
        } else if (airTemperature) {
            weatherData.setAirTemperature(Float.parseFloat(string));
        } else if (windSpeed) {
            weatherData.setWindSpeed(Float.parseFloat(string));
        } else if (phenomenon) {
            weatherData.setPhenomenon(string);
        } else if (station && weatherData == null) {
            weatherData = new WeatherData();
            weatherData.setTimestamp(timestamp);
        }
    }

    /**
     * Handles the end of an element by setting the appropriate flag as false, based on the element's tag.
     * If the tag is "station", that means the station's weather report has ended,
     * so it will save the resulting WeatherData object to the database and assign null as the object's new value.
     * <p>
     * This method is called after all the content and attributes of the element have been processed.
     *
     * @param uri       the Namespace URI, or the empty string if the element has no
     *                  Namespace URI or if Namespace processing is not being performed
     * @param localName the local name (without prefix), or the empty string if
     *                  Namespace processing is not being performed
     * @param qName     the qualified name (with prefix), or the empty string if
     *                  qualified names are not available
     * @see org.xml.sax.ContentHandler#endElement(String, String, String)
     */
    @Override
    public void endElement(String uri, String localName, String qName) {
        switch (qName) {
            case "name" -> name = false;
            case "wmocode" -> wmocode = false;
            case "airtemperature" -> airTemperature = false;
            case "windspeed" -> windSpeed = false;
            case "phenomenon" -> phenomenon = false;
            case "station" -> {
                station = false;
                if (stations.contains(weatherData.getLocation())) {
                    weatherDataRepository.save(weatherData);
                }
                weatherData = null;
            }
        }
    }
}
