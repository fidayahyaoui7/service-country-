package com.countryservice.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.countryservice.demo.beans.Country;
import com.countryservice.demo.repositories.CountryRepository;
import com.countryservice.demo.services.CountryService;

@ExtendWith(MockitoExtension.class)
class ServiceMockitoTests {

	@Mock
	private CountryRepository countryRep;

	@InjectMocks
	private CountryService countryService;

	private Country tunisia;
	private Country france;

	@BeforeEach
	void setUp() {
		tunisia = new Country(1, "Tunisia", "Tunis");
		france = new Country(2, "France", "Paris");
	}

	@Test
	void testGetAllCountries() {
		when(countryRep.findAll()).thenReturn(Arrays.asList(tunisia, france));

		List<Country> result = countryService.getAllCountries();

		assertEquals(2, result.size());
		assertEquals("Tunisia", result.get(0).getName());
		verify(countryRep).findAll();
	}

	@Test
	void testGetCountryById() {
		when(countryRep.findById(1)).thenReturn(Optional.of(tunisia));

		Country result = countryService.getCountryById(1);

		assertEquals("Tunisia", result.getName());
		assertEquals("Tunis", result.getCapital());
	}

	@Test
	void testGetCountryByIdNotFound() {
		when(countryRep.findById(99)).thenReturn(Optional.empty());

		assertThrows(NoSuchElementException.class, () -> countryService.getCountryById(99));
	}

	@Test
	void testGetCountryByName() {
		when(countryRep.findByNameIgnoreCase("France")).thenReturn(Optional.of(france));

		Country result = countryService.getCountryByName("France");

		assertEquals(2, result.getIdCountry());
		assertEquals("Paris", result.getCapital());
	}

	@Test
	void testGetCountryByNameNotFound() {
		when(countryRep.findByNameIgnoreCase("Atlantis")).thenReturn(Optional.empty());

		assertThrows(NoSuchElementException.class, () -> countryService.getCountryByName("Atlantis"));
	}

	@Test
	void testAddCountry() {
		when(countryRep.existsById(1)).thenReturn(false);
		when(countryRep.save(tunisia)).thenReturn(tunisia);

		Country result = countryService.addCountry(tunisia);

		assertEquals("Tunisia", result.getName());
		verify(countryRep).save(tunisia);
	}

	@Test
	void testAddCountryAlreadyExists() {
		when(countryRep.existsById(1)).thenReturn(true);

		assertThrows(IllegalArgumentException.class, () -> countryService.addCountry(tunisia));
		verify(countryRep, never()).save(any(Country.class));
	}

	@Test
	void testUpdateCountry() {
		Country updated = new Country(1, "Tunisie", "Tunis");
		when(countryRep.save(updated)).thenReturn(updated);

		Country result = countryService.updateCountry(updated);

		assertEquals("Tunisie", result.getName());
		verify(countryRep).save(updated);
	}

	@Test
	void testDeleteCountry() {
		countryService.deleteCountry(tunisia);

		verify(countryRep).delete(tunisia);
	}

}