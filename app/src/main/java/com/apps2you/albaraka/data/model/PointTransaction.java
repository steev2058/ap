package com.apps2you.albaraka.data.model;

import com.apps2you.albaraka.ui.base.BaseViewModel;

public class PointTransaction  {
    public String description;
    public String points;
    public String date;

    public PointTransaction(String description, String points, String date) {
        this.description = description;
        this.points = points;
        this.date = date;
    }
}
