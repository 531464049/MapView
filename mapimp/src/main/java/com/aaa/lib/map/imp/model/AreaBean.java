package com.aaa.lib.map.imp.model;

import java.util.ArrayList;

public class AreaBean {
    /**
     * "areaType"	enum	区域类型
     * eArea	区域
     * eRestrict	禁区
     * eRoom	房间
     * eVirtualWall	虚拟墙
     * eLocations	定点
     * "areaCnt"	int		区域/禁区/房间/虚拟墙个数an
     * "area" : [...]     (array) 区域信息
     * "areaName"	string	区域/禁区/房间/虚拟墙名称
     * "areaID"	int	区域/禁区/房间/虚拟墙ID
     * "stamp"	string	时间戳
     * "points"	int	坐标点个数n
     * "p1-x"	float	第一个点x坐标（x1=min{x1,x2,..xn}）
     * "p1-y"		第一个点y坐标
     * "p2-x"		第二个点x坐标顺时针旋转取点
     * "p2-y"		第二个点y坐标
     * …		…
     * …		…
     * "pn-x"		第n个点x坐标
     * "pn-y"		第n个点y坐标
     */

    protected String areaName;
    protected long areaID;
    protected int type;
    protected long stamp;
    //    private List<PointD> pointList;
    protected ArrayList<double[]> points;

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public long getAreaID() {
        return areaID;
    }

    public void setAreaID(long areaID) {
        this.areaID = areaID;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getStamp() {
        return stamp;
    }

    public void setStamp(long stamp) {
        this.stamp = stamp;
    }

    public ArrayList<double[]> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<double[]> points) {
        this.points = points;
    }
//
//    public void setPointList(ArrayList<PointD> points) {
//        this.pointList = points;
//    }
//
//    public ArrayList<double[]> getPointList() {
//        return points;
//    }


}

