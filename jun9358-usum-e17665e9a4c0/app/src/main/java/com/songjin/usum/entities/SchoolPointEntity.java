package com.songjin.usum.entities;

import com.kth.baasio.entity.BaasioBaseEntity;

public class SchoolPointEntity {
    public static final String COLLECTION_NAME = "school_point_2s";
    public static final String PROPERTY_SCHOOL_ID = "school_id";
    public static final String PROPERTY_POINT = "point";

    public long point;
    public int school_id;

    public SchoolPointEntity() {

    }

    public SchoolPointEntity(BaasioBaseEntity entity) {
        set(entity);
    }

    public void set(BaasioBaseEntity entity) {
        if (entity.getProperty(PROPERTY_SCHOOL_ID) != null) {
            this.school_id = entity.getProperty(PROPERTY_SCHOOL_ID).asInt();
        }
        if (entity.getProperty(PROPERTY_POINT) != null) {
            this.point = entity.getProperty(PROPERTY_POINT).asLong();
        }
    }

    public BaasioBaseEntity getBaasioBaseEntity() {
        BaasioBaseEntity entity = new BaasioBaseEntity();
        entity.setProperty(PROPERTY_SCHOOL_ID, this.school_id);
        entity.setProperty(PROPERTY_POINT, this.point);

        return entity;
    }
}
