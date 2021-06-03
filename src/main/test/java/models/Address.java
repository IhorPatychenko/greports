package models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
@AllArgsConstructor
public class Address {

    private final String street;
    private final String city;
    private final String country;
    private final Integer zipCode;

    public String getFullAddress() {
        return String.join(" ", Arrays.asList(this.street, String.valueOf(this.zipCode), this.city, this.country));
    }
}
