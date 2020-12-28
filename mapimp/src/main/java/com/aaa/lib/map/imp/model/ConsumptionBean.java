package com.aaa.lib.map.imp.model;

public class ConsumptionBean {
    /**
     * "mainBrush"	        主刷使用时间 int
     * "mainBrushLife"		主刷寿命
     * "sideBrush"			边刷使用时间
     * "sideBrushLife"		边刷寿命
     * "strainer"			滤网使用时间
     * "strainerLife"		滤网寿命
     * "sensor"			    传感器使用时间
     * "sensorLife"			传感器寿命
     */


    private int mainBrush;
    private int mainBrushLife;
    private int sideBrush;
    private int sideBrushLife;
    private int strainer;
    private int strainerLife;
    private int sensor;
    private int sensorLife;

    public int getMainBrush() {
        return mainBrush;
    }

    public void setMainBrush(int mainBrush) {
        this.mainBrush = mainBrush;
    }

    public int getMainBrushLife() {
        return mainBrushLife;
    }

    public void setMainBrushLife(int mainBrushLife) {
        this.mainBrushLife = mainBrushLife;
    }

    public int getSideBrush() {
        return sideBrush;
    }

    public void setSideBrush(int sideBrush) {
        this.sideBrush = sideBrush;
    }

    public int getSideBrushLife() {
        return sideBrushLife;
    }

    public void setSideBrushLife(int sideBrushLife) {
        this.sideBrushLife = sideBrushLife;
    }

    public int getStrainer() {
        return strainer;
    }

    public void setStrainer(int strainer) {
        this.strainer = strainer;
    }

    public int getStrainerLife() {
        return strainerLife;
    }

    public void setStrainerLife(int strainerLife) {
        this.strainerLife = strainerLife;
    }

    public int getSensor() {
        return sensor;
    }

    public void setSensor(int sensor) {
        this.sensor = sensor;
    }

    public int getSensorLife() {
        return sensorLife;
    }

    public void setSensorLife(int sensorLife) {
        this.sensorLife = sensorLife;
    }
}
