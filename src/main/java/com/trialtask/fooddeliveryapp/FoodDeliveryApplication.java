package com.trialtask.fooddeliveryapp;

import com.trialtask.fooddeliveryapp.weather.CollectWeatherReport;
import com.trialtask.fooddeliveryapp.weather.WeatherDataRepository;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FoodDeliveryApplication {

    public static WeatherDataRepository repository;

    public static void main(String[] args) {
        SpringApplication.run(FoodDeliveryApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(WeatherDataRepository dataRepository) {
        return args -> {
            repository = dataRepository;
            insertWeatherData();
            System.out.println(dataRepository.findAll());
        };
    }

    public static void insertWeatherData() {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
