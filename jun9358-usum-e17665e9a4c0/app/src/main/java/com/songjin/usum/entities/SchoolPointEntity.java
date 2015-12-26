package com.songjin.usum.entities;

import org.json.JSONException;
import org.json.JSONObject;

public class SchoolPointEntity {
    public static final String COLLECTION_NAME = "school_point_2s";
    public static final String PROPERTY_SCHOOL_ID = "school_id";
    public static final String PROPERTY_POINT = "school_point";

    public long point;
    public int school_id;

    public SchoolPointEntity() {

    }

    public SchoolPointEntity(JSONObject object) {
        set(object);
    }

    public void set(JSONObject object) {
        try {
            if (object.getInt(PROPERTY_SCHOOL_ID) != -1) {
                this.school_id = object.getInt(PROPERTY_SCHOOL_ID);
            }
            if (object.getLong(PROPERTY_POINT) != -1) {
                this.point = object.getLong(PROPERTY_POINT);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getBaasioBaseEntity() {
        JSONObject object = new JSONObject();
        try {
            object.put(PROPERTY_SCHOOL_ID, this.school_id);
            object.put(PROPERTY_POINT, this.point);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }
}
