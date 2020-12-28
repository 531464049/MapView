package com.aaa.lib.map.imp.parser;

import com.aaa.lib.map.imp.model.AreaBean;

import java.util.List;

public class LZCleanAreaBean {
    /*
    eAreaClean	区域清扫
    eLocationClean	定点清扫
    eRoomClean	按房间清扫
     */
    public static final int TYPE_AREA = 0;
    public static final int TYPE_LOCATION = 0;
    public static final int TYPE_ROOM = 0;

    private int cleanType;
    /**
     * 划区和选区信息
     */
    private List<CleanAreaInfo> area;
//    private List<point> locationPoint;


    public int getCleanType() {
        return cleanType;
    }

    public void setCleanType(int cleanType) {
        this.cleanType = cleanType;
    }

    public List<CleanAreaInfo> getArea() {
        return area;
    }

    public void setArea(List<CleanAreaInfo> area) {
        this.area = area;
    }

    public static class CleanAreaInfo extends AreaBean {

        public CleanAreaInfo(AreaBean areaBean) {
            this.areaID=areaBean.getAreaID();
            this.areaName=areaBean.getAreaName();
            this.stamp=areaBean.getStamp();
            this.type=areaBean.getType();
            this.points=areaBean.getPoints();
        }

        private int cnt;
        private int funSuction;
        private int waterSpeed;

        public int getCnt() {
            return cnt;
        }

        public void setCnt(int cnt) {
            this.cnt = cnt;
        }

        public int getFunSuction() {
            return funSuction;
        }

        public void setFunSuction(int funSuction) {
            this.funSuction = funSuction;
        }

        public int getWaterSpeed() {
            return waterSpeed;
        }

        public void setWaterSpeed(int waterSpeed) {
            this.waterSpeed = waterSpeed;
        }
    }
}

