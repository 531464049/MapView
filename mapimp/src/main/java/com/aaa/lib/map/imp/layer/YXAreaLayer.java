package com.aaa.lib.map.imp.layer;

import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;

import com.aaa.lib.map.MapUtils;
import com.aaa.lib.map.MapView;
import com.aaa.lib.map.area.Area;
import com.aaa.lib.map.imp.model.AreaBean;
import com.aaa.lib.map.layer.AreaLayer;


public class YXAreaLayer<T extends Area> extends AreaLayer<T> {

    private static final String TAG = "YXAreaLayer";


    public static final int TYPE_CLEAN_AREA = 0;
    public static final int TYPE_FORBIDDEN_AREA = 1;
    public static final int TYPE_FORBIDDEN_LINE = 3;
    public static final int TYPE_FORBIDDEN_MOP = 5;
    public static final int TYPE_LOCATION = 4;
    public static final int TYPE_ROOM = 2;

    protected static final int OP_NONE = 0;     // 无操作 超出点击范围
    protected static final int OP_MOVE = 1;       // 移动
    protected static final int OP_SCALE = 2;      // 缩放
    protected static final int OP_ROTATE = 3;     // 旋转
    protected static final int OP_DELETE = 4;     // 删除
    protected int operate;  //触摸操作

    public static boolean isMultiSelect = false;    //是否是单选
    protected volatile boolean isEdit; //是否可编辑  是否响应触摸事件

    protected static long focusId;   //当前焦点的id 有焦点显示三个图标
    protected boolean isSelected; //是否选中
    //TODO 排序
    protected int order;    //选中顺序

    private float[] tmpPoint;

    protected AreaBean areaBean;

    public YXAreaLayer(MapView mapView) {
        super(mapView);
        tmpPoint = new float[2];
        areaBean = new AreaBean();
    }

    @Override
    public boolean onTouch(MotionEvent event) {
        if (!isEdit) {
            //不可编辑  不处理事件
            return false;
        }
        return handleTouch(event);
    }

    @Override
    public void draw(Canvas canvas) {

    }

    @Override
    public void release() {
        //TODO 需要实现
    }

    public void initAreaLayer(AreaBean areaBean) {
        setAreaInfo(areaBean);
    }

    /**
     * 处理手势
     *
     * @param event
     * @return
     */
    private boolean handleTouch(MotionEvent event) {
        Log.i(TAG, " x : " + event.getX() + "y " + event.getY());
        //获取反转矩阵 ，用于获取坐标平移前的点
        MapUtils.getInverseRotatedPoint(mMapView.getTransform(), event.getX(), event.getY(), tmpPoint);
        float x = tmpPoint[0];
        float y = tmpPoint[1];

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            boolean isHandled = onTouchDown(x, y);
            if (isHandled) {
                focusId = areaBean.getAreaID();
            }
            return isHandled;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            return onTouchMove(x, y);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            return onTouchUp(x, y);
        }
        return false;
    }

    protected boolean onTouchDown(float x, float y) {
        return false;
    }

    protected boolean onTouchMove(float x, float y) {
        return false;
    }

    protected boolean onTouchUp(float x, float y) {
        return false;
    }

    public void setEditMode(boolean isEdit) {
        this.isEdit = isEdit;
    }

    public boolean getEditMode() {
        return isEdit;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public AreaBean getAreaInfo() {
        return areaBean;
    }
    public AreaBean getNewAreaInfo() {
        return areaBean;
    }

    public void setAreaInfo(AreaBean areaBean) {
        this.areaBean = areaBean;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isClosePower(){
        return false;
    }
}
