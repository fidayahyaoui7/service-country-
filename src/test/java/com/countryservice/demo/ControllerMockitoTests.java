package com.countryservice.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.countryservice.demo.beans.Country;
import com.countryservice.demo.services.CountryService;
import com.countryservice.demo.controllers.CountryController;

@ExtendWith(MockitoExtension.class)
class ControllerMockitoTests {

	@Mock
	private CountryService countryService;

	@InjectMocks
	private CountryController countryController;

	private Country tunisia;
	private Country france;

	@BeforeEach
	void setUp() {
		tunisia = new Country(1, "Tunisia", "Tunis");
		france = new Country(2, "France", "Paris");
	}

	@Test
	void testGetCountries() {
		List<Country> countries = Arrays.asList(tunisia, france);
		when(countryService.getAllCountries()).thenReturn(countries);

		ResponseEntity<List<Country>> response = countryController.getCountries();

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(2, response.getBody().size());
	}

	@Test
	void testGetCountryById() {
		when(countryService.getCountryById(1)).thenReturn(tunisia);

		ResponseEntity<Country> response = countryController.getCountryById(1);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Tunisia", response.getBody().getName());
	}

	@Test
	void testGetCountryByIdNotFound() {
		when(countryService.getCountryById(99)).thenThrow(new NoSuchElementException());

		ResponseEntity<Country> response = countryController.getCountryById(99);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	void testGetCountryByName() {
		when(countryService.getCountryByName("France")).thenReturn(france);

		ResponseEntity<Country> response = countryController.getCountryByName("France");

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Paris", response.getBody().getCapital());
	}

	@Test
	void testGetCountryByNameNotFound() {
		when(countryService.getCountryByName("Atlantis")).thenThrow(new NoSuchElementException());

		ResponseEntity<Country> response = countryController.getCountryByName("Atlantis");

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	void testAddCountry() {
		when(countryService.addCountry(tunisia)).thenReturn(tunisia);

		ResponseEntity<Country> response = countryController.addCountry(tunisia);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals("Tunisia", response.getBody().getName());
	}

	@Test
	void testAddCountryConflict() {
		when(countryService.addCountry(tunisia)).thenThrow(new IllegalArgumentException());

		ResponseEntity<Country> response = countryController.addCountry(tunisia);

		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
	}

	@Test
	void testUpdateCountry() {
		Country newValues = new Country(1, "Tunisie", "Tunis City");
		when(countryService.getCountryById(1)).thenReturn(tunisia);
		when(countryService.updateCountry(any(Country.class))).thenReturn(tunisia);

		ResponseEntity<Country> response = countryController.updateCountry(1, newValues);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Tunisie", response.getBody().getName());
		assertEquals("Tunis City", response.getBody().getCapital());
	}

	@Test
	void testUpdateCountryNotFound() {
		when(countryService.getCountryById(99)).thenThrow(new NoSuchElementException());

		ResponseEntity<Country> response = countryController.updateCountry(99, tunisia);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		verify(countryService, never()).updateCountry(any(Country.class));
	}

	@Test
	void testDeleteCountry() {
		when(countryService.getCountryById(1)).thenReturn(tunisia);

		ResponseEntity<Country> response = countryController.deleteCountry(1);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		verify(countryService).deleteCountry(tunisia);
	}

	@Test
	void testDeleteCountryNotFound() {
		when(countryService.getCountryById(99)).thenThrow(new NoSuchElementException());

		ResponseEntity<Country> response = countryController.deleteCountry(99);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		verify(countryService, never()).deleteCountry(any(Country.class));
	}

}