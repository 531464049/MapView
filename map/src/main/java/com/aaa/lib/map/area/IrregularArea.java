package com.aaa.lib.map.area;

import android.graphics.PointF;

import java.util.List;

public class IrregularArea implements Area {
    private List<PointF> border;

    public List<PointF> getBorder() {
        return border;
    }

    public void setBorder(List<PointF> border) {
        this.border = border;
    }


}
