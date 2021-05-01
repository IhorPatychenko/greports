package models;

import java.util.Arrays;

public class Person {
    private String firstName;
    private String lastName;
    private Address address = new Address("Fake street", "New York", "USA", 10001);

    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public Person setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public Person setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public Address getAddress() {
        return address;
    }

    public Person setAddress(Address address) {
        this.address = address;
        return this;
    }

    public String getFullName() {
        return String.join(" ", Arrays.asList(this.firstName, this.lastName));
    }
}
