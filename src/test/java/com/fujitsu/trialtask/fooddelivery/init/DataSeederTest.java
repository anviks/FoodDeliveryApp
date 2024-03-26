package com.fujitsu.trialtask.fooddelivery.init;

import com.fujitsu.trialtask.fooddelivery.regionalfee.RegionalFee;
import com.fujitsu.trialtask.fooddelivery.regionalfee.RegionalFeeRepository;
import com.fujitsu.trialtask.fooddelivery.weatherfee.WeatherFee;
import com.fujitsu.trialtask.fooddelivery.weatherfee.WeatherFeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataSeederTest {

    private static final int EXPECTED_REGIONAL_FEES_COUNT = 9;
    private static final int EXPECTED_WEATHER_FEES_COUNT = 20;

    @Mock
    private RegionalFeeRepository regionalFeeRepository;

    @Mock
    private WeatherFeeRepository weatherFeeRepository;

    @InjectMocks
    private DataSeeder dataSeeder;

    @Test
    void run_WithNoFees_ShouldSeedDataAllAtOnce() {
        // Arrange
        when(regionalFeeRepository.count()).thenReturn(0L);
        when(weatherFeeRepository.count()).thenReturn(0L);
        ArgumentCaptor<List<RegionalFee>> regionalFeeCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<WeatherFee>> weatherFeeCaptor = ArgumentCaptor.forClass(List.class);

        // Act
        dataSeeder.run();

        // Assert
        verify(regionalFeeRepository, times(1)).saveAll(any());
        verify(weatherFeeRepository, times(1)).saveAll(any());
        verify(regionalFeeRepository, never()).save(any());
        verify(weatherFeeRepository, never()).save(any());

        verify(regionalFeeRepository).saveAll(regionalFeeCaptor.capture());
        verify(weatherFeeRepository).saveAll(weatherFeeCaptor.capture());
        assertEquals(EXPECTED_REGIONAL_FEES_COUNT, regionalFeeCaptor.getValue().size());
        assertEquals(EXPECTED_WEATHER_FEES_COUNT, weatherFeeCaptor.getValue().size());
    }

    @ParameterizedTest
    @CsvSource({"0, 1", "1, 0", "1, 1"})
    void run_WithFees_ShouldNotSeedData(long regionalFeeCount, long weatherFeeCount) {
        // Arrange
        lenient().when(regionalFeeRepository.count()).thenReturn(regionalFeeCount);
        lenient().when(weatherFeeRepository.count()).thenReturn(weatherFeeCount);

        // Act
        dataSeeder.run();

        // Assert
        verify(regionalFeeRepository, never()).saveAll(any());
        verify(weatherFeeRepository, never()).saveAll(any());
        verify(regionalFeeRepository, never()).save(any());
        verify(weatherFeeRepository, never()).save(any());
    }
}