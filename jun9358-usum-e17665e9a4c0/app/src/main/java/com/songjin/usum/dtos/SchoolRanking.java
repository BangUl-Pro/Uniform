package com.songjin.usum.dtos;

import com.songjin.usum.entities.SchoolEntity;
import com.songjin.usum.entities.SchoolPointEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class SchoolRanking implements Serializable {
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

    public SchoolRanking(JSONObject object) {
        set(object);
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

    public void set(JSONObject object) {
        try {
            this.point = object.getLong(SchoolPointEntity.PROPERTY_POINT);
            this.school_id = object.getInt(SchoolEntity.PROPERTY_ID);
            this.schoolname = object.getString(SchoolEntity.PROPERTY_SCHOOLNAME);
            this.address = object.getString(SchoolEntity.PROPERTY_ADDRESS);
            this.city = object.getString(SchoolEntity.PROPERTY_CITY);
            this.category = object.getString(SchoolEntity.PROPERTY_CATEGORY);
            this.gu = object.getString(SchoolEntity.PROPERTY_GU);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getBaasioBaseEntity() {
        JSONObject object = new JSONObject();
        try {
            object.put(SchoolPointEntity.PROPERTY_POINT, this.point);
            object.put(SchoolEntity.PROPERTY_ID, this.school_id);
            object.put(SchoolEntity.PROPERTY_SCHOOLNAME, this.schoolname);
            object.put(SchoolEntity.PROPERTY_ADDRESS, this.address);
            object.put(SchoolEntity.PROPERTY_CITY, this.city);
            object.put(SchoolEntity.PROPERTY_CATEGORY, this.category);
            object.put(SchoolEntity.PROPERTY_GU, this.gu);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
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
