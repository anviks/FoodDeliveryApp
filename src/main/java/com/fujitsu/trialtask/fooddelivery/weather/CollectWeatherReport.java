package com.fujitsu.trialtask.fooddelivery.weather;

import com.fujitsu.trialtask.fooddelivery.repositories.WeatherDataRepository;
import com.fujitsu.trialtask.fooddelivery.xml_reading.XMLParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * A class that implements the Quartz Job interface to collect the weather report data from a URL
 * and save it in an XML file.
 */
@Service
public class CollectWeatherReport {
    private static final Logger logger = LoggerFactory.getLogger(CollectWeatherReport.class);
    private final WeatherDataRepository weatherDataRepository;

    @Autowired
    public CollectWeatherReport(WeatherDataRepository weatherDataRepository) {
        this.weatherDataRepository = weatherDataRepository;
    }

    /**
     * Executes the job and collects the weather report data from the specified URL and saves it in an XML file.
     * If any exception occurs during the parsing process, a stack trace is printed to the console.
     */
    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(cron = "${cron}")
    public void execute() {
        URL url;
        try {
            url = new URL("https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php");
            BufferedInputStream stream = new BufferedInputStream(url.openStream());
            Path target = Path.of("src/main/resources/weather-report.xml");
            Files.copy(stream, target, StandardCopyOption.REPLACE_EXISTING);
            stream.close();
            XMLParser.parseXML(target, this.weatherDataRepository);
        } catch (IOException e) {
            logger.error("Error collecting weather report", e);
        }
    }
}
