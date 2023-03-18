package com.trialtask.fooddeliveryapp.xml_reading;

import com.trialtask.fooddeliveryapp.FoodDeliveryApplication;
import com.trialtask.fooddeliveryapp.weather.WeatherData;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;

public class WeatherXMLHandler extends DefaultHandler {
    private boolean station, name, wmocode, airTemperature, windSpeed, phenomenon;
    private long timestamp;
    private WeatherData weatherData;
    private final List<String> stations = List.of("Tallinn-Harku", "Tartu-Tõravere", "Pärnu");

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
                    FoodDeliveryApplication.repository.save(weatherData);
                }
                weatherData = null;
            }
        }
    }
}
