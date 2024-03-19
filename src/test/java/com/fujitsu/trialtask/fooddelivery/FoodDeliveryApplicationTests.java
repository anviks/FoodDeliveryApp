package com.fujitsu.trialtask.fooddelivery;

import com.fujitsu.trialtask.fooddelivery.entities.WeatherData;
import com.fujitsu.trialtask.fooddelivery.repositories.WeatherDataRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


//@AutoConfigureTestDatabase
@SpringBootTest
@AutoConfigureMockMvc
class FoodDeliveryApplicationTests {

    @MockBean
    private WeatherDataRepository weatherDataRepository;
    @Autowired
    private MockMvc mockMvc;


    @Test
    public void testGetDeliveryFee() throws Exception {
        // Mock WeatherData
        WeatherData weatherData = new WeatherData();
        weatherData.setAirTemperature(20F); // Set air temperature
        weatherData.setWindSpeed(10F); // Set wind speed
        Mockito.when(weatherDataRepository.findFirstByLocationContainingIgnoreCaseOrderByTimestampDesc(Mockito.anyString()))
                .thenReturn(weatherData);

        // Perform GET request
        mockMvc.perform(get("/api/delivery/TALLINN")
                        .param("vehicle", "CAR")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.city").value("TALLINN"))
                .andExpect(jsonPath("$.vehicle").value("car"))
                .andExpect(jsonPath("$.fee").value(4.0)); // Assuming a fee of 4 for this scenario

        weatherData.setPhenomenon("Glaze");

        mockMvc.perform(get("/api/delivery/TALLINN")
                        .param("vehicle", "SCOOTER")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }


//    private final DeliveryFeeCalculator tallinnScooter = new DeliveryFeeCalculator(
//            City.TALLINN, Vehicle.SCOOTER, repository
//    );
//    private final DeliveryFeeCalculator tartuCar = new DeliveryFeeCalculator(
//            City.TARTU, Vehicle.CAR, repository
//    );
//    private final DeliveryFeeCalculator parnuBike = new DeliveryFeeCalculator(
//            City.PÄRNU, Vehicle.BIKE, repository
//    );
//
//
//    @BeforeEach
//    void setup(final TestInfo info) {
//        if (!info.getTags().contains("no-setup")) {
//            XMLParser.parseXML(Path.of("./src/test/resources/weather-report.xml"));
//        }
//    }
//
//    @AfterEach
//    void cleanup() {
//        repository.deleteAll();
//    }
//
//    @Test
//    void canSaveToDatabase() {
//        WeatherData sampleData = new WeatherData(
//                12345,
//                "Saaremaa",
//                67890,
//                "Amazing",
//                -200.5f,
//                500.92f
//        );
//
//        repository.save(sampleData);
//        Optional<WeatherData> optionalEntry = repository.findById(sampleData.getId());
//        assertTrue(optionalEntry.isPresent());
//
//        WeatherData entry = optionalEntry.get();
//        assertEquals(sampleData, entry);
//    }
//
//    @Tag("no-setup")
//    @Test
//    void canParseXMLFile() {
//        assertEquals(0, getRepositoryAsList().size());
//        XMLParser.parseXML(Path.of("./src/test/resources/weather-report.xml"));
//        assertEquals(6, getRepositoryAsList().size());
//    }
//
//    @Test
//    void databaseHasCorrectDataFromXMLFile() {
//        List<WeatherData> repositoryContent = getRepositoryAsList();
//
//        assertEquals(new WeatherData(
//                1678622009,
//                "Pärnu",
//                41803,
//                null,
//                -0.1f,
//                5.4f
//        ), repositoryContent.get(2));
//
//        assertEquals(new WeatherData(
//                1678639129,
//                "Tartu-Tõravere",
//                26242,
//                "Clear",
//                -2.6f,
//                3.3f
//        ), repositoryContent.get(4));
//    }
//
//    @Test
//    void idGenerationDoesntSkipNumbers() {
//        for (int i = 0; i < 100; i++) {
//            repository.save(new WeatherData());
//        }
//
//        List<WeatherData> repositoryContent = getRepositoryAsList();
//        long firstId = repositoryContent.get(0).getId();
//        long lastId = repositoryContent.get(repositoryContent.size() - 1).getId();
//
//        for (long i = firstId; i <= lastId; i++) {
//            assertTrue(repository.existsById(i));
//        }
//
//        assertFalse(repository.existsById(lastId + 1));
//    }
//
//    @Test
//    void getLatestWeatherDataReturnsMostRecentReport() {
//        DeliveryFeeCalculator calculator = new DeliveryFeeCalculator(
//                City.TALLINN, Vehicle.CAR, repository
//        );
//        WeatherData weatherTallinn = calculator.getLatestWeatherData(City.TALLINN);
//        WeatherData weatherTartu = calculator.getLatestWeatherData(City.TARTU);
//        WeatherData weatherParnu = calculator.getLatestWeatherData(City.PÄRNU);
//
//        assertEquals("Variable clouds", weatherTallinn.getPhenomenon());
//        assertEquals(-1.3f, weatherTallinn.getAirTemperature());
//        assertEquals(3.7f, weatherTallinn.getWindSpeed());
//
//        assertEquals("Clear", weatherTartu.getPhenomenon());
//        assertEquals(-2.6f, weatherTartu.getAirTemperature());
//        assertEquals(3.3f, weatherTartu.getWindSpeed());
//
//        assertEquals("Light snow shower", weatherParnu.getPhenomenon());
//        assertEquals(-1.7f, weatherParnu.getAirTemperature());
//        assertEquals(3.2f, weatherParnu.getWindSpeed());
//    }
//
//    @Test
//    void calculatorCalculatesCorrectly() {
//        assertEquals(3.5 + 0.5, tallinnScooter.calculate());
//        assertEquals(3.5, tartuCar.calculate());
//        assertEquals(2 + 0.5 + 1, parnuBike.calculate());
//    }
//
//    @Test
//    void calculatorUsesLatestWeatherData() {
//        repository.save(new WeatherData(
//                1678639200,
//                "Tallinn-Harku",
//                26038,
//                "Moderate sleet",
//                -10.1f,
//                30
//        ));
//        repository.save(new WeatherData(
//                1678639200,
//                "Tartu-Tõravere",
//                26242,
//                "Moderate sleet",
//                -10.1f,
//                30
//        ));
//        repository.save(new WeatherData(
//                1678639200,
//                "Pärnu",
//                41803,
//                "Light shower",
//                -10.1f,
//                20
//        ));
//
//        assertEquals(3.5 + 1 + 1, tallinnScooter.calculate());
//        assertEquals(3.5, tartuCar.calculate());
//        assertEquals(2 + 1 + 0.5 + 0.5, parnuBike.calculate());
//    }
//
//    @Test
//    void forbiddenVehicleTypeScooter() {
//        WeatherData weatherData = new WeatherData(
//                1678639200,
//                "Tallinn-Harku",
//                26038,
//                "Thunder",
//                1,
//                1
//        );
//
//        DeliveryFeeCalculator tallinnScooter = new DeliveryFeeCalculator(
//                City.TALLINN, Vehicle.SCOOTER, repository
//        );
//
//        repository.save(weatherData);
//        assertEquals(-1, tallinnScooter.calculate());
//
//        weatherData.setPhenomenon("Glaze");
//        repository.save(weatherData);
//        assertEquals(-1, tallinnScooter.calculate());
//
//        weatherData.setPhenomenon("Hail");
//        repository.save(weatherData);
//        assertEquals(-1, tallinnScooter.calculate());
//
//        weatherData.setPhenomenon("Clear");
//        repository.save(weatherData);
//        assertNotEquals(-1, tallinnScooter.calculate());
//
//        weatherData.setAirTemperature(-30f);
//        weatherData.setWindSpeed(30f);
//        repository.save(weatherData);
//        assertNotEquals(-1, tallinnScooter.calculate());
//    }
//
//    @Test
//    void forbiddenVehicleTypeBike() {
//        WeatherData weatherData = new WeatherData(
//                1678639200,
//                "Tallinn-Harku",
//                26038,
//                "Thunder",
//                1,
//                1
//        );
//
//        DeliveryFeeCalculator tallinnBike = new DeliveryFeeCalculator(
//                City.TALLINN, Vehicle.BIKE, repository
//        );
//
//        repository.save(weatherData);
//        assertEquals(-1, tallinnBike.calculate());
//
//        weatherData.setPhenomenon("Glaze");
//        repository.save(weatherData);
//        System.out.println(weatherData);
//        assertEquals(-1, tallinnBike.calculate());
//
//        weatherData.setPhenomenon("Hail");
//        repository.save(weatherData);
//        assertEquals(-1, tallinnBike.calculate());
//
//        weatherData.setPhenomenon("Clear");
//        repository.save(weatherData);
//        assertNotEquals(-1, tallinnBike.calculate());
//
//        weatherData.setAirTemperature(-30f);
//        weatherData.setWindSpeed(20f);
//        repository.save(weatherData);
//        assertNotEquals(-1, tallinnBike.calculate());
//
//        weatherData.setWindSpeed(20.1f);
//        repository.save(weatherData);
//        assertEquals(-1, tallinnBike.calculate());
//    }
//
//    @Test
//    void carIsNeverForbidden() {
//        WeatherData weatherData = new WeatherData(
//                1678639200,
//                "Tallinn-Harku",
//                26038,
//                "Glaze",
//                -60,
//                100
//        );
//
//        DeliveryFeeCalculator tallinnCar = new DeliveryFeeCalculator(
//                City.TALLINN, Vehicle.CAR, repository
//        );
//
//        repository.save(weatherData);
//        assertNotEquals(-1, tallinnCar.calculate());
//    }
//
//    @Test
//    void controllerReturnsFee() {
//        assertTrue(controller.getDeliveryFee("taLlinN", "sCoOteR").contains(tallinnScooter.calculate() + ""));
//        assertTrue(controller.getDeliveryFee("TARTU", "car").contains(tartuCar.calculate() + ""));
//        assertTrue(controller.getDeliveryFee("pärnu", "BIKE").contains(parnuBike.calculate() + ""));
//    }
//
//    @Test
//    void controllerWrongCity() {
//        assertFalse(controller.getDeliveryFee("taLlin", "sCoOteR").contains(tallinnScooter.calculate() + ""));
//        assertFalse(controller.getDeliveryFee("TART", "car").contains(tartuCar.calculate() + ""));
//        assertFalse(controller.getDeliveryFee("parnu", "BIKE").contains(parnuBike.calculate() + ""));
//    }
//
//    @Test
//    void controllerWrongVehicle() {
//        assertTrue(controller.getDeliveryFee("taLlinn", "sCoOtR").getStatusCode().is4xxClientError());
//        assertTrue(controller.getDeliveryFee("TARTU", "cars").getStatusCode().is4xxClientError());
//        assertTrue(controller.getDeliveryFee("pärnu", "TRIKE").getStatusCode().is4xxClientError());
//    }
//
//    private List<WeatherData> getRepositoryAsList() {
//        List<WeatherData> repositoryContent = new ArrayList<>();
//        repository.findAll().iterator().forEachRemaining(repositoryContent::add);
//        return repositoryContent;
//    }
}
