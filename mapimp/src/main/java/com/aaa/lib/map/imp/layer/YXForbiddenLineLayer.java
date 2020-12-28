package com.aaa.lib.map.imp.layer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;
import android.widget.Toast;

import com.aaa.lib.map.MapUtils;
import com.aaa.lib.map.MapView;
import com.aaa.lib.map.area.LineArea;
import com.aaa.lib.map.imp.model.AreaBean;
import com.aaa.lib.map.imp.R;
import com.aaa.lib.map.imp.YXCoordinateConverter;
import com.aaa.lib.map.imp.YXMapView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class YXForbiddenLineLayer extends YXAreaLayer<LineArea> {
    private static final String TAG = "YXForbiddenLineLayer";

    private Paint mPaint;       //画笔
    private Bitmap deleteIcon;  //左上角删除按钮
    private Bitmap rotateIcon;  //右上角的旋转按钮

    private float lastPosX;
    private float lastPosY;

    private boolean isChanged;

    public YXForbiddenLineLayer(MapView mapView) {
        super(mapView);
        deleteIcon = BitmapFactory.decodeResource(mapView.getContext().getResources(), R.mipmap.delete);
        rotateIcon = BitmapFactory.decodeResource(mapView.getContext().getResources(), R.mipmap.rotate);

        area = new LineArea();

        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
    }

    @Override
    public void initAreaLayer(AreaBean areaBean) {
        List<double[]> areaVetexs = areaBean.getPoints();
        area.p1 = YXCoordinateConverter.getScreenPointFromOrigin(areaVetexs.get(0)[0], areaVetexs.get(0)[1]);
        area.p2 = YXCoordinateConverter.getScreenPointFromOrigin(areaVetexs.get(1)[0], areaVetexs.get(1)[1]);
        area.setLine(area.p1.x, area.p1.y, area.p2.x, area.p2.y);
        setAreaInfo(areaBean);
    }

    public void setLine(float x1, float y1, float x2, float y2) {
        area.setLine(x1, y1, x2, y2);
        AreaBean areaBean = new AreaBean();
        areaBean.setAreaID(((YXMapView) mMapView).getUniqueAreaId(YXAreaLayer.TYPE_FORBIDDEN_LINE));
        areaBean.setStamp(new Date().getTime());
        areaBean.setAreaName("forbidline-" + areaBean.getAreaID());
        setAreaInfo(areaBean);
    }


    @Override
    public AreaBean getNewAreaInfo() {
        AreaBean newAreaBean = new AreaBean();
        newAreaBean.setType(YXAreaLayer.TYPE_FORBIDDEN_AREA);
        newAreaBean.setAreaName(areaBean.getAreaName());
        newAreaBean.setAreaID(areaBean.getAreaID());
        newAreaBean.setStamp(areaBean.getStamp());

        PointF newP1 = YXCoordinateConverter.getOriginPointFromScreen(area.p1.x, area.p1.y);
        PointF newP2 = YXCoordinateConverter.getOriginPointFromScreen(area.p2.x, area.p2.y);

        ArrayList<double[]> points = new ArrayList<>();
        points.add(new double[]{newP1.x, newP1.y});
        points.add(new double[]{newP2.x, newP2.y});
        points.add(new double[]{newP2.x, newP2.y});
        points.add(new double[]{newP1.x, newP1.y});
        newAreaBean.setPoints(points);
        return newAreaBean;
    }

    @Override
    public boolean isClosePower() {
        //TODO 这里获取了外部的电源位置
        return MapUtils.isLineCrossPower(area, ((YXMapView) mMapView).getPowerLayer().getArea());
    }

    public boolean isChanged() {
        return isChanged;
    }

    @Override
    protected boolean onTouchDown(float x, float y) {
        lastPosX = x;
        lastPosY = y;
        //判断点击位置
        if (MapUtils.isPointInCircle(x, y, area.p1, deleteIcon.getWidth())) {
            operate = OP_DELETE;
            return true;
        } else if (MapUtils.isPointInCircle(x, y, area.p2, rotateIcon.getWidth())) {
            operate = OP_SCALE;
            return true;
        } else if (MapUtils.pointToLine(x, y, area.p1.x, area.p1.y, area.p2.x, area.p2.y) < 2 * deleteIcon.getWidth()) {
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
        Log.i("YXForbiddenLineLayer", "move to x: " + x + " y : " + y);
        if (operate == OP_MOVE) {
            move(x, y);
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
        } else if (operate == OP_NONE) {
            return false;
        } else {
            if (isClosePower()) {
                Toast.makeText(mMapView.getContext().getApplicationContext(), R.string.line_close_charging_line,Toast.LENGTH_SHORT).show();
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

        double distance = MapUtils.distance(area.p1.x, area.p1.y, x, y);

        if (distance < 2 * deleteIcon.getWidth()) {
            //两条线距离太近 不操作
            return;
        }

        area.setP2(x, y);

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

        area.setLine(area.p1.x + translateX,
                area.p1.y + translateY,
                area.p2.x + translateX,
                area.p2.y + translateY);

        //记录上次触摸点
        lastPosX = x;
        lastPosY = y;

        isChanged = true;

        mMapView.refresh();
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();

        canvas.setMatrix(mMapView.getTransform());
        drawLine(canvas);

        if (isEdit && focusId == areaBean.getAreaID()) {
            drawIcon(canvas);
        }

        canvas.restore();
    }

    /**
     * 绘制矩形边框和内部的栅格线
     *
     * @param canvas
     */
    public void drawLine(Canvas canvas) {
        canvas.drawLine(area.p1.x, area.p1.y, area.p2.x, area.p2.y, mPaint);
    }

    /**
     * 绘制删除 旋转 缩放按钮
     *
     * @param canvas
     */
    private void drawIcon(Canvas canvas) {
        canvas.drawBitmap(deleteIcon, area.p1.x - deleteIcon.getWidth() / 2, area.p1.y - deleteIcon.getHeight() / 2, null);

        canvas.drawBitmap(rotateIcon, area.p2.x - rotateIcon.getWidth() / 2, area.p2.y - rotateIcon.getHeight() / 2, null);
    }
}
