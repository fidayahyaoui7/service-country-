package com.countryservice.demo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.countryservice.demo.beans.Country;
import com.countryservice.demo.services.CountryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.countryservice.demo.controllers.CountryController;


@ExtendWith(MockitoExtension.class)
class ControllerMockMvcTests {

	@Mock
	private CountryService countryService;

	@InjectMocks
	private CountryController countryController;

	private MockMvc mockMvc;
	private final ObjectMapper objectMapper = new ObjectMapper();

	private Country tunisia;
	private Country france;

	@BeforeEach
	void setUp() {
		// MockMvc autonome : pas de serveur, pas de contexte Spring complet
		mockMvc = MockMvcBuilders.standaloneSetup(countryController).build();
		tunisia = new Country(1, "Tunisia", "Tunis");
		france = new Country(2, "France", "Paris");
	}

	@Test
	void testGetCountries() throws Exception {
		when(countryService.getAllCountries()).thenReturn(Arrays.asList(tunisia, france));

		mockMvc.perform(get("/getcountries"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].name").value("Tunisia"))
				.andExpect(jsonPath("$[1].name").value("France"));
	}

	@Test
	void testGetCountryById() throws Exception {
		when(countryService.getCountryById(1)).thenReturn(tunisia);

		mockMvc.perform(get("/getcountries/{id}", 1))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.idCountry").value(1))
				.andExpect(jsonPath("$.name").value("Tunisia"))
				.andExpect(jsonPath("$.capital").value("Tunis"));
	}

	@Test
	void testGetCountryByIdNotFound() throws Exception {
		when(countryService.getCountryById(99)).thenThrow(new NoSuchElementException());

		mockMvc.perform(get("/getcountries/{id}", 99))
				.andExpect(status().isNotFound());
	}

	@Test
	void testGetCountryByName() throws Exception {
		when(countryService.getCountryByName("France")).thenReturn(france);

		mockMvc.perform(get("/getcountries/countryname").param("name", "France"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.capital").value("Paris"));
	}

	@Test
	void testAddCountry() throws Exception {
		when(countryService.addCountry(any(Country.class))).thenReturn(tunisia);

		mockMvc.perform(post("/addcountry")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(tunisia)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name").value("Tunisia"));
	}

	@Test
	void testAddCountryConflict() throws Exception {
		when(countryService.addCountry(any(Country.class))).thenThrow(new IllegalArgumentException());

		mockMvc.perform(post("/addcountry")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(tunisia)))
				.andExpect(status().isConflict());
	}

	@Test
	void testUpdateCountry() throws Exception {
		Country newValues = new Country(1, "Tunisie", "Tunis City");
		when(countryService.getCountryById(1)).thenReturn(tunisia);
		when(countryService.updateCountry(any(Country.class))).thenReturn(tunisia);

		mockMvc.perform(put("/updatecountry/{id}", 1)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newValues)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Tunisie"))
				.andExpect(jsonPath("$.capital").value("Tunis City"));
	}

	@Test
	void testUpdateCountryNotFound() throws Exception {
		when(countryService.getCountryById(99)).thenThrow(new NoSuchElementException());

		mockMvc.perform(put("/updatecountry/{id}", 99)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(tunisia)))
				.andExpect(status().isNotFound());
	}

	@Test
	void testDeleteCountry() throws Exception {
		when(countryService.getCountryById(1)).thenReturn(tunisia);

		mockMvc.perform(delete("/deletecountry/{id}", 1))
				.andExpect(status().isOk());

		verify(countryService).deleteCountry(tunisia);
	}

	@Test
	void testDeleteCountryNotFound() throws Exception {
		when(countryService.getCountryById(99)).thenThrow(new NoSuchElementException());

		mockMvc.perform(delete("/deletecountry/{id}", 99))
				.andExpect(status().isNotFound());
	}

}