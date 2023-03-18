package com.trialtask.fooddeliveryapp.weather;

import com.trialtask.fooddeliveryapp.xml_reading.XMLParser;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class CollectWeatherReport implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        URL url;

        try {
            url = new URL("https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php");
            System.out.println(url.getContent());
            BufferedInputStream stream = new BufferedInputStream(url.openStream());
            Path target = Path.of("src/main/resources/weather-report.xml");
            Files.copy(stream, target, StandardCopyOption.REPLACE_EXISTING);
            stream.close();
            XMLParser.parseXML(target);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


//        File file = new File("weather_report.xml");

    }
}
