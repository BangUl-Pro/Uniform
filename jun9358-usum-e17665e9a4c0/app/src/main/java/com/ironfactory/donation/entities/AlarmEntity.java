package com.ironfactory.donation.entities;


public class AlarmEntity {
    public String message;
    public Long timestamp;

    public AlarmEntity() {

    }

    public AlarmEntity(String message, Long timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    public AlarmEntity(String message) {
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
}
