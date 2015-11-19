package com.songjin.usum.dtos;

import com.kth.baasio.entity.BaasioBaseEntity;
import com.songjin.usum.entities.SchoolEntity;
import com.songjin.usum.entities.SchoolPointEntity;

public class SchoolRanking {
    public long point;
    public int school_id;
    public String schoolname;
    public String address;
    public String city;
    public String category;
    public String gu;

    public SchoolRanking(SchoolPointEntity schoolPointEntity, SchoolEntity schoolEntity) {
        set(schoolPointEntity, schoolEntity);
    }

    public SchoolRanking(BaasioBaseEntity entity) {
        set(entity);
    }

    public void set(SchoolPointEntity schoolPointEntity, SchoolEntity schoolEntity) {
        this.point = schoolPointEntity.point;
        this.school_id = schoolEntity.id;
        this.schoolname = schoolEntity.schoolname;
        this.address = schoolEntity.address;
        this.city = schoolEntity.city;
        this.category = schoolEntity.category;
        this.gu = schoolEntity.gu;
    }

    public void set(BaasioBaseEntity entity) {
        this.point = entity.getProperty(SchoolPointEntity.PROPERTY_POINT).asLong();
        this.school_id = entity.getProperty(SchoolEntity.PROPERTY_ID).asInt();
        this.schoolname = entity.getProperty(SchoolEntity.PROPERTY_SCHOOLNAME).asText();
        this.address = entity.getProperty(SchoolEntity.PROPERTY_ADDRESS).asText();
        this.city = entity.getProperty(SchoolEntity.PROPERTY_CITY).asText();
        this.category = entity.getProperty(SchoolEntity.PROPERTY_CATEGORY).asText();
        this.gu = entity.getProperty(SchoolEntity.PROPERTY_GU).asText();
    }

    public BaasioBaseEntity getBaasioBaseEntity() {
        BaasioBaseEntity entity = new BaasioBaseEntity();
        entity.setProperty(SchoolPointEntity.PROPERTY_POINT, this.point);
        entity.setProperty(SchoolEntity.PROPERTY_ID, this.school_id);
        entity.setProperty(SchoolEntity.PROPERTY_SCHOOLNAME, this.schoolname);
        entity.setProperty(SchoolEntity.PROPERTY_ADDRESS, this.address);
        entity.setProperty(SchoolEntity.PROPERTY_CITY, this.city);
        entity.setProperty(SchoolEntity.PROPERTY_CATEGORY, this.category);
        entity.setProperty(SchoolEntity.PROPERTY_GU, this.gu);
        return entity;
    }

    public SchoolEntity getSchoolEntity() {
        SchoolEntity schoolEntity = new SchoolEntity();
        schoolEntity.id = this.school_id;
        schoolEntity.schoolname = this.schoolname;
        schoolEntity.address = this.address;
        schoolEntity.city = this.city;
        schoolEntity.category = this.category;
        schoolEntity.gu = this.gu;

        return schoolEntity;
    }
}
