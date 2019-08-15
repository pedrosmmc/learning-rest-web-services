package com.pedrocoelho.restwebservices.learningrestwebservices.versioning_tests;

import com.pedrocoelho.restwebservices.learningrestwebservices.user.User;

import javax.validation.constraints.NotNull;

public class User_v2 extends User {
    @NotNull
    private Name name;
}
