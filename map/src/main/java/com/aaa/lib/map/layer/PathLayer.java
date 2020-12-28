package com.aaa.lib.map.layer;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.view.MotionEvent;


import com.aaa.lib.map.MapView;

import java.util.ArrayList;
import java.util.List;

public abstract class PathLayer extends BaseLayer {

    protected List<PointF> mPathList;
    public PathLayer(MapView mapView) {
        this(mapView, new ArrayList<PointF>());
    }

    public PathLayer(MapView mapView, List<PointF> mPathList) {
        super(mapView, LEVEL_PATH);
        this.mPathList = mPathList;
    }


    @Override
    public boolean onTouch(MotionEvent event) {
        return false;
    }

    @Override
    public void draw(Canvas canvas) {

    }

    @Override
    public void release() {
    }

    public List<PointF> getPathList() {
        return mPathList;
    }

    public void setPathList(List<PointF> pathList) {
        this.mPathList = pathList;
    }
}
