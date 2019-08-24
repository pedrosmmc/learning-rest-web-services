package com.pedrocoelho.restwebservices.learningrestwebservices.versioning_tests;

import com.pedrocoelho.restwebservices.learningrestwebservices.models.User;

import javax.validation.constraints.NotNull;

public class User_v2 extends User {
    @NotNull
    private Name name;
}
