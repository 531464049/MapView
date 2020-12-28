package com.aaa.lib.map;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.aaa.lib.map.layer.BaseLayer;


public class MapView<T extends LayerManager> extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = MapView.class.getSimpleName();

    private SurfaceHolder mHolder;
    private TouchHandler mTouchHandler;
    private DrawHelper mDrawHelper;
    private Matrix mMatrix;
    private int bgColor;
    protected T mLayerManager;

    public MapView(Context context) {
        this(context, null);
    }

    public MapView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setBackgroundColor(int color) {
        bgColor = color;
    }

    private void init() {
        getHolder().addCallback(this);
        mMatrix = new Matrix();
        mTouchHandler = new TouchHandler(this.getContext(), this);

    }

    public void refresh() {
        Log.i(TAG, "refresh");
        if (mDrawHelper != null) {
            mDrawHelper.refresh();
        }
    }

    public void refreshLayer(BaseLayer layer) {
        if (mDrawHelper != null) {
            if (layer != null) {
                mDrawHelper.refresh(layer);
            } else {
                mDrawHelper.refresh();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i(TAG, "surface onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.i(TAG, "surface onlayout");
        super.onLayout(changed, left, top, right, bottom);
    }

    /**
     * 处理触摸事件
     * 先根据layer层级，交由各个layer图层处理
     * 如果layer未处理 则交由自己做缩放平移
     *
     * @param event MotionEvent
     * @return boolean
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mLayerManager.dispatchToLayers(event)) {
            return true;
        }
        return mTouchHandler.onTouchEvent(event);
    }

    /**
     * Surface callback
     *
     * @param holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated");
        mHolder = holder;

        mDrawHelper = new DrawHelper(mHolder);
        mDrawHelper.setLayerList(mLayerManager.mLayerList);
        mDrawHelper.setBgColor(bgColor);
        mDrawHelper.refresh();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surfaceChanged");
        mHolder = holder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed");
        mHolder = null;
        mDrawHelper.release();
    }

    public Matrix getTransform() {
        return mMatrix;
    }


    public T getLayerManager() {
        return mLayerManager;
    }


    public void clearMap() {
        mLayerManager.clearLayer();
    }
}
