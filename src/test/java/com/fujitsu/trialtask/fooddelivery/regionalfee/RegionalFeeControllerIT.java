package com.fujitsu.trialtask.fooddelivery.regionalfee;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fujitsu.trialtask.fooddelivery.enums.City;
import com.fujitsu.trialtask.fooddelivery.enums.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(RegionalFeeController.class)
@AutoConfigureMockMvc
class RegionalFeeControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegionalFeeRepository regionalFeeRepository;

    private RegionalFee sampleRegionalFee;

    @BeforeEach
    void setUp() {
        sampleRegionalFee = new RegionalFee(City.TALLINN, Vehicle.CAR, 10.0f);
        sampleRegionalFee.setId(1L);
    }

    @Test
    void getAllRegionalFees_ShouldReturnListOfRegionalFees() throws Exception {
        // Arrange
        Mockito.when(regionalFeeRepository.findAll()).thenReturn(Collections.singletonList(sampleRegionalFee));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/regional-fees")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].city").value("TALLINN"))
                .andExpect(jsonPath("$[0].vehicle").value("CAR"))
                .andExpect(jsonPath("$[0].fee").value(10.0));
    }

    @Test
    void getRegionalFeeById_ExistingId_ShouldReturnRegionalFee() throws Exception {
        // Arrange
        Mockito.when(regionalFeeRepository.findById(1L)).thenReturn(Optional.of(sampleRegionalFee));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/regional-fees/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.city").value("TALLINN"))
                .andExpect(jsonPath("$.vehicle").value("CAR"))
                .andExpect(jsonPath("$.fee").value(10.0));
    }

    @Test
    void getRegionalFeeById_NonExistingId_ShouldReturnNotFound() throws Exception {
        // Arrange
        Mockito.when(regionalFeeRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/regional-fees/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createRegionalFee_ValidData_ShouldReturnCreatedRegionalFee() throws Exception {
        // Arrange
        RegionalFee newRegionalFee = new RegionalFee(City.TALLINN, Vehicle.CAR, 15.0f);
        Mockito.when(regionalFeeRepository.save(ArgumentMatchers.any(RegionalFee.class))).thenReturn(newRegionalFee);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/regional-fees")
                        .content(objectMapper.writeValueAsString(newRegionalFee))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.city").value("TALLINN"))
                .andExpect(jsonPath("$.vehicle").value("CAR"))
                .andExpect(jsonPath("$.fee").value(15.0));
    }

    @Test
    void createRegionalFee_DuplicateData_ShouldReturnConflict() throws Exception {
        // Arrange
        Mockito.when(regionalFeeRepository.existsByCityAndVehicle(City.TALLINN, Vehicle.CAR)).thenReturn(true);
        RegionalFee newRegionalFee = new RegionalFee(City.TALLINN, Vehicle.CAR, 15.0f);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/regional-fees")
                        .content(objectMapper.writeValueAsString(newRegionalFee))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void updateRegionalFee_ValidIdAndData_ShouldReturnUpdatedRegionalFee() throws Exception {
        // Arrange
        RegionalFee updatedRegionalFee = new RegionalFee(City.TALLINN, Vehicle.CAR, 15.0f);
        updatedRegionalFee.setId(1L);
        Mockito.when(regionalFeeRepository.findById(1L)).thenReturn(Optional.of(sampleRegionalFee));
        Mockito.when(regionalFeeRepository.save(ArgumentMatchers.any(RegionalFee.class))).thenReturn(updatedRegionalFee);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/regional-fees/1")
                        .content(objectMapper.writeValueAsString(updatedRegionalFee))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.city").value("TALLINN"))
                .andExpect(jsonPath("$.vehicle").value("CAR"))
                .andExpect(jsonPath("$.fee").value(15.0));
    }

    @Test
    void updateRegionalFee_InvalidId_ShouldReturnNotFound() throws Exception {
        // Arrange
        RegionalFee updatedRegionalFee = new RegionalFee(City.TALLINN, Vehicle.CAR, 15.0f);
        updatedRegionalFee.setId(1L);
        Mockito.when(regionalFeeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/regional-fees/1")
                        .content(objectMapper.writeValueAsString(updatedRegionalFee))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchRegionalFee_ValidIdAndData_ShouldReturnUpdatedRegionalFee() throws Exception {
        // Arrange
        float newFee = 15.0f;
        var updatedRegionalFee = Map.of("fee", newFee);
        Mockito.when(regionalFeeRepository.findById(1L)).thenReturn(Optional.of(sampleRegionalFee));
        Mockito.when(regionalFeeRepository.save(ArgumentMatchers.any(RegionalFee.class))).thenReturn(sampleRegionalFee);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/regional-fees/1")
                        .content(objectMapper.writeValueAsString(updatedRegionalFee))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.city").value("TALLINN"))
                .andExpect(jsonPath("$.vehicle").value("CAR"))
                .andExpect(jsonPath("$.fee").value(newFee));
    }

    @Test
    void patchRegionalFee_InvalidId_ShouldReturnNotFound() throws Exception {
        // Arrange
        RegionalFee updatedRegionalFee = new RegionalFee(City.TALLINN, Vehicle.CAR, 15.0f);
        updatedRegionalFee.setId(1L);
        Mockito.when(regionalFeeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/regional-fees/1")
                        .content(objectMapper.writeValueAsString(updatedRegionalFee))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteRegionalFee_ExistingId_ShouldReturnNoContent() throws Exception {
        // Arrange
        Mockito.doNothing().when(regionalFeeRepository).deleteById(1L);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/regional-fees/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteRegionalFee_NonExistingId_ShouldReturnNoContent() throws Exception {
        // Arrange
        Mockito.doNothing().when(regionalFeeRepository).deleteById(1L);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/regional-fees/1"))
                .andExpect(status().isNoContent());
    }
}
