package models;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
public class Person {

    private String firstName;
    private String lastName;
    private Address address = new Address("Fake street", "New York", "USA", 10001);

    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFullName() {
        return String.join(" ", Arrays.asList(this.firstName, this.lastName));
    }
}
