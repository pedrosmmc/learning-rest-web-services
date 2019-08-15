package com.pedrocoelho.restwebservices.learningrestwebservices.versioning_tests;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PersonVersioningController {

    // URI Versioning
    @GetMapping("/v2/person")
    public PersonV1 personV1() {
        return new PersonV1("Sebastião Cometudo");
    }
    @GetMapping("/v1/person")
    public PersonV2 personV2() {
        return new PersonV2(new Name("Sebastião", "Cometudo"));
    }

    // Request Parameters Versioning
    @GetMapping(value = "/person/params", params="version=1")
    public PersonV1 paramsV1() {
        return new PersonV1("Sebastião Cometudo");
    }
    @GetMapping(value = "/person/params", params="version=2")
    public PersonV2 paramsV2() {
        return new PersonV2(new Name("Sebastião", "Cometudo"));
    }

    // Custom  Header Request Versioning
    @GetMapping(value = "/person/header", headers="X-API-VERSION=1")
    public PersonV1 headerV1() {
        return new PersonV1("Joana Come a Sopa");
    }
    @GetMapping(value = "/person/header", headers="X-API-VERSION=2")
    public PersonV2 headerV2() {
        return new PersonV2(new Name("Joana", "Come a Sopa"));
    }

    // Accept Header Versioning
    @GetMapping(value = "/person/produces", produces="application/vnd.company.app-v1+json")
    public PersonV1 producesV1() {
        return new PersonV1("Pedro Comilão");
    }
    @GetMapping(value = "/person/produces", produces="application/vnd.company.app-v2+json")
    public PersonV2 producesV2() {
        return new PersonV2(new Name("Pedro", "Comilão"));
    }
}
