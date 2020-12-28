package com.aaa.lib.map.imp.layer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;


import com.aaa.lib.map.MapUtils;
import com.aaa.lib.map.MapView;
import com.aaa.lib.map.imp.R;
import com.aaa.lib.map.layer.AreaLayer;

import java.util.List;

public class YXAreaDivideLineLayer extends AreaLayer {
    public static final String TAG = "YXAreaDivideLineLayer";
    private PointF p1, p2; //分割线两个端点
    private volatile PointF p3, p4; //交点

    private int pointRadus = 0; //分割线端点圆的半径 也是点击范围的半径

    //是否点击两个端点 做拖拽用
    private boolean isTouchP1 = false;
    private boolean isTouchP2 = false;
    private boolean isTouchLine = false;

    private Paint mPaint; //画笔
    private PathEffect mDashPath; //虚线效果
    private List<Point> crossPoints;

    private float lastPosX;
    private float lastPosY;

    float[] tmpPoint;
    private OnDividerMoveListener onDividerMoveListener;

    public YXAreaDivideLineLayer(MapView mapView) {
        super(mapView);
        p1 = new PointF(-1, -1);
        p2 = new PointF(-1, -1);
        p3 = new PointF(-1, -1);
        p4 = new PointF(-1, -1);

        tmpPoint = new float[2];

        //通过屏幕密度设置具体 线宽度 端点半径
        Context context = mapView.getContext().getApplicationContext();
        float scale = context.getResources().getDisplayMetrics().density;
        int lineWidth = (int) (2 * scale);
        pointRadus = (int) (9 * scale);
        Log.i("YXAreaDivideLineLayer", "screen density： " + scale);

        //设置画笔
        mDashPath = new DashPathEffect(new float[]{5, 5}, 0);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(lineWidth);
        mPaint.setColor(context.getResources().getColor(R.color.area_divide_line));
    }

    public void setLine(PointF start, PointF end) {
        setLine(start.x, start.y, end.x, end.y);
    }

    public void setLine(float startX, float startY, float endX, float endY) {
        p1.x = startX;
        p1.y = startY;
        p2.x = endX;
        p2.y = endY;
    }

    public void setDividerMoveListener(OnDividerMoveListener listener) {
        onDividerMoveListener = listener;
    }

    @Override
    public boolean onTouch(MotionEvent event) {
        return handleTouch(event);
    }

    private boolean handleTouch(MotionEvent event) {
        Log.i(TAG, " x : " + event.getX() + "y " + event.getY());
        //获取反转矩阵 ，用于获取坐标平移前的点
        MapUtils.getInverseRotatedPoint(mMapView.getTransform(),event.getX(),event.getY(),tmpPoint);
        float x = tmpPoint[0];
        float y = tmpPoint[1];


        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //判断是否点击端点 准备拖动
            if (MapUtils.isInCircle(x, y, p1.x, p1.y, pointRadus * 3)) {
                isTouchP1 = true;
            } else if (MapUtils.isInCircle(x, y, p2.x, p2.y, pointRadus * 3)) {
                isTouchP2 = true;
            } else if (MapUtils.pointToLine(x, y, p1.x, p1.y, p2.x, p2.y) < pointRadus) {
                isTouchLine = true;
                lastPosX = x;
                lastPosY = y;
            } else {
                return false;
            }
            //按下去的时候不显示交点的直线 因为要计算。。。。
            setCrossPoint(-1, -1, -1, -1);
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            Log.i(TAG, "move to x: " + x + " y : " + y);
            //将端点移到拖动位置
            if (isTouchP1) {
                movePoint(p1, x, y);
            } else if (isTouchP2) {
                movePoint(p2, x, y);
            } else if (isTouchLine) {
                float tempX = x - lastPosX;
                float tempY = y - lastPosY;
                movePoint(p1, p1.x + tempX, p1.y + tempY);
                movePoint(p2, p2.x + tempX, p2.y + tempY);
                lastPosX = x;
                lastPosY = y;
            } else {
                return false;
            }
            mMapView.refresh();
            return true;

        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (isTouchP1 || isTouchP2 || isTouchLine) {
                isTouchP1 = false;
                isTouchP2 = false;
                if (onDividerMoveListener != null) {
                    onDividerMoveListener.onDividerMove(p1.x, p1.y, p2.x, p2.y);
                }
//                crossPoints = MapImageUtil.getCrossPoint(p1, p2);
//                //抬起的时候 看有无交点 有交点就显示分割直线
//                if (crossPoints.size() >= 2) {
//                    Point drawPoint1 = MapImageUtil.getScalePoint(crossPoints.get(0));
//                    Point drawPoint2 = MapImageUtil.getScalePoint(crossPoints.get(1));
//                    setCrossPoint(drawPoint1.x, drawPoint1.y, drawPoint2.x, drawPoint2.y);
//                }
                return true;
            }
        }
        return false;
    }

    private void movePoint(PointF point, float x, float y) {
        point.x = x;
        point.y = y;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();

        canvas.setMatrix(mMapView.getTransform());
        mPaint.setPathEffect(null); //清除虚线效果
        //画两个端点的圆
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(p1.x, p1.y, pointRadus, mPaint);
        canvas.drawCircle(p2.x, p2.y, pointRadus, mPaint);

        if (p3.x < 0 || p3.y < 0 || p4.x < 0 || p4.y < 0) {
            mPaint.setPathEffect(mDashPath);
            canvas.drawLine(p1.x, p1.y, p2.x, p2.y, mPaint);
        } else {
            //画实线
            canvas.drawLine(p3.x, p3.y, p4.x, p4.y, mPaint);

            //设置虚线效果 画虚线
            mPaint.setPathEffect(mDashPath);
            canvas.drawLine(p1.x, p1.y, p3.x, p3.y, mPaint);
            canvas.drawLine(p2.x, p2.y, p4.x, p4.y, mPaint);
        }

        canvas.restore();
    }

    @Override
    public void release() {

    }

    /**
     * 设置两个交点位置
     */
    private void setCrossPoint(float p3x, float p3y, float p4x, float p4y) {
        //判断下点的顺序 画图的时候默认认为顺序为 p1-p3-p4-p2 p3离p1近  如果不对 那么 把p3赋值为离p1近的那个点
        float distansep1p3 = Math.abs(p1.x - p3x);
        float distansep1p4 = Math.abs(p1.x - p4x);
        if (distansep1p3 <= distansep1p4) {
            this.p3.x = p3x;
            this.p3.y = p3y;
            this.p4.x = p4x;
            this.p4.y = p4y;
        } else {
            this.p3.x = p4x;
            this.p3.y = p4y;
            this.p4.x = p3x;
            this.p4.y = p3y;
        }
    }

    public List<Point> getCrossPoint() {
        return crossPoints;
    }

    interface OnDividerMoveListener {
        void onDividerMove(float x1, float y1, float x2, float y2);
    }

}
