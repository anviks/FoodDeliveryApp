package com.fujitsu.trialtask.fooddelivery.weatherdatacollection;

import com.fujitsu.trialtask.fooddelivery.enums.City;
import com.fujitsu.trialtask.fooddelivery.weatherdata.WeatherData;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherXMLHandlerTest {

    private WeatherXMLHandler handler;
    private static final Random RANDOM = new Random();

    private static City getRandomCity() {
        City[] cities = City.values();
        return cities[RANDOM.nextInt(cities.length)];
    }

    private void handleXmlElement(String name, String value) {
        handler.startElement("", "", name, null);
        handler.characters(value.toCharArray(), 0, value.length());
        handler.endElement("", "", name);
    }

    @RepeatedTest(10)
    void weatherXMLHandler_WithMockedElements_ShouldParseWeatherData() {
        // Arrange
        handler = new WeatherXMLHandler();
        long timestamp = RANDOM.nextLong(3_000_000_000L);
        int wmocode = RANDOM.nextInt(20_000, 30_000);
        City city = getRandomCity();
        float airTemperature = RANDOM.nextFloat(-50.0f, 50.0f);
        float windSpeed = RANDOM.nextFloat(0.0f, 50.0f);
        String phenomenon = RandomStringUtils.random(10, true, false)
                + " " + RandomStringUtils.random(15, true, false);

        Attributes attributes = mock(Attributes.class);
        when(attributes.getValue("timestamp")).thenReturn(timestamp + "");

        // Act
        handler.startElement("", "", "observations", attributes);
        handler.characters(new char[]{}, 0, 0);

        handler.startElement("", "", "station", null);
        handler.characters(new char[]{}, 0, 0);

        handleXmlElement("name", city.getStation());
        handleXmlElement("wmocode", wmocode + "");
        handleXmlElement("longitude", "24.75");
        handleXmlElement("latitude", "59.4");
        handleXmlElement("phenomenon", phenomenon);
        handleXmlElement("visibility", "10.0");
        handleXmlElement("precipitations", "0");
        handleXmlElement("airpressure", "1003.9");
        handleXmlElement("relativehumidity", "68");
        handleXmlElement("airtemperature", airTemperature + "");
        handleXmlElement("winddirection", "270");
        handleXmlElement("windspeed", windSpeed + "");
        handleXmlElement("windspeedmax", "20.0");
        handleXmlElement("waterlevel", "8");
        handleXmlElement("waterlevel_eh2000", "5");
        handleXmlElement("watertemperature", "7.5");
        handleXmlElement("uvindex", "0.9");
        handleXmlElement("sunshineduration", "4");
        handleXmlElement("globalradiation", "191");

        handler.endElement("", "", "station");

        handler.endElement("", "", "observations");

        // Assert
        List<WeatherData> parsedWeatherData = handler.getParsedWeatherData();
        assertEquals(1, parsedWeatherData.size());
        WeatherData weatherData = parsedWeatherData.get(0);
        assertEquals(city, weatherData.getCity());
        assertEquals(wmocode, weatherData.getWmocode());
        assertEquals(airTemperature, weatherData.getAirTemperature());
        assertEquals(windSpeed, weatherData.getWindSpeed());
        assertEquals(phenomenon.toLowerCase(), weatherData.getPhenomenon());
        assertEquals(timestamp, weatherData.getTimestamp());
    }

    @Test
    void weatherXMLHandler_WithXMLFile_ShouldParseWeatherData() throws ParserConfigurationException, SAXException, IOException {
        // Arrange
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        InputStream xmlInputStream = getClass().getResourceAsStream("/sample_weather_data.xml");

        // Act
        WeatherXMLHandler handler = new WeatherXMLHandler();
        saxParser.parse(xmlInputStream, handler);

        // Assert, that all cities are present in the parsed data and there are no duplicates
        List<WeatherData> parsedWeatherData = handler.getParsedWeatherData();
        int cityCount = City.values().length;
        assertEquals(cityCount, parsedWeatherData.stream().map(WeatherData::getCity).collect(Collectors.toSet()).size());
        assertEquals(cityCount, parsedWeatherData.size());
    }
}