package com.trialtask.fooddeliveryapp;

import com.trialtask.fooddeliveryapp.weather.CollectWeatherReport;
import com.trialtask.fooddeliveryapp.weather.WeatherDataRepository;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * The main class for the Food Delivery Application.
 */
@SpringBootApplication
public class FoodDeliveryApplication {

    public static WeatherDataRepository repository;

    /**
     * The main method for the application.
     *
     * @param args the command-line arguments for the application
     */
    public static void main(String[] args) {
        SpringApplication.run(FoodDeliveryApplication.class, args);
    }

    /**
     * Creates a CommandLineRunner that initialises the WeatherDataRepository.
     *
     * @param dataRepository the WeatherDataRepository used by the application
     * @return a CommandLineRunner that initialises the WeatherDataRepository
     */
    @Bean
    public CommandLineRunner run(WeatherDataRepository dataRepository) {
        return args -> {
            repository = dataRepository;
            scheduleWeatherDataCollection();
        };
    }

    /**
     * This method schedules the collection of weather data at regular intervals using Quartz Scheduler.
     * The schedule is determined by the cron expression specified in the properties file.
     * If the cron expression is not specified in the properties file, the default schedule is used.
     * The default schedule is set to run the weather data collection job at the 15th minute of every hour.
     * If an error occurs during the scheduling process, the stack trace is printed to the console.
     */
    public static void scheduleWeatherDataCollection() {
        try {
            JobDetail job = JobBuilder.newJob(CollectWeatherReport.class).build();
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withSchedule(
                            CronScheduleBuilder.cronSchedule(
                                    ReadProperties.getProperties().getProperty(
                                            "cron", "0 15 * * * ?"
                                    )
                            )
                    ).build();
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
