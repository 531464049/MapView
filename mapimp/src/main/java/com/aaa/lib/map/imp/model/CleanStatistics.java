package com.aaa.lib.map.imp.model;

public class CleanStatistics {
    /**
     * "cleanTime"	int	minute	当前清扫时间
     * "historyTime"			历史清扫时间
     * "cleanArea"	float	㎡	当前清扫面积
     * "historyArea"			历史清扫面积
     * "cleanCount"	int		历史清扫次数
     */
    private int cleanTime;
    private int historyTime;
    private double cleanArea;
    private double historyArea;
    private int cleanCount;

    public int getCleanTime() {
        return cleanTime;
    }

    public void setCleanTime(int cleanTime) {
        this.cleanTime = cleanTime;
    }

    public int getHistoryTime() {
        return historyTime;
    }

    public void setHistoryTime(int historyTime) {
        this.historyTime = historyTime;
    }

    public double getCleanArea() {
        return cleanArea;
    }

    public void setCleanArea(double cleanArea) {
        this.cleanArea = cleanArea;
    }

    public double getHistoryArea() {
        return historyArea;
    }

    public void setHistoryArea(double historyArea) {
        this.historyArea = historyArea;
    }

    public int getCleanCount() {
        return cleanCount;
    }

    public void setCleanCount(int cleanCount) {
        this.cleanCount = cleanCount;
    }


}
