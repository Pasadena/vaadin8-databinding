package org.pasadena.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.pasadena.models.City;
import org.pasadena.models.Country;

public class Backend {

	private List<Country> availableCountries;


	public List<Country> getAvailableCountries() {
		if(this.availableCountries == null) {
			this.availableCountries = new CountryListBuilder()
					.withCountry("Finland", "FI", "Turku", "Salo", "Halikko", "Helsinki", "Tampere")
					.withCountry("Sweden", "SE", "Stockholm", "Malmo", "Gothenburg", "Lulaja")
					.withCountry("Norway", "NO", "Oslo", "Trondheim", "Stavanger", "Lillehammer")
					.build();
		}
		return availableCountries;
	}

	public List<City> getCitiesForCountry(final Country country) {
		if(country == null) return new ArrayList<>();
		return getAvailableCountries()
				.stream()
				.filter(item -> country.getName().equals(item.getName()))
				.flatMap(item -> item.getCities().stream())
				.collect(Collectors.toList());
	}

	private class CountryListBuilder {

		private List<Country> result;

		CountryListBuilder() {
			this.result = new ArrayList<>();
		}

		CountryListBuilder withCountry(final String name, final String abbreviation, String... cities) {
			Country country = new Country(name, abbreviation);
			for(String cityName: cities) {
				City city = new City();
				city.setName(cityName);
				city.setCountry(country);
				country.getCities().add(city);
			}
			this.result.add(country);
			return this;
		}

		List<Country> build() {
			return this.result;
		}
	}
}
