package com.aaa.lib.map;

import android.content.Context;
import android.graphics.Matrix;
import android.os.Build;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class TouchHandler {
    private static final String TAG = "TouchHandler";
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private boolean isScale;
    private MapView mMapView;

    public TouchHandler(Context context, final MapView mapView) {
        mMapView = mapView;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            float[] matrixValue = new float[9];
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.i(TAG, "onScroll distanceX : " + distanceX + "  distanceY: " + distanceY);

                /**
                 * author : master hao
                 */
                Matrix m = mMapView.getTransform();
                float w = mapView.getWidth();
                float h = mapView.getHeight();

                m.postTranslate(-distanceX, -distanceY);
                m.getValues(matrixValue);
                if (matrixValue[2] >= w / 2) {
                    matrixValue[2] = w / 2;
                }
                if (matrixValue[2] <= -w / 2) {
                    matrixValue[2] = -w / 2;
                }
                if (matrixValue[5] >= h / 2) {
                    matrixValue[5] = h / 2;
                }
                if (matrixValue[5] <= -h / 2) {
                    matrixValue[5] = -h / 2;
                }
                m.setValues(matrixValue);

//                mMapView.getTransform().postTranslate(-distanceX, -distanceY);
                mMapView.refresh();
                return true;
            }
        });
        mGestureDetector.setIsLongpressEnabled(false);

        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            private float preScaleFactor;
            private float curScaleFactor;

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                curScaleFactor = detector.getScaleFactor();
                float dF = curScaleFactor - preScaleFactor;
                Log.i(TAG, "onScale preScaleFactor : " + preScaleFactor + " curScaleFactor : " + curScaleFactor);

                MatrixUtil.scale(1f + dF, detector.getFocusX(), detector.getFocusY(), mMapView.getTransform());
                mMapView.refresh();

                preScaleFactor = curScaleFactor;//保存上一次的伸缩值
                return super.onScale(detector);
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                isScale = true;
                preScaleFactor = 1.0f;
                return super.onScaleBegin(detector);
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                isScale = false;
                super.onScaleEnd(detector);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mScaleGestureDetector.setQuickScaleEnabled(false);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean processed = mScaleGestureDetector.onTouchEvent(event);
        if (isScale) {
            return processed;
        } else {
            return mGestureDetector.onTouchEvent(event);
        }
    }
}
