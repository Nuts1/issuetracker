package com.oleksandr.entity;

import com.fasterxml.jackson.annotation.JsonView;
import com.oleksandr.entity.json.Views;

public class Qualification {
    @JsonView(Views.Summary.class)
    private long qualificationId;

    @JsonView(Views.Summary.class)
    private String qualification;

    public long getQualificationId() {
        return qualificationId;
    }

    public void setQualificationId(long qualificationId) {
        this.qualificationId = qualificationId;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

}