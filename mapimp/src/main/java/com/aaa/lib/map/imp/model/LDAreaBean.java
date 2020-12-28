package com.aaa.lib.map.imp.model;

import java.util.ArrayList;

public class LDAreaBean {
    private ArrayList<AreaBean> areaList;

    public LDAreaBean() {
        areaList = new ArrayList<>();
    }

    public ArrayList<AreaBean> getAreaList() {
        return areaList;
    }

    public void setAreaList(ArrayList<AreaBean> areaList) {
        this.areaList = areaList;
    }
}
