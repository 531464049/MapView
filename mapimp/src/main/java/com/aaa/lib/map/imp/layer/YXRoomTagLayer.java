package com.aaa.lib.map.imp.layer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.Log;

import com.aaa.lib.map.MapView;
import com.aaa.lib.map.area.CircleArea;
import com.aaa.lib.map.imp.model.AreaBean;
import com.aaa.lib.map.imp.R;

import java.util.List;

public class YXRoomTagLayer extends YXAreaLayer<CircleArea> {

    private static final String TAG = "YXRoomTagLayer";
    private static final float MIN_TAG_PADDING = 10;

    private boolean clickTag = false;
    private static volatile boolean visible = false;    //是否可见
    private static volatile boolean selectable = false; //是否可选
    public static volatile boolean showOrder = false;   //选中时是否显示序号


    private TextPaint textPaint;
    private Paint bgPaint;
    private RectF bgRect;
    private float padding = MIN_TAG_PADDING;

    public YXRoomTagLayer(MapView mapView) {
        super(mapView);
        setEditMode(true);
        area = new CircleArea();

        bgRect = new RectF();
        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(mapView.getResources().getDisplayMetrics().density * 11);

        bgPaint = new Paint();
        textPaint.setAntiAlias(true);
    }


    @Override
    protected boolean onTouchDown(float x, float y) {
        //判断点击位置
        Log.i(TAG, "rect left top : " + bgRect.left + " , " + bgRect.top + "   " + bgRect.right + " ," + bgRect.bottom);
        if (bgRect.contains(x, y)) {
            clickTag = true;
            return true;
        } else {
            //不在点击范围内 下一位
            clickTag = false;
            return false;
        }
    }

    @Override
    protected boolean onTouchUp(float x, float y) {
        //判断点击位置
        if (clickTag) {
            isSelected = !isSelected;
            mMapView.refresh();
            return true;
        }
        return false;
    }

    @Override
    public void draw(Canvas canvas) {
        if (!visible) {
            return;
        }

        canvas.save();
        canvas.setMatrix(mMapView.getTransform());

        //重新设置矩形 和文本宽高
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float textWidth = textPaint.measureText(areaBean.getAreaName());   //测量文本长度
        float textHeight = fontMetrics.descent - fontMetrics.ascent;    //计算文本高度

        float textX = area.x - textWidth / 2;    //获取文本x坐标
        float textY = area.y + textHeight / 2 - fontMetrics.bottom;  //文本baseline  这里注意 不是文本顶部高度
        //矩形大小：  height= textHeight+2*padding    width = textWidth+ 2* textHeight/2 (左右两个半圆内不写字) +padding*2
        bgRect.set(area.x - textWidth / 2 - padding - textHeight / 2,
                area.y - textHeight / 2 - padding,
                area.x + textWidth / 2 + padding + textHeight / 2,
                area.y + textHeight / 2 + padding);

        //绘制圆角矩形， 圆角半径为高度的一半 即一个椭圆的条条
        canvas.drawRoundRect(bgRect, textHeight / 2 + padding, textHeight / 2 + padding, bgPaint);
        canvas.drawText(areaBean.getAreaName(), textX, textY, textPaint);

        if (selectable && isSelected) {
            //如果可以选中  并且选中了 那么就绘制蓝底白字
            textPaint.setColor(mResource.getColor(R.color.white));
            bgPaint.setColor(mResource.getColor(R.color.area_stroke));
            //如果选中了 并且显示序号 那么就绘制序号
            if (showOrder) {
                float orderTextWidth = textPaint.measureText(order + "");
                textPaint.setColor(mResource.getColor(R.color.white_gray));
                bgPaint.setColor(mResource.getColor(R.color.white));
                float radius = (textHeight + padding) / 2;
                canvas.drawCircle(area.x, bgRect.top - padding - radius, radius, bgPaint);
                canvas.drawText(order + "", area.x - orderTextWidth / 2, bgRect.top - padding - radius + textHeight / 2 - fontMetrics.bottom, textPaint);
            }
        } else {
            //如果不可选中 或者没有选中 那么就绘制白底灰字
            textPaint.setColor(mResource.getColor(R.color.white_gray));
            bgPaint.setColor(mResource.getColor(R.color.white));
        }

        canvas.restore();

    }

    @Override
    public void initAreaLayer(AreaBean areaBean) {
        List<double[]> areaVetexs = areaBean.getPoints();
        area.setCenter((float) areaVetexs.get(0)[0], (float) areaVetexs.get(0)[1]);
        setAreaInfo(areaBean);
    }


    public void setPadding(float padding) {
        if (padding < MIN_TAG_PADDING) {
            this.padding = MIN_TAG_PADDING;
        } else {
            this.padding = padding;
        }
    }

    public float getPadding() {
        return padding;
    }

    public static void setMode(boolean isVisible, boolean canSelect,boolean isShowOrder) {
        visible = isVisible;
        selectable = canSelect;
        showOrder = isShowOrder;
    }
}
