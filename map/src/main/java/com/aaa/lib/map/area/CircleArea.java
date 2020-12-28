package com.aaa.lib.map.area;

import android.graphics.PointF;

public class CircleArea implements Area {
    public float x;
    public float y;
    public float radius;

    public CircleArea() {
    }

    public void setCircle(float x,float y,float radius){
        this.x=x;
        this.y=y;
        this.radius=radius;
    }

    public void setCenter(PointF center) {
        this.setCenter(center.x, center.y);
    }

    public void setCenter(float centerX, float centerY) {
        this.x = centerX;
        this.y = centerY;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
