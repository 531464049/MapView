package com.aaa.lib.map.imp.layer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;

import com.aaa.lib.map.MapView;
import com.aaa.lib.map.layer.PathLayer;

import java.util.List;

public class YXPathLayer extends PathLayer {
    private static final String TAG="YXPathLayer";
    private Bitmap mPathBitmap;

    public YXPathLayer(MapView mapView) {
        super(mapView);
    }
    public YXPathLayer(MapView mapView, Bitmap bitmap) {
        super(mapView);
        this.mPathBitmap = bitmap;
    }

    public YXPathLayer(MapView mapView, List<PointF> pathList) {
        super(mapView);
        this.mPathList = pathList;
    }

    @Override
    public boolean onTouch(MotionEvent event) {
        return false;
    }

    @Override
    public void draw(Canvas canvas) {
        if(mPathBitmap==null||mPathBitmap.isRecycled()){
            return;
        }
        Log.i(TAG,"draw path ");

        canvas.drawBitmap(mPathBitmap,mMapView.getTransform(),null);
    }

    @Override
    public void release() {
        if (mPathBitmap != null && !mPathBitmap.isRecycled()) {
            mPathBitmap.recycle();
        }
    }

    public Bitmap getPathBitmap() {
        return mPathBitmap;
    }

    public void setPathBitmap(Bitmap mPathBitmap) {
        this.mPathBitmap = mPathBitmap;
    }
}
