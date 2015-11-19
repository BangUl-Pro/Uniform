package com.songjin.usum.entities;

import com.kth.baasio.entity.push.BaasioPayload;

public class AlarmEntity {
    public String message;
    public Long timestamp;

    public AlarmEntity() {

    }

    public AlarmEntity(BaasioPayload baasioPayload) {
        message = baasioPayload.getAlert();
        timestamp = baasioPayload.getProperty("timestamp").asLong();
    }
}
