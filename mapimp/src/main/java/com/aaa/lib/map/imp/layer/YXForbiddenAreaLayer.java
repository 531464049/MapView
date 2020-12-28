package com.aaa.lib.map.imp.layer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.util.Log;
import android.widget.Toast;

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

public class YXForbiddenAreaLayer extends YXAreaLayer<RectangleArea> {
    private static final String TAG = "YXForbiddenAreaLayer";

    private Path rectPath213;   //矩形两条边的路径  右上-左上-左下  用于绘制边框和内部栅格
    private Path rectPath243;   //矩形两条边的路径  右上-右下-左下

    protected Paint mPaint;       //画笔
    private Bitmap deleteIcon;  //左上角删除按钮
    private Bitmap rotateIcon;  //右上角的旋转按钮
    private Bitmap scaleIcon;   //右下角的缩放按钮

    private Matrix tempMatrix; //当前矩形 相对view的变换，偏移/缩放/旋转
    float[] tmpPoint;

    private float lastPosX;
    private float lastPosY;
    private float lastRtAngle;

    private static final int STOKEN_W = 4;
    private static final int STOKEN_MARGIN = 32;

    boolean isChanged = false;

    public YXForbiddenAreaLayer(MapView mapView) {
        super(mapView);
        deleteIcon = BitmapFactory.decodeResource(mapView.getContext().getResources(), R.mipmap.delete);
        rotateIcon = BitmapFactory.decodeResource(mapView.getContext().getResources(), R.mipmap.rotate);
        scaleIcon = BitmapFactory.decodeResource(mapView.getContext().getResources(), R.mipmap.drag);

        tempMatrix = new Matrix();
        tmpPoint = new float[2];

        area = new RectangleArea();

        rectPath213 = new Path();
        rectPath243 = new Path();

        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
    }

    @Override
    public void initAreaLayer(AreaBean areaBean) {
        List<double[]> areaVetexs = areaBean.getPoints();
        area.lt = YXCoordinateConverter.getScreenPointFromOrigin(areaVetexs.get(0)[0], areaVetexs.get(0)[1]);
        area.rt = YXCoordinateConverter.getScreenPointFromOrigin(areaVetexs.get(1)[0], areaVetexs.get(1)[1]);
        area.rb = YXCoordinateConverter.getScreenPointFromOrigin(areaVetexs.get(2)[0], areaVetexs.get(2)[1]);
        area.lb = YXCoordinateConverter.getScreenPointFromOrigin(areaVetexs.get(3)[0], areaVetexs.get(3)[1]);
        area.setRect(area.lt, area.rt, area.rb, area.lb);
        Log.i(TAG, "set layer area : " + area.toString());
        setAreaInfo(areaBean);
    }

    public AreaBean getNewAreaInfo() {
        AreaBean newAreaBean = new AreaBean();
        newAreaBean.setType(YXAreaLayer.TYPE_FORBIDDEN_AREA);
        newAreaBean.setAreaName(areaBean.getAreaName());
        newAreaBean.setAreaID(areaBean.getAreaID());
        newAreaBean.setStamp(areaBean.getStamp());


        PointF newlt = YXCoordinateConverter.getOriginPointFromScreen(area.lt.x,area.lt.y);
        PointF newrt = YXCoordinateConverter.getOriginPointFromScreen(area.rt.x,area.rt.y);
        PointF newrb = YXCoordinateConverter.getOriginPointFromScreen(area.rb.x,area.rb.y);
        PointF newlb = YXCoordinateConverter.getOriginPointFromScreen(area.lb.x,area.lb.y);

        ArrayList<double[]> points = new ArrayList<>();
        points.add(new double[]{newlt.x, newrt.y});
        points.add(new double[]{newrt.x, newrt.y});
        points.add(new double[]{newrb.x, newrb.y});
        points.add(new double[]{newlb.x, newlb.y});
        newAreaBean.setPoints(points);
        return newAreaBean;
    }

    @Override
    public boolean isClosePower(){
        //TODO 这里获取了外部的电源位置
        return MapUtils.isAreaCrossPower(area,((YXMapView)mMapView).getPowerLayer().getArea());
    }

    public void setArea(float centerX, float centerY, float width, float height, float rotate) {
        area.setRect(centerX, centerY, width, height, rotate);
        AreaBean areaBean = new AreaBean();
        areaBean.setAreaID(((YXMapView) mMapView).getUniqueAreaId(YXAreaLayer.TYPE_FORBIDDEN_AREA));
        areaBean.setStamp(new Date().getTime());
        areaBean.setAreaName("forbidarea-" + areaBean.getAreaID());
        setAreaInfo(areaBean);
    }

    public boolean isChanged() {
        return isChanged;
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
        } else if (MapUtils.isPointInRectangle(x, y, area.lt.x, area.lt.y, area.rt.x, area.rt.y, area.rb.x, area.rb.y, area.lb.x, area.lb.y)) {
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
        Log.i("YXForbiddenAreaLayer", "move to x: " + x + " y : " + y);
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
        }else if(operate== OP_NONE){
            return false;
        }else {
            if(isClosePower()){
                Toast.makeText(mMapView.getContext().getApplicationContext(), R.string.area_close_charging_line,Toast.LENGTH_SHORT).show();
            }
            return true;
        }
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
        //获取新rb的点到两条边的距离，即为新矩形宽高
        float h = (float) MapUtils.pointToLine(x, y, area.lt.x, area.lt.y, area.rt.x, area.rt.y);
        float w = (float) MapUtils.pointToLine(x, y, area.lt.x, area.lt.y, area.lb.x, area.lb.y);

        //矩形太小 不操作
        if (h < 2 * deleteIcon.getWidth()) {
            h = 2 * deleteIcon.getWidth();
        }

        if (w < 2 * deleteIcon.getWidth()) {
            w = 2 * deleteIcon.getWidth();
        }

        //这里如果简单通过右下角来设置中心点 ， 缩放到最小后 会把控件推走。 不删是因为 还蛮好玩的。。。。
        //        center.x = (lt.x + x) / 2;
        //        center.y = (lt.y + y) / 2;

        //计算中心点  现在知道旋转角度 ， 以及高宽，  求出对角钱长度一半 算出当前对角线的的角度  从0度旋转到当前角度  获取到中心点
        float degree = (float) (180 * Math.atan2(h, w) / Math.PI);
        tempMatrix.setRotate(area.rotate + degree, area.lt.x, area.lt.y);
        //中心点X=左上角X+ 半对角线长  中心点Y= 左上角Y+0
        float centerX = area.lt.x + (float) Math.sqrt(w * w + h * h) / 2;
        float centerY = area.lt.y;
        MapUtils.getTransformedPoint(tempMatrix, centerX, centerY, area.center);

        area.setRect(area.center, w, h, area.rotate);

        isChanged = true;

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

        //移动中心点
        area.setCenter(area.center.x + translateX, area.center.y + translateY);

        //记录上次触摸点
        lastPosX = x;
        lastPosY = y;

        isChanged = true;

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
        float rtEndRotation = MapUtils.getRotateByRect(x, y, area.center.x, area.center.y);
        //当前旋转角度等于  之前的角度 加上增量
        float tempRotate = area.rotate + rtEndRotation - lastRtAngle;

        area.setRotate(tempRotate);
        lastRtAngle = rtEndRotation;

        isChanged = true;

        mMapView.refresh();
    }

    @Override
    public void draw(Canvas canvas) {
        Log.i("YXForbiddenAreaLayer", "draw area start");
        canvas.save();

        canvas.setMatrix(mMapView.getTransform());

        drawRect(canvas);

        if (isEdit && focusId == areaBean.getAreaID()) {
            drawIcon(canvas);
        }

        canvas.restore();
        Log.i("YXForbiddenAreaLayer", "draw area end");
    }


    /**
     * 设置路径 用于绘制
     */
    private void setBorder(RectangleArea area) {
        rectPath213.reset();
        rectPath213.moveTo(area.rt.x, area.rt.y);
        rectPath213.lineTo(area.lt.x, area.lt.y);
        rectPath213.lineTo(area.lb.x, area.lb.y);

        rectPath243.reset();
        rectPath243.moveTo(area.rt.x, area.rt.y);
        rectPath243.lineTo(area.rb.x, area.rb.y);
        rectPath243.lineTo(area.lb.x, area.lb.y);
    }


    public PointF getCenter() {
        return area.center;
    }

    /**
     * 绘制矩形边框和内部的栅格线
     *
     * @param canvas
     */
    public void drawRect(Canvas canvas) {

        //设置边框
        setBorder(area);

        //画边框
        canvas.drawPath(rectPath213, mPaint);
        canvas.drawPath(rectPath243, mPaint);

        //画栅格
        PathMeasure mPathMeasure1 = new PathMeasure(rectPath213, false);
        PathMeasure mPathMeasure2 = new PathMeasure(rectPath243, false);
        float[] point1 = new float[2];
        float[] point2 = new float[2];

        for (int i = 0; i < area.width + area.height; i = i + STOKEN_MARGIN) {
            mPathMeasure1.getPosTan(i, point1, null);
            mPathMeasure2.getPosTan(i, point2, null);
            canvas.drawLine(point1[0], point1[1], point2[0], point2[1], mPaint);
        }
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
