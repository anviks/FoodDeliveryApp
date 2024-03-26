package com.fujitsu.trialtask.fooddelivery.helpers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EnumConverterTest {

    @ParameterizedTest
    @ValueSource(strings = {"value1", "VALUE1"})
    void convertStringToEnum_WithOneWord_ShouldAcceptUpperAndLowerCase(String string) {
        assertEquals(convertStringToEnum(string), TestEnum.VALUE1);
    }

    @ParameterizedTest
    @ValueSource(strings = {"VaLuE1", "valUe1", "VALUE1_", "val_ue1"})
    void convertStringToEnum_WithOneWord_ShouldReturnNullForRandomCase(String string) {
        assertNull(convertStringToEnum(string));
    }

    @ParameterizedTest
    @ValueSource(strings = {"value_two", "VALUE_TWO", "valueTwo", "ValueTwo", "value-two"})
    void convertStringToEnum_WithTwoWords_ShouldAcceptCommonCases(String string) {
        assertEquals(convertStringToEnum(string), TestEnum.VALUE_TWO);
    }

    @ParameterizedTest
    @ValueSource(strings = {"value_Three", "VALUE_Four", "valueFive_", "ValueSix_", "value-seven_"})
    void convertStringToEnum_WithTwoWords_ShouldReturnNullForRandomCase(String string) {
        assertNull(convertStringToEnum(string));
    }

    @ParameterizedTest
    @ValueSource(strings = {"value8", "VALUE_NINE", "valueTen", "ValueEleven", "value-twelve"})
    void convertStringToEnum_WithInvalidValue_ShouldReturnNull(String string) {
        assertNull(convertStringToEnum(string));
    }

    private TestEnum convertStringToEnum(String input) {
        return EnumConverter.convertStringToEnum(input, TestEnum.class);
    }

    enum TestEnum {
        VALUE1,
        VALUE_TWO,
    }
}