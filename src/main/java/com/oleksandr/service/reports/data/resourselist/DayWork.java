package com.oleksandr.service.reports.data.resourselist;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import com.oleksandr.entity.json.Views;

import java.util.Date;

public class DayWork {
    @JsonView(Views.Summary.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm", timezone="EET")
    Date date;
    @JsonView(Views.Summary.class)
    private double works;

    @JsonView(Views.Summary.class)
    private boolean isDelay;

    public boolean isDelay() {
        return isDelay;
    }

    public void setDelay(boolean delay) {
        isDelay = delay;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getWorks() {
        return works;
    }

    public void setWorks(double works) {
        this.works = works;
    }

    public DayWork(Date date, double works) {
        this.date = date;
        this.works = works;
    }


    public DayWork() {

    }
}
