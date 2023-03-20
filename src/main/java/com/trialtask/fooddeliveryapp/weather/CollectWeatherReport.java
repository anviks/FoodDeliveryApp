package com.trialtask.fooddeliveryapp.weather;

import com.trialtask.fooddeliveryapp.xml_reading.XMLParser;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * A class that implements the Quartz Job interface to collect the weather report data from a URL
 * and save it in an XML file.
 */
public class CollectWeatherReport implements Job {

    /**
     * Executes the job and collects the weather report data from the specified URL and saves it in an XML file.
     * If any exception occurs during the parsing process, a stack trace is printed to the console.
     *
     * @param jobExecutionContext the context of the job execution
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        URL url;
        try {
            url = new URL("https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php");
            BufferedInputStream stream = new BufferedInputStream(url.openStream());
            Path target = Path.of("src/main/resources/weather-report.xml");
            Files.copy(stream, target, StandardCopyOption.REPLACE_EXISTING);
            stream.close();
            XMLParser.parseXML(target);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
