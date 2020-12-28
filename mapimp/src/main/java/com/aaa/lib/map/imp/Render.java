package com.aaa.lib.map.imp;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.aaa.lib.map.imp.model.LDMapBean;
import com.aaa.lib.map.imp.model.LDPathBean;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class Render {
    private static final String TAG = Render.class.getSimpleName();
    private static boolean hasPower = false;

    public static Bitmap renderMap(LDMapBean ldMapBean, LDPathBean ldPathBean) {
        if (ldMapBean.width <= 0 && ldMapBean.height <= 0) {
            return null;
        }
        //创建地图bitmap
        Bitmap bitmapInfo = Bitmap.createBitmap(ldMapBean.width, ldMapBean.height, Bitmap.Config.ARGB_8888);
        int[] pathX = getIntArray(ldPathBean.posPathX);
        int[] pathY = getIntArray(ldPathBean.posPathY);
        Log.i(TAG, "ldMapBean.fullMapData: " + ldMapBean.fullMapData + " pathX:" + pathX + " path y : " + pathY);
        Bitmap mapBitmap = MapDrawingAPIs.Geo(ldMapBean.fullMapData, ldMapBean.width, ldMapBean.height, pathX, pathY, pathX.length, ldMapBean.x_min, ldMapBean.y_min, 1, hasPower, bitmapInfo);
        setPathParam();
        return mapBitmap;
    }

    public static Bitmap renderPath(LDMapBean ldMapBean, LDPathBean ldPathBean) {
        //List<Integer> 转 int[]   api21有intStream
        int[] pathX = getIntArray(ldPathBean.posPathX);
        int[] pathY = getIntArray(ldPathBean.posPathY);

        Log.d(TAG, "pathBitmap, pathX.length:" + pathX.length);
        if (pathX.length < 10 || pathX.length != pathY.length) {
            return null;
        }

        Log.d(TAG, "pathBitmap, width:" + ldMapBean.width + " height:" + ldMapBean.height);
        if (ldMapBean.width <= 0 || ldMapBean.height <= 0) {
            return null;
        }

        //创建路径bitmap
        Bitmap bitmapInfo = Bitmap.createBitmap(ldMapBean.width, ldMapBean.height, Bitmap.Config.ARGB_8888);
        Bitmap pathBitmap = MapDrawingAPIs.Path(ldMapBean.width, ldMapBean.height, pathX, pathY, pathX.length, ldMapBean.x_min, ldMapBean.y_min, 1, hasPower, bitmapInfo);
        setPathParam();
        //saveBitmapFile(pathBitmap);
        Log.d(TAG, "pathBitmap, width:" + pathBitmap.getWidth() + " height:" + pathBitmap.getHeight());
        return pathBitmap;
    }



    //设置路径起始结束点 以及mul信息
    private static void setPathParam() {
        //获取路径起始点和偏移
        int[] pathPoint = MapDrawingAPIs.getPathPoint();

        YXCoordinateConverter.mul = pathPoint[6];
        YXCoordinateConverter.top = pathPoint[0];
        YXCoordinateConverter.left = pathPoint[1];
        YXCoordinateConverter.devicePosX = pathPoint[4];
        YXCoordinateConverter.devicePosY = pathPoint[5];
    }


    private static int[] getIntArray(List<Integer> list) {
        int pathSize = 0;
        if (list != null && (pathSize = list.size()) > 0) {
            int[] array = new int[pathSize];
            for (int index = 0; index < pathSize; index++) {
                array[index] = list.get(index);
            }
            return array;
        } else {
            return new int[1];
        }
    }


    public static void saveBitmapFile(Bitmap bitmap) {
        Log.i(TAG, "directory" + Environment.getExternalStorageDirectory().getAbsolutePath());
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/tempPathImage/");
        if (!dir.exists() || dir.isFile()) {
            dir.mkdirs();
        }
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tempPathImage/" + System.currentTimeMillis() + ".png";
        File file = new File(path);//将要保存图片的路径
        try {
            file.createNewFile();
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
