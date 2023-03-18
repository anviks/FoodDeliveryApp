package com.trialtask.fooddeliveryapp;

import com.trialtask.fooddeliveryapp.weather.WeatherData;
import com.trialtask.fooddeliveryapp.weather.WeatherDataRepository;
import com.trialtask.fooddeliveryapp.xml_reading.XMLParser;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureTestDatabase
class FoodDeliveryApplicationTests {

    @Autowired
    private WeatherDataRepository repository;

    @BeforeEach
    void setup(final TestInfo info) {
        if (!info.getTags().contains("no-setup")) {
            XMLParser.parseXML(Path.of("./src/test/resources/weather-report.xml"));
            System.out.println("done setup");
        }
    }

    @AfterEach
    void cleanup() {
        repository.deleteAll();
        System.out.println("done deletion");
    }

    @Test
    void canSaveToDatabase() {
        WeatherData sampleData = new WeatherData(
                12345,
                "Saaremaa",
                67890,
                "Amazing",
                -200.5f,
                500.92f
        );

        repository.save(sampleData);
        Optional<WeatherData> optionalEntry = repository.findById(sampleData.getId());
        assertTrue(optionalEntry.isPresent());
        WeatherData entry = optionalEntry.get();

        assertEquals(sampleData, entry);
    }

    @Tag("no-setup")
    @Test
    void canParseXMLFile() {
        assertEquals(0, getRepositoryContent().size());
        XMLParser.parseXML(Path.of("./src/test/resources/weather-report.xml"));
        assertEquals(6, getRepositoryContent().size());
    }

    @Test
    void databaseHasCorrectDataFromXMLFile() {
        List<WeatherData> repositoryContent = getRepositoryContent();

        assertEquals(new WeatherData(
                1678622009,
                "Pärnu",
                41803,
                null,
                -0.1f,
                5.4f
        ), repositoryContent.get(2));

        assertEquals(new WeatherData(
                1678639129,
                "Tartu-Tõravere",
                26242,
                "Clear",
                -2.6f,
                3.3f
        ), repositoryContent.get(4));
    }

    @Test
    void idGenerationDoesntSkipNumbers() {
        for (int i = 0; i < 100; i++) {
            repository.save(new WeatherData());
        }

        List<WeatherData> repositoryContent = getRepositoryContent();
        long firstId = repositoryContent.get(0).getId();
        long lastId = repositoryContent.get(repositoryContent.size() - 1).getId();

        for (long i = firstId; i <= lastId; i++) {
            assertTrue(repository.existsById(i));
        }

        assertFalse(repository.existsById(lastId + 1));
    }

    private List<WeatherData> getRepositoryContent() {
        List<WeatherData> repositoryContent = new ArrayList<>();
        repository.findAll().iterator().forEachRemaining(repositoryContent::add);
        return repositoryContent;
    }
}
