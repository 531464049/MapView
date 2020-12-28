package com.aaa.lib.map.area;

import android.graphics.PointF;

public class LineArea implements Area {
    public PointF p1, p2;

    public LineArea() {
        p1 = new PointF();
        p2 = new PointF();
    }

    public void setLine(PointF p1, PointF p2) {
        this.setLine(p1.x, p1.y, p2.x, p2.y);
    }

    public void setP1(float p1x, float p1y) {
        this.p1.x = p1x;
        this.p1.y = p1y;
    }

    public void setP2(float p2x, float p2y) {
        this.p2.x = p2x;
        this.p2.y = p2y;
    }

    public void setLine(float p1x, float p1y, float p2x, float p2y) {
        p1.x = p1x;
        p1.y = p1y;
        p2.x = p2x;
        p2.y = p2y;
    }
}
