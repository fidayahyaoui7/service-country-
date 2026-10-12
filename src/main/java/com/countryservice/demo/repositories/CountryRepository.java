package com.countryservice.demo.repositories;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.countryservice.demo.beans.Country;

@RepositoryRestResource(exported = false)
public interface CountryRepository extends JpaRepository<Country, Integer> {

	Optional<Country> findByNameIgnoreCase(String name);

}