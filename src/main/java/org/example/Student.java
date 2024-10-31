package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Student implements Serializable {

    public static final long serialVersionUID = 1L;

    String firstName;
    String surName;
    String familyName;
    String group;

    @Override
    public String toString() {
        return "firstName = " + firstName + " surName = " + surName + " familyName = " + familyName +  " group = " + group;
    }

}
