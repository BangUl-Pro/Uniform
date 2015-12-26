package com.songjin.usum.entities;


public class AlarmEntity {
    public String message;
    public Long timestamp;
    public int type;

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

    public AlarmEntity(String message, int type) {
        this.message = message;
        this.type = type;
    }
}
