package com.microservice.limits_service.bean;

public class Limits {
    private int minimum;
    private int maximum;

    //limits的setter getter区别是用来和clients交流
    public Limits(int minimum, int maximum) {
        this.minimum = minimum;
    }

    public int getMinimum() {
        return minimum;
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    public int getMaximum() {
        return maximum;
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }
}