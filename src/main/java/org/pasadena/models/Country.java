package org.pasadena.models;

import java.util.ArrayList;
import java.util.List;

public class Country {

	private final String name;

	private final String abbreviation;

	private List<City> cities;

	public Country(final String name, final String abbreviation) {
		this.name = name;
		this.abbreviation = abbreviation;
		this.cities = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public List<City> getCities() {
		return cities;
	}

	public void setCities(List<City> cities) {
		this.cities = cities;
	}
}
