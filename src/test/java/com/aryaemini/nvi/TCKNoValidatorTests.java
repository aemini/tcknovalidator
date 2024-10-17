package com.aryaemini.nvi;

import com.aryaemini.nvi.model.Citizen;
import com.aryaemini.nvi.model.Identity;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TCKNoValidatorTests {

    @Test
    void testValidatePersonSuccess() {
        var properties = readTestCaseProperties();
        var tckNoValidator = TCKNoValidator.getInstance();
        var person = Citizen.builder()
                .identityNumber(properties.getProperty("valid.identity-number"))
                .firstName(properties.getProperty("valid.first-name"))
                .lastName(properties.getProperty("valid.last-name"))
                .birthDate(getValidBirthDate().getTime())
                .build();
        boolean result = tckNoValidator.validate(person);
        assertTrue(result, "Person doğrulaması başarılı olmalıdır.");
    }

    @Test
    void testValidatePersonFailure() {
        var properties = readTestCaseProperties();
        var tckNoValidator = TCKNoValidator.getInstance();
        var person = Citizen.builder()
                .identityNumber(properties.getProperty("valid.identity-number"))
                .firstName(properties.getProperty("valid.first-name"))
                .lastName(properties.getProperty("valid.last-name"))
                .build();
        boolean result = tckNoValidator.validate(person);
        assertFalse(result, "Person doğrulaması başarısız olmalıdır.");
    }

    @Test
    void testValidateInvalidTckn1() {
        var properties = readTestCaseProperties();
        var tckNoValidator = TCKNoValidator.getInstance();
        var person = Citizen.builder()
                .identityNumber(properties.getProperty("invalid.identity-number-1"))
                .build();
        boolean result = tckNoValidator.validate(person);
        assertFalse(result, "Person doğrulaması başarısız olmalıdır.");
    }

    @Test
    void testValidateInvalidTckn2() {
        var properties = readTestCaseProperties();
        var tckNoValidator = TCKNoValidator.getInstance();
        var person = Citizen.builder()
                .identityNumber(properties.getProperty("invalid.identity-number-2"))
                .build();
        boolean result = tckNoValidator.validate(person);
        assertFalse(result, "Person doğrulaması başarısız olmalıdır.");
    }

    @Test
    void testValidateWithoutTckn() {
        var properties = readTestCaseProperties();
        var tckNoValidator = TCKNoValidator.getInstance();
        var person = Citizen.builder()
                .firstName(properties.getProperty("valid.first-name"))
                .lastName(properties.getProperty("valid.last-name"))
                .build();
        boolean result = tckNoValidator.validate(person);
        assertFalse(result, "Person doğrulaması başarısız olmalıdır.");
    }

    @Test
    void testValidateWithShortTckn() {
        var properties = readTestCaseProperties();
        var tckNoValidator = TCKNoValidator.getInstance();
        var person = Citizen.builder()
                .identityNumber(properties.getProperty("invalid.short-identity-number"))
                .firstName(properties.getProperty("valid.first-name"))
                .lastName(properties.getProperty("valid.last-name"))
                .build();
        boolean result = tckNoValidator.validate(person);
        assertFalse(result, "Person doğrulaması başarısız olmalıdır.");
    }

    @Test
    void testValidateIdentityCardSuccess() {
        var properties = readTestCaseProperties();
        var tckNoValidator = TCKNoValidator.getInstance();
        var identityCard = Identity.builder()
                .identityNumber(properties.getProperty("valid.identity-number"))
                .firstName(properties.getProperty("valid.first-name"))
                .lastName(properties.getProperty("valid.last-name"))
                .birthDate(getValidBirthDate().getTime())
                .tckCardSerialNumber(properties.getProperty("valid.tck-serial-number"))
                .build();
        boolean result = tckNoValidator.validate(identityCard);
        assertTrue(result, "Kimlik kartı doğrulaması başarılı olmalıdır.");
    }

    @Test
    void testValidateIdentityCardWithoutTckSerial() {
        var properties = readTestCaseProperties();
        var tckNoValidator = TCKNoValidator.getInstance();
        var identityCard = Identity.builder()
                .identityNumber(properties.getProperty("valid.identity-number"))
                .firstName(properties.getProperty("valid.first-name"))
                .lastName(properties.getProperty("valid.last-name"))
                .birthDate(getValidBirthDate().getTime())
                .build();
        boolean result = tckNoValidator.validate(identityCard);
        assertTrue(result, "Kimlik kartı doğrulaması başarılı olmalıdır.");
    }

    @Test
    void testValidateIdentityCardOnlyBirthYear() {
        var properties = readTestCaseProperties();
        var tckNoValidator = TCKNoValidator.getInstance();
        var identityCard = Identity.builder()
                .identityNumber(properties.getProperty("valid.identity-number"))
                .firstName(properties.getProperty("valid.first-name"))
                .lastName(properties.getProperty("valid.last-name"))
                .birthYear(Integer.parseInt(properties.getProperty("valid.birth-year")))
                .build();
        boolean result = tckNoValidator.validate(identityCard);
        assertFalse(result, "Kimlik kartı doğrulaması başarısız olmalıdır.");
    }

    @Test
    void testValidateOldIdentityCard() {
        var properties = readTestCaseProperties();
        var tckNoValidator = TCKNoValidator.getInstance();
        var identityCard = Identity.builder()
                .identityNumber(properties.getProperty("valid.identity-number"))
                .firstName(properties.getProperty("valid.first-name"))
                .lastName(properties.getProperty("valid.last-name"))
                .birthYear(Integer.parseInt(properties.getProperty("valid.birth-year")))
                .idCardSerial("Z03")
                .idCardNumber(0)
                .build();
        boolean result = tckNoValidator.validate(identityCard);
        assertFalse(result, "Kimlik kartı doğrulaması başarısız olmalıdır.");
    }

    @Test
    void testValidateIdentityWithShortTckn() {
        var properties = readTestCaseProperties();
        var tckNoValidator = TCKNoValidator.getInstance();
        var identityCard = Identity.builder()
                .identityNumber(properties.getProperty("invalid.short-identity-number"))
                .firstName(properties.getProperty("valid.first-name"))
                .lastName(properties.getProperty("valid.last-name"))
                .build();
        boolean result = tckNoValidator.validate(identityCard);
        assertFalse(result, "Kimlik kartı doğrulaması başarılı olmalıdır.");
    }

    @Test
    void testValidateIdentityCardWithoutSurname() {
        var properties = readTestCaseProperties();
        var tckNoValidator = TCKNoValidator.getInstance();
        var identityCard = Identity.builder()
                .identityNumber(properties.getProperty("valid.identity-number"))
                .firstName(properties.getProperty("valid.first-name"))
                .build();
        boolean result = tckNoValidator.validate(identityCard);
        assertFalse(result, "Kimlik kartı doğrulaması başarılı olmalıdır.");
    }

    private Calendar getValidBirthDate() {
        var properties = readTestCaseProperties();
        var calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(properties.getProperty("valid.birth-year")));
        calendar.set(Calendar.MONTH, Integer.parseInt(properties.getProperty("valid.birth-month")));
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(properties.getProperty("valid.birth-day")));
        return calendar;
    }

    private Properties readTestCaseProperties() {
        var properties = new Properties();
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("test-case.properties")) {
            properties.load(input);
            return properties;
        } catch (IOException ignore) {
        }
        return properties;
    }

}
