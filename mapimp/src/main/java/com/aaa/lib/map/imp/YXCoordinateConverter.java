package com.aaa.lib.map.imp;

import android.graphics.Matrix;
import android.graphics.PointF;

import com.aaa.lib.map.imp.model.LDMapBean;
import com.aaa.lib.map.imp.model.Robot;

public class YXCoordinateConverter {
    public static float mul = 1f;   //地图记载jni绘制时的缩放
    public static float left = 0f;  //裁剪后的地图基于原地图
    public static float top = 0f;
    public static int devicePosX = 0; //扫地机的x坐标
    public static int devicePosY = 0; //扫地机的y坐标

    public static PointF getScreenPointFromOrigin(double x, double y) {
        LDMapBean ldMapBean = Robot.get().getMapData();

        x = x - ldMapBean.x_min;
        y = y - ldMapBean.y_min;

        x = x / ldMapBean.resolution;
        y = y / ldMapBean.resolution;

        double kx = ldMapBean.width - y;
        double ky = ldMapBean.height - x;

        kx = kx * mul - left + 10;
        ky = ky * mul - top + 10;

        return new PointF((float) kx, (float) ky);
    }

    /**
     * 路径数据的点 单位是厘米 所以先转化成米
     */
    private PointF getScreenPathPointFromOrigin(double x, double y) {
        x = x / 100;
        y = y / 100;
        return getScreenPointFromOrigin(x, y);
    }


    /**
     * 将屏幕像素坐标转换成地图原始坐标
     *
     * @param x
     * @param y
     * @return
     */
    public static PointF getOriginPointFromScreen(double x, double y) {
        LDMapBean ldMapBean = Robot.get().getMapData();
        double originY = (ldMapBean.width - (x - 10 + left) / mul) * ldMapBean.resolution + ldMapBean.y_min;
        double originX = (ldMapBean.height - (y - 10 + top) / mul) * ldMapBean.resolution + ldMapBean.x_min;
        return new PointF((float) originX, (float) originY);
    }

    public static float worldLengthToMap(float length, LDMapBean ldMapBean, Matrix matrix) {
        float[] value = new float[9];
        matrix.getValues(value);
        float mapLength = length / ldMapBean.resolution * mul * value[0];
        return mapLength;
    }

    public static float mapLengthToWorld(float length, LDMapBean ldMapBean, Matrix matrix) {
        float[] value = new float[9];
        matrix.getValues(value);
        float worldLength = length / mul / value[0] * ldMapBean.resolution;
        return worldLength;
    }

}
