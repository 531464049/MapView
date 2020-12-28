package com.aaa.lib.map.layer;

import android.graphics.PointF;

import com.aaa.lib.map.MapView;


/**
 * 标记点
 */
public abstract class MarkerLayer extends BaseLayer {
    protected PointF markerPosition;

    public MarkerLayer(MapView mapView, PointF position) {
        super(mapView,LEVEL_MARKER);
        markerPosition = position;
    }

    public PointF getMarkerPosition() {
        return markerPosition;
    }

    public void setMarkerPosition(PointF markerPosition) {
        this.markerPosition = markerPosition;
    }
}
