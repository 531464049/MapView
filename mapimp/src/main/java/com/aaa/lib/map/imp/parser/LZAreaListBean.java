package com.aaa.lib.map.imp.parser;

import java.util.List;

public class LZAreaListBean {

    /**
     * area : [{"areaID":1,"areaName":"","points":[[1.2618945837020874,-0.7044366598129272],[1.2618945837020874,-1.2493104934692383],[-0.7564093470573425,-1.2493104934692383],[-0.7564093470573425,-0.7044366598129272]],"stamp":1608175778},{"areaID":1,"areaName":"","points":[[0.5160291790962219,1.6086252927780151],[1.394279956817627,1.130424976348877],[0.9160796999931335,0.25217416882514954],[0.037828899919986725,0.7303744554519653]],"stamp":1608175778},{"areaID":1,"areaName":"","points":[[-0.1441316157579422,0.5369358658790588],[-0.7317303419113159,-0.2722166180610657],[-1.5408828258514404,0.31538209319114685],[-0.9532840847969055,1.1245346069335938]],"stamp":1608175778}]
     * areaCnt : 3
     * areaType : 1
     */

    private int areaCnt;
    private int areaType;
    private List<AreaBean> area;

    public int getAreaCnt() {
        return areaCnt;
    }

    public void setAreaCnt(int areaCnt) {
        this.areaCnt = areaCnt;
    }

    public int getAreaType() {
        return areaType;
    }

    public void setAreaType(int areaType) {
        this.areaType = areaType;
    }

    public List<AreaBean> getArea() {
        return area;
    }

    public void setArea(List<AreaBean> area) {
        this.area = area;
    }

    public static class AreaBean {
        /**
         * areaID : 1
         * areaName :
         * points : [[1.2618945837020874,-0.7044366598129272],[1.2618945837020874,-1.2493104934692383],[-0.7564093470573425,-1.2493104934692383],[-0.7564093470573425,-0.7044366598129272]]
         * stamp : 1608175778
         */

        private long areaID;
        private String areaName;
        private long stamp;
        private List<List<Double>> points;

        public long getAreaID() {
            return areaID;
        }

        public void setAreaID(long areaID) {
            this.areaID = areaID;
        }

        public String getAreaName() {
            return areaName;
        }

        public void setAreaName(String areaName) {
            this.areaName = areaName;
        }

        public long getStamp() {
            return stamp;
        }

        public void setStamp(long stamp) {
            this.stamp = stamp;
        }

        public List<List<Double>> getPoints() {
            return points;
        }

        public void setPoints(List<List<Double>> points) {
            this.points = points;
        }
    }
}
