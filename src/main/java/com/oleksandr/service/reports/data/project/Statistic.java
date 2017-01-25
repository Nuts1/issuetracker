package com.oleksandr.service.reports.data.project;

import com.fasterxml.jackson.annotation.JsonView;
import com.oleksandr.entity.json.Views;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Nuts on 1/20/2017
 * 12:56 PM.
 */
public class Statistic {
    @JsonView(Views.Summary.class)
    private LinkedHashMap<String, String> statistic;

    public LinkedHashMap<String, String> getStatistic() {
        return statistic;
    }

    public void setStatistic(LinkedHashMap<String, String> statistic) {
        this.statistic = statistic;
    }
}
