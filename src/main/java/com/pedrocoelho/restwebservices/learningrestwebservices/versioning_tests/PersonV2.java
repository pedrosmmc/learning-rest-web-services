package com.pedrocoelho.restwebservices.learningrestwebservices.versioning_tests;

public class PersonV2 {
    private Name name;

    public PersonV2() {
    }

    public PersonV2(Name name) {
        this.name = name;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }
}

