package models;

import java.util.Arrays;

public class Address {
    private final String street;
    private final String city;
    private final String country;
    private final Integer zipCode;

    public Address(String street, String city, String country, Integer zipCode) {
        this.street = street;
        this.city = city;
        this.country = country;
        this.zipCode = zipCode;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public Integer getZipCode() {
        return zipCode;
    }

    public String getFullAddress() {
        return String.join(" ", Arrays.asList(this.street, String.valueOf(this.zipCode), this.city, this.country));
    }
}
