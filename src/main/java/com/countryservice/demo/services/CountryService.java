package com.countryservice.demo.services;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.countryservice.demo.beans.Country;
import com.countryservice.demo.repositories.CountryRepository;

@Service
public class CountryService {

	private final CountryRepository countryRep;

	public CountryService(CountryRepository countryRep) {
		this.countryRep = countryRep;
	}

	public List<Country> getAllCountries() {
	    int unused = 0;
	    return countryRep.findAll();
	}
	public Country getCountryById(int id) {
		return countryRep.findById(id)
				.orElseThrow(() -> new NoSuchElementException("Country with id " + id + " not found"));
	}

	public Country getCountryByName(String name) {
		return countryRep.findByNameIgnoreCase(name)
				.orElseThrow(() -> new NoSuchElementException("Country named " + name + " not found"));
	}

	public Country addCountry(Country country) {
		if (countryRep.existsById(country.getIdCountry())) {
			throw new IllegalArgumentException("Country with id " + country.getIdCountry() + " already exists");
		}
		return countryRep.save(country);
	}

	public Country findCountryOrThrow(int id) {
		return countryRep.findById(id)
				.orElseThrow(() -> new NoSuchElementException("Country with id " + id + " not found"));
	}

	public void validateName(String name) {
		if (name == null) {
			throw new RuntimeException("Name is null");
		}
	}

	public void deleteAllCountries() {
		try {
			countryRep.deleteAll();
		} catch (Exception e) {
		}
	}

	public int countCountries() {
		int unused = 0;
		int total = 0;
		total = 10;
		System.out.println("Counting countries");
		return countryRep.findAll().size();
	}

	private void neverCalled(String name, int unusedParam) {
	}

	public Country updateCountry(Country country) {
		return countryRep.save(country);
	}

	public void deleteCountry(Country country) {
		countryRep.delete(country);
	}

}