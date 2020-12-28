package com.aaa.lib.map.imp.layer;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;


import com.aaa.lib.map.MapUtils;
import com.aaa.lib.map.MapView;
import com.aaa.lib.map.area.RectangleArea;
import com.aaa.lib.map.imp.model.AreaBean;
import com.aaa.lib.map.imp.R;
import com.aaa.lib.map.imp.YXCoordinateConverter;
import com.aaa.lib.map.imp.YXMapView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class YXCleanAreaLayer extends YXAreaLayer<RectangleArea> {
    private static final String TAG = "YXCleanAreaLayer";

    private static final int STOKEN_W = 4;
    private static final int STOKEN_MARGIN = 32;

    private Paint mPaint;       //画笔
    private Path mRectBorder;
    private DashPathEffect mDashPath;
    private Bitmap deleteIcon;  //左上角删除按钮
    private Bitmap rotateIcon;  //右上角的旋转按钮
    private Bitmap scaleIcon;   //右下角的缩放按钮
    private int borderColor;
    private int bgColor;
    private int textColor;

    private Matrix tempMatrix; //当前矩形 相对view的变换，偏移/缩放/旋转
    float[] tmpPoint;

    private float lastPosX;
    private float lastPosY;         //用于计算平移
    private float lastRtAngle;      //圆心->右上角 与x轴夹角  用于计算旋转


    public YXCleanAreaLayer(MapView mapView) {
        super(mapView);
        Resources resources = mapView.getResources();
        borderColor = resources.getColor(R.color.area_stroke);
        bgColor = resources.getColor(R.color.area_fill);
        textColor = resources.getColor(R.color.white_gray);
        deleteIcon = BitmapFactory.decodeResource(mapView.getContext().getResources(), R.mipmap.delete);
        rotateIcon = BitmapFactory.decodeResource(mapView.getContext().getResources(), R.mipmap.rotate);
        scaleIcon = BitmapFactory.decodeResource(mapView.getContext().getResources(), R.mipmap.drag);

        area = new RectangleArea();

        tempMatrix = new Matrix();
        tmpPoint = new float[2];

        mRectBorder = new Path();
        mPaint = new Paint();
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(11 * resources.getDisplayMetrics().density);
        mDashPath = new DashPathEffect(new float[]{15, 5}, 0);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();

        canvas.setMatrix(mMapView.getTransform());
        drawRect(canvas);

        //在可编辑状态 并且获取到焦点
        if (isEdit && focusId == areaBean.getAreaID()) {
            drawIcon(canvas);
        }

        canvas.restore();
    }


    @Override
    protected boolean onTouchDown(float x, float y) {
        lastPosX = x;
        lastPosY = y;
        //判断点击位置
        if (MapUtils.isPointInCircle(x, y, area.lt, deleteIcon.getWidth())) {
            operate = OP_DELETE;
            return true;
        } else if (MapUtils.isPointInCircle(x, y, area.rt, rotateIcon.getWidth())) {
            operate = OP_ROTATE;
            //开始旋转时 设置右上角到圆心的初始角度
            lastRtAngle = MapUtils.getRotateByRect(x, y, area.center.x, area.center.y);
            return true;
        } else if (MapUtils.isPointInCircle(x, y, area.rb, scaleIcon.getWidth())) {
            operate = OP_SCALE;
            return true;
        } else if (MapUtils.isPointInRectangle(x, y, area.lt.x, area.lt.y, area.rt.x, area.rt.y, area.rb.x, area.rb.y, area.lt.x, area.lt.y)) {
            operate = OP_MOVE;
            return true;
        } else {
            //不在点击范围内 下一位
            operate = OP_NONE;
            return false;
        }
    }

    @Override
    protected boolean onTouchMove(float x, float y) {
        Log.i("YXCleanAreaLayer", "move to x: " + x + " y : " + y);
        if (operate == OP_MOVE) {
            move(x, y);
            return true;
        }
        if (operate == OP_ROTATE) {
            rotate(x, y);
            return true;
        } else if (operate == OP_SCALE) {
            scale(x, y);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected boolean onTouchUp(float x, float y) {
        //判断点击位置
        if (operate == OP_DELETE) {
            delete();
            return true;
        }
        return false;
    }

    /**
     * 删除
     */
    private void delete() {
        mMapView.getLayerManager().removeLayer(this);
    }

    /**
     * 缩放
     *
     * @param x 右下角拉伸到
     * @param y
     */
    private void scale(float x, float y) {
        //修改右下角
        float h = (float) MapUtils.pointToLine(x, y, area.lt.x, area.lt.y, area.rt.x, area.rt.y);
        float w = (float) MapUtils.pointToLine(x, y, area.lt.x, area.lt.y, area.lb.x, area.lb.y);


        if (h < 2 * deleteIcon.getWidth()) {
            //矩形太小 不操作
            h = 2 * deleteIcon.getWidth();
        }

        if (w < 2 * deleteIcon.getWidth()) {
            w = 2 * deleteIcon.getWidth();
        }

        //TODO   这里通过右下角来设置中心点 ，限制不完善， 缩放到最小后 会把控件推走。。。。
//        area.getCenter().x = (area.lt.x + x) / 2;
//        area.getCenter().y = (area.lt.y + y) / 2;


        //计算中心点  现在知道旋转角度 ， 以及高宽

        //求出对角钱长度一半
        //中心点X=左上角X+ 半对角线长  中心点Y= 左上角Y+0
        float centerX = area.lt.x + (float) Math.sqrt(w * w + h * h) / 2;
        float centerY = area.lt.y;
        // 算出当前对角线的的角度  对角线角度等于
        float degree = (float) (180 * Math.atan2(h, w) / Math.PI);
        tempMatrix.setRotate(area.rotate + degree, area.lt.x, area.lt.y);
        //从0度旋转到当前角度  获取到中心点
        MapUtils.getTransformedPoint(tempMatrix, centerX, centerY, area.center);

        area.setRect(area.center, w, h, area.rotate);
        lastRtAngle = MapUtils.getRotateByRect(area.rt.x, area.rt.y, area.center.x, area.center.y);

        mMapView.refresh();
    }


    /**
     * 平移
     *
     * @param x
     * @param y
     */
    private void move(float x, float y) {
        //计算平移距离
        float translateX = x - lastPosX;
        float translateY = y - lastPosY;

        //移动中心点 重新设置矩形
        area.setCenter(area.center.x + translateX, area.center.y + translateY);

        //记录上次触摸点
        lastPosX = x;
        lastPosY = y;

        mMapView.refresh();
    }

    /**
     * 旋转
     *
     * @param x
     * @param y
     */
    private void rotate(float x, float y) {

        //获取旋转后 右上角到圆心的角度
        float rtAngle = MapUtils.getRotateByRect(x, y, area.center.x, area.center.y);
        //当前旋转角度等于  之前的角度 加上增量
        float tempRotate = area.rotate + rtAngle - lastRtAngle;

        area.setRotate(tempRotate);
        lastRtAngle = MapUtils.getRotateByRect(area.rt.x, area.rt.y, area.center.x, area.center.y);

        mMapView.refresh();
    }

    @Override
    public void initAreaLayer(AreaBean areaBean) {
        List<double[]> areaVetexs = areaBean.getPoints();
        area.lt = YXCoordinateConverter.getScreenPointFromOrigin(areaVetexs.get(0)[0], areaVetexs.get(0)[1]);
        area.rt = YXCoordinateConverter.getScreenPointFromOrigin(areaVetexs.get(1)[0], areaVetexs.get(1)[1]);
        area.rb = YXCoordinateConverter.getScreenPointFromOrigin(areaVetexs.get(2)[0], areaVetexs.get(2)[1]);
        area.lb = YXCoordinateConverter.getScreenPointFromOrigin(areaVetexs.get(3)[0], areaVetexs.get(3)[1]);
        area.setRect(area.lt, area.rt, area.rb, area.lb);
//        area.setRect(areaVetexs.get(0)[0], areaVetexs.get(0)[1],
//                areaVetexs.get(1)[0], areaVetexs.get(1)[1],
//                areaVetexs.get(2)[0], areaVetexs.get(2)[1],
//                areaVetexs.get(3)[0], areaVetexs.get(3)[1]
//        );
        setAreaInfo(areaBean);
    }

    public void setArea(float centerX, float centerY, float width, float height, float rotate) {
        area.setRect(centerX, centerY, width, height, rotate);
        AreaBean areaBean = new AreaBean();
        areaBean.setAreaID(((YXMapView) mMapView).getUniqueAreaId(YXAreaLayer.TYPE_CLEAN_AREA));
        areaBean.setStamp(new Date().getTime());
        areaBean.setAreaName("cleanArea-" + areaBean.getAreaID());
        setAreaInfo(areaBean);
    }

    public AreaBean getNewAreaInfo() {
        AreaBean newAreaBean = new AreaBean();
        newAreaBean.setType(YXAreaLayer.TYPE_FORBIDDEN_AREA);
        newAreaBean.setAreaName(areaBean.getAreaName());
        newAreaBean.setAreaID(areaBean.getAreaID());
        newAreaBean.setStamp(areaBean.getStamp());


        PointF newlt = YXCoordinateConverter.getOriginPointFromScreen(area.lt.x, area.lt.y);
        PointF newrt = YXCoordinateConverter.getOriginPointFromScreen(area.rt.x, area.rt.y);
        PointF newrb = YXCoordinateConverter.getOriginPointFromScreen(area.rb.x, area.rb.y);
        PointF newlb = YXCoordinateConverter.getOriginPointFromScreen(area.lb.x, area.lb.y);

        ArrayList<double[]> points = new ArrayList<>();
        points.add(new double[]{newlt.x, newrt.y});
        points.add(new double[]{newrt.x, newrt.y});
        points.add(new double[]{newrb.x, newrb.y});
        points.add(new double[]{newlb.x, newlb.y});
        newAreaBean.setPoints(points);
        return newAreaBean;
    }

    private void setBorder(RectangleArea area) {
        mRectBorder.reset();
        mRectBorder.moveTo(area.lt.x, area.lt.y);
        mRectBorder.lineTo(area.rt.x, area.rt.y);
        mRectBorder.lineTo(area.rb.x, area.rb.y);
        mRectBorder.lineTo(area.lb.x, area.lb.y);
        mRectBorder.close();
    }

    /**
     * 绘制矩形边框和内部的栅格线
     *
     * @param canvas
     */
    private void drawRect(Canvas canvas) {
        setBorder(area);

        //画边框
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(borderColor);
        mPaint.setPathEffect(mDashPath);
        canvas.drawPath(mRectBorder, mPaint);

        //画背景
        mPaint.setColor(bgColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setPathEffect(null); //清除虚线效果
        canvas.drawPath(mRectBorder, mPaint);

        //写文字
        mPaint.setColor(textColor);
        mPaint.setTextAlign(Paint.Align.CENTER);

        canvas.rotate(area.rotate, area.center.x, area.center.y);
        canvas.drawText(order + "", area.center.x, area.center.y - mPaint.getTextSize(), mPaint);
        canvas.drawText(areaSize + "㎡", area.center.x, area.center.y + mPaint.getTextSize(), mPaint);
        canvas.rotate(-area.rotate, area.center.x, area.center.y);

    }

    /**
     * 绘制删除 旋转 缩放按钮
     *
     * @param canvas
     */
    private void drawIcon(Canvas canvas) {
        canvas.drawBitmap(deleteIcon, area.lt.x - deleteIcon.getWidth() / 2, area.lt.y - deleteIcon.getHeight() / 2, null);

        canvas.drawBitmap(rotateIcon, area.rt.x - rotateIcon.getWidth() / 2, area.rt.y - rotateIcon.getHeight() / 2, null);

        canvas.drawBitmap(scaleIcon, area.rb.x - scaleIcon.getWidth() / 2, area.rb.y - scaleIcon.getHeight() / 2, null);
    }

}
