package com.aaa.lib.map.imp.layer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.aaa.lib.map.MapUtils;
import com.aaa.lib.map.MapView;
import com.aaa.lib.map.layer.MarkerLayer;

/**
 * 标记点
 */
public class YXImageMarkerLayer extends MarkerLayer {
    protected Bitmap markerBitmap;
    protected float rotation;
    protected int iconRadiu;  //图片边长的1/2  因为绘制要从左下角开始绘制

    public YXImageMarkerLayer(MapView mapView) {
        this(mapView, null, null);
    }

    public YXImageMarkerLayer(MapView mapView, PointF position) {
        this(mapView, null, position);
    }

    public YXImageMarkerLayer(MapView mapView, Bitmap bitmap) {
        this(mapView, bitmap, null);
    }

    public YXImageMarkerLayer(MapView mapView, Bitmap bitmap, PointF position) {
        super(mapView, position);
        markerBitmap = bitmap;
        markerPosition = new PointF();
    }

    @Override
    public boolean onTouch(MotionEvent event) {
        return false;
    }

    @Override
    public void draw(Canvas canvas) {
        if (markerBitmap == null || markerBitmap.isRecycled()) {
            return;
        }
        if (markerPosition == null) {
            return;
        }

        //TODO 其实也应该用矩阵变换来实现 到图片中心点的偏移
        iconRadiu = markerBitmap.getWidth() / 2;

        PointF pointF = MapUtils.getTransformedPoint(mMapView.getTransform(), markerPosition.x, markerPosition.y);

        canvas.drawBitmap(markerBitmap, pointF.x - iconRadiu, pointF.y - iconRadiu, null);
    }

    @Override
    public void release() {
        if (markerBitmap != null && !markerBitmap.isRecycled()) {
            markerBitmap.recycle();
        }
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public Bitmap getMarkerBitmap() {
        return markerBitmap;
    }

    public void setMarkerBitmap(Bitmap markerBitmap) {
        this.markerBitmap = markerBitmap;
    }

    public PointF getMarkerPosition() {
        return markerPosition;
    }

    public void setMarkerPosition(PointF markerPosition) {
        this.markerPosition = markerPosition;
    }

    public void setMarker(PointF position, float rotation) {
        this.rotation = rotation;
        this.markerPosition = position;
    }

    public void setMarker(float x, float y, float rotation) {
        this.rotation = rotation;
        this.markerPosition.x = x;
        this.markerPosition.y = y;
    }

}
