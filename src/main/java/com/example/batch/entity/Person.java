package com.example.batch.entity;

import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@Setter
public class Person {

    final static private Logger log = LoggerFactory.getLogger(Person.class);

    private String lastName;
    private String firstName;
    public Person(){
        log.info("@Data -> Person()");
    }
    public Person(String firstName, String lastName){
        log.info("@Data -> Person(String firstName, String lastName)");
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "firstName: " + firstName + ", lastName: " + lastName;
    }
}
