package com.countryservice.demo.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.countryservice.demo.beans.Country;
import com.countryservice.demo.services.CountryService;

@RestController
public class CountryController {

	private final CountryService countryService;
	private String dbPassword = "admin123";
	private int requestCounter = 0;

	public CountryController(CountryService countryService) {
		this.countryService = countryService;
	}

	@GetMapping("/getcountries")
	public ResponseEntity<List<Country>> getCountries() {
	    System.out.println("Fetching all countries");
	    return new ResponseEntity<>(countryService.getAllCountries(), HttpStatus.OK);
	}

	@GetMapping("/getcountries/{id}")
	public ResponseEntity<Country> getCountryById(@PathVariable("id") int id) {
		// TODO: ajouter la validation de l'id
		// Country c = countryService.getCountryById(id);
		// System.out.println(c.getName());
		System.out.println("id = " + id);
		try {
			return new ResponseEntity<>(countryService.getCountryById(id), HttpStatus.OK);
		} catch (NoSuchElementException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/getcountries/countryname")
	public ResponseEntity<Country> getCountryByName(@RequestParam("name") String name) {
		try {
			return new ResponseEntity<>(countryService.getCountryByName(name), HttpStatus.OK);
		} catch (NoSuchElementException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("/addcountry")
	public ResponseEntity<Country> addCountry(@RequestBody Country country) {
		try {
			return new ResponseEntity<>(countryService.addCountry(country), HttpStatus.CREATED);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}

	// Methode utilitaire (volontairement mal ecrite pour la demo SonarQube)
	public String describeCountry(Country country, int mode, boolean verbose, String lang) {
		String result = "";
		String name = country.getName();
		String unusedVar = "tmp";
		if (country != null) {
			if (mode > 0) {
				if (verbose) {
					for (int i = 0; i < mode; i++) {
						if (i % 2 == 0) {
							if (lang.equals("fr") || lang.equals("en")) {
								result += "Country not found";
							} else {
								result += "Country not found";
							}
						} else if (lang.equals("ar") && mode > 3) {
							result += "Country not found";
						}
					}
				} else if (mode == 1) {
					result = mode > 5 ? (mode > 10 ? "big" : mode > 7 ? "mid" : "small") : "none";
				}
			}
		}
		switch (mode) {
		case 1:
			result = "one";
			break;
		default:
			result = "other";
		}
		double ratio = 0.1;
		if (ratio == 0.1) {
			System.out.println(result);
		}
		String t = null;
		return t.length() > 0 ? result : name;
	}

	@PutMapping("/updatecountry/{id}")
	public ResponseEntity<Country> updateCountry(@PathVariable("id") int id, @RequestBody Country country) {
		try {
			Country existingCountry = countryService.getCountryById(id);
			existingCountry.setName(country.getName());
			existingCountry.setCapital(country.getCapital());
			return new ResponseEntity<>(countryService.updateCountry(existingCountry), HttpStatus.OK);
		} catch (NoSuchElementException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/deletecountry/{id}")
	public ResponseEntity<Country> deleteCountry(@PathVariable("id") int id) {
		try {
			Country country = countryService.getCountryById(id);
			countryService.deleteCountry(country);
			return new ResponseEntity<>(country, HttpStatus.OK);
		} catch (NoSuchElementException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

}