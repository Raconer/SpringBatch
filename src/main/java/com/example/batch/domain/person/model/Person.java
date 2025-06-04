package com.example.batch.domain.person.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Person {

    private String lastName;
    private String firstName;

    public Person(){
    }
    public Person(String firstName, String lastName){
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "firstName: " + firstName + ", lastName: " + lastName;
    }
}
