package com.pedrocoelho.restwebservices.learningrestwebservices.versioning_tests;

import javax.validation.constraints.Size;

public class Name {
    @Size(min = 2, message = "Name should have at least 2 characters")
    private String firstName;

    @Size(min = 2, message = "Name should have at least 2 characters")
    private String lastName;

    public Name() {
    }

    public Name(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
