package com.aaa.lib.map.imp.model;

import java.util.Arrays;

/**
 * 地图数据
 *
 * width 宽
 * height 高
 * resolution 分辨率
 * x_min 世界坐标x偏移
 * y_min 世界坐标y偏移
 * dockerPosX 充电座x
 * dockerPosY 充电座y
 * frameNumber当前地图帧数
 * lz4_len 地图数据长度
 * mapId 地图id
 * pathId 路径ID
 * fullMapData 地图数据
 */
public class LDMapBean {
    public int width;
    public int height;
    public float resolution;
    public float x_min;
    public float y_min;
    public double dockerPosX;
    public double dockerPosY;
    public int frameNumber;
    public int lz4_len;
    public long mapId;
    public String pathId;
    public int[] fullMapData;

    @Override
    public String toString() {
        return "{" +
                "\"width\":" + width +
                ", \"height\":" + height +
                ", \"resolution\":" + resolution +
                ", \"x_min\":" + x_min +
                ", \"y_min\":" + y_min +
                ", \"dockerPosX\":" + dockerPosX +
                ", \"dockerPosY\":" + dockerPosY +
                ", \"frameNumber\":" + frameNumber +
                ", \"lz4_len\":" + lz4_len +
                ", \"mapId\":" + mapId +
                ", \"pathId\":\'" + pathId + "\'" +
                ", \"fullMapData\":" + Arrays.toString(fullMapData) +
                '}';
    }
}
