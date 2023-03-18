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

    public static WeatherDataRepository dataRepository;

    public static void main(String[] args) {
        SpringApplication.run(FoodDeliveryApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(WeatherDataRepository repository) {
        return args -> {
            insertWeatherData(repository);
            System.out.println(repository.findAll());
        };
    }

    public static void insertWeatherData(WeatherDataRepository repository) {
        dataRepository = repository;

        try {
            JobDetail job = JobBuilder.newJob(CollectWeatherReport.class)
//                    .withIdentity("job", "group")
                    .build();
            Trigger trigger = TriggerBuilder.newTrigger()
//                    .withIdentity("cronTrigger", "group")
                    .withSchedule(CronScheduleBuilder.cronSchedule(ReadProperties.getProperties().getProperty("cron", "0 15 * * * ?")))
                    .build();
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
            scheduler.scheduleJob(job, trigger);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        repository.save(new WeatherData("Tartu-Tõravere", 26242, -7.5f, 2.8f, "Few clouds", 1678478563));
    }
}
