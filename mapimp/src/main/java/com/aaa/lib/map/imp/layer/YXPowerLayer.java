package com.aaa.lib.map.imp.layer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;

import com.aaa.lib.map.MapUtils;
import com.aaa.lib.map.MapView;
import com.aaa.lib.map.imp.R;

public class YXPowerLayer extends YXPointAroundAreaLayer {
    protected Bitmap markerBitmap;
    private boolean showProtectArea = false;
    private float rotation;
    private Paint mPaint;
    private Matrix matrix;


    public YXPowerLayer(MapView mapView) {
        super(mapView);

        markerBitmap = BitmapFactory.decodeResource(mapView.getResources(), R.mipmap.charger_inmap);
        matrix = new Matrix();

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setColor(mapView.getResources().getColor(R.color.area_fill));
    }

    @Override
    public void draw(Canvas canvas) {
        if (markerBitmap == null || markerBitmap.isRecycled()) {
            return;
        }

        if (area == null) {
            return;
        }

        PointF pointF = MapUtils.getTransformedPoint(mMapView.getTransform(), area.x, area.y);

        //绘制保护区域 如果半径为0 则不绘制
        if (area.radius > 0) {
            canvas.drawCircle(pointF.x, pointF.y, area.radius, mPaint);
        }

        //绘制图标 设置位置和偏移
        matrix.setTranslate(pointF.x - markerBitmap.getWidth() / 2f, pointF.y - markerBitmap.getWidth() / 2f);
        matrix.postRotate(rotation);
        canvas.drawBitmap(markerBitmap, matrix, null);

    }

    public void initPowerLayer(float centerX, float centerY, float radius, float rotation) {
        area.setCircle(centerX, centerY, radius);
        this.rotation = rotation;
    }

    public Bitmap getBitmap() {
        return markerBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        if (markerBitmap != null) {
            markerBitmap.recycle();
        }
        this.markerBitmap = bitmap;
    }

    public void setCenter(float centerX, float centerY) {
        area.setCenter(centerX, centerY);
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public boolean isShowProtectArea() {
        return showProtectArea;
    }

    public void setShowProtectArea(boolean showProtectArea) {
        this.showProtectArea = showProtectArea;
    }
}
