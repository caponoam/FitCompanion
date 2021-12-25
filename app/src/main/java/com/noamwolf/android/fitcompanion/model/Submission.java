package com.noamwolf.android.fitcompanion.model;

public class Submission {

    enum Sub {
        REAR_NAKED_CHOKE("RNC"),
        COLLAR_CHOKE("CC"),
        EIZIKEIL_CHOKE("EZ"),
        BASEBALL_CHOKE("BC"),
        ARM_BAR("AB"),
        KNEE_BAR("KB"),
        STRAIGHT_ANKLE("SA"),
        HEELHOOK("HH"),
        TOE_HOLD("TH"),
        AMERICANA("A"),
        KIMURA("K"),
        OMOPLATTA("O"),
        WRISTLOCK("WL"),
        BUGGY_CHOKE("BGC");

        private String key;
        Sub(String key) {
            this.key = key;
        }
    }
}
