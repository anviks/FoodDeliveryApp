package com.fujitsu.trialtask.fooddelivery.weatherdatacollection;

import com.fujitsu.trialtask.fooddelivery.weatherdata.WeatherData;
import com.fujitsu.trialtask.fooddelivery.weatherdata.WeatherDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.util.List;


/**
 * Service class responsible for collecting weather reports from an external XML source
 * and saving them to the database.
 */
@Service
class CollectWeatherReport {
    private static final Logger log = LoggerFactory.getLogger(CollectWeatherReport.class);
    private final WeatherDataRepository weatherDataRepository;
    private static final String OBSERVATIONS_URL = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";

    @Autowired
    public CollectWeatherReport(WeatherDataRepository weatherDataRepository) {
        this.weatherDataRepository = weatherDataRepository;
    }

    /**
     * Method executed when the application is ready and scheduled to run periodically based on the configured cron expression.
     */
    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(cron = "${cron}")
    public void execute() {
        InputStream inputStream = null;

        try {
            log.info("Collecting weather report...");
            URL url = new URL(OBSERVATIONS_URL);
            inputStream = url.openStream();
            List<WeatherData> parsedData = parseXML(inputStream);
            weatherDataRepository.saveAll(parsedData);
            log.info("Weather report collected successfully");
        } catch (MalformedInputException e) {
            log.error("Error creating URL object", e);
        } catch (IOException e) {
            log.error("Error collecting weather report", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("Error closing input stream", e);
                }
            }
        }
    }

    private List<WeatherData> parseXML(InputStream inputStream) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            WeatherXMLHandler handler = new WeatherXMLHandler();
            saxParser.parse(inputStream, handler);
            return handler.getParsedWeatherData();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            log.error("Error parsing XML", e);
            return List.of();
        }
    }
}
