package com.example.LastProject;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Data {
    private String allergies;
    private String key;
    public Data() {
    }

    public void setAllergies(String allergies, String key) {
        this.key = key;
        this.allergies = allergies;
    }

    public String getAllergies() {

        return allergies;
    }

    public Data(String allergies) {

        this.allergies = allergies;
    }
    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
}
