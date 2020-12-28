package com.aaa.lib.map.layer;

import com.aaa.lib.map.MapView;
import com.aaa.lib.map.area.Area;

public abstract class AreaLayer<T extends Area> extends BaseLayer {
    private static final String TAG = AreaLayer.class.getSimpleName();

    protected double areaSize; //面积
    protected T area;    //区域

    public AreaLayer(MapView mapView) {
        super(mapView, LEVEL_AREA);
    }

    public double getAreaSize() {
        return areaSize;
    }

    public void setAreaSize(double areaSize) {
        this.areaSize = areaSize;
    }

    public T getArea() {
        return area;
    }

    protected void setArea(T area) {
        this.area = area;
    }


}
