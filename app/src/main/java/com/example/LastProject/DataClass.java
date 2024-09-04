package com.example.LastProject;

public class DataClass {
    private String allergies;
    public DataClass() {
    }

    public void setAllergies(String allergies) {

        this.allergies = allergies;
    }

    public String getAllergies() {

        return allergies;
    }

    public DataClass(String allergies) {

        this.allergies = allergies;
    }
}
