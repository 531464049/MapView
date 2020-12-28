package com.aaa.lib.map;

import android.graphics.Canvas;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.SurfaceHolder;

import com.aaa.lib.map.layer.BaseLayer;

import java.util.List;

/**
 * 图层绘制
 */
public class DrawHelper implements Runnable {

    private static final String TAG = DrawHelper.class.getSimpleName();

    private HandlerThread mHandlerThread;
    private Handler mDrawHandler;
    private SurfaceHolder mHolder;
    private List<BaseLayer> mLayerList;
    private volatile long mStartDrawTime;

    //单个图层刷新的runnable
    private Runnable mDrawLayerRunnable;
    private int bgColor;

    public DrawHelper(SurfaceHolder holder) {
        this.mHandlerThread = new HandlerThread("draw_map");
        this.mHandlerThread.start();
        this.mDrawHandler = new Handler(mHandlerThread.getLooper());
        this.mHolder = holder;
    }

    public void setBgColor(int color) {
        bgColor = color;
    }

    public void setLayerList(List<BaseLayer> mLayerList) {
        this.mLayerList = mLayerList;
    }

    @Override
    public void run() {
        Canvas canvas = null;
        try {
            canvas = mHolder.lockCanvas();
            //执行具体的绘制操作
            mStartDrawTime = System.currentTimeMillis();
            draw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null) {
                mHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    /**
     * 刷新地图
     * <p>
     * 如果上次绘制间隔超过了32ms那么绘制
     * 如果没超过32ms 计算延迟时间 等32ms后再绘制
     */
    public void refresh() {
        if (mDrawHandler != null) {
            long currentTime = System.currentTimeMillis();
            long interval = currentTime - mStartDrawTime;
            if (interval >= 32) {
                mDrawHandler.removeCallbacksAndMessages(null);
                mDrawHandler.post(this);
            } else {
                long delay = 32 - interval;
                mDrawHandler.removeCallbacksAndMessages(null);
                mDrawHandler.postDelayed(this, delay);
            }
        }
    }

    /**
     * 刷新指定图层
     *
     * @param layer
     */
    public void refresh(final BaseLayer layer) {
        if (mDrawHandler != null) {
            if (mDrawLayerRunnable == null) {
                mDrawLayerRunnable = new Runnable() {
                    @Override
                    public void run() {
                        Canvas canvas = null;
                        try {
                            canvas = mHolder.lockCanvas();
                            //执行具体的绘制操作
                            mStartDrawTime = System.currentTimeMillis();
                            layer.draw(canvas);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (canvas != null) {
                                mHolder.unlockCanvasAndPost(canvas);
                            }
                        }
                    }
                };
            }
            mDrawHandler.removeCallbacksAndMessages(null);
            mDrawHandler.post(mDrawLayerRunnable);
        }
    }

    /**
     * 绘制所有图层
     *
     * @param canvas
     */
    private void draw(Canvas canvas) {
        canvas.drawColor(bgColor);
        synchronized (mHolder) {
            if (mLayerList != null) {
                Log.i(TAG, "draw layer size :" + mLayerList.size());
                for (int i = 0; i < mLayerList.size(); i++) {
                    Log.i(TAG, "draw layer :" + mLayerList.get(i).getClass().getSimpleName());
                    mLayerList.get(i).draw(canvas);
                }
            }
        }
    }

    /**
     * 释放handler
     */
    public void release() {
        try {
            mHandlerThread.quit();
            mDrawHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mHandlerThread = null;
            mDrawHandler = null;
            mHolder = null;
        }
    }


}
