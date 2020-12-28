package com.aaa.lib.map.imp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.util.Log;

import com.aaa.lib.map.imp.model.LDMapBean;
import com.aaa.lib.map.imp.model.Robot;

import java.util.ArrayList;

/**
 * Class MapImageUtil
 * Effect
 * Created by sane
 * on 2020/5/21
 */
public class MapImageUtil {
    private static final String TAG = "MapImageUtil";


   /* public static List<Point> getCrossPoint(Point p1, Point p2) {
        ArrayList<RoomTagView> selectedDivideTag = MapViewManager.getSelectedRoomTag();
        List<Point> crossPointList = new ArrayList<>();
        if (selectedDivideTag.size() != 1) {
            Log.i(TAG, "selected more than one tag");
            return crossPointList;
        }
        int roomId = selectedDivideTag.get(0).getRoomId();

        //通过界面显示的点获取地图点的坐标
        Point mapPoint1 = MapImageUtil.getBeforeScalePoint(p1);
        Point mapPoint2 = MapImageUtil.getBeforeScalePoint(p2);

        LDMapBean ldMapBean = Robot.get().getMapData();
        //获取边缘路径
        int[][] borderPath = MapDrawingAPIs.getBorderPath(ldMapBean.fullMapData, ldMapBean.width, ldMapBean.height, roomId);
        if (borderPath.length < 2) {
            return crossPointList;
        }

        //计算最后一个点和第一个点的线段交点 不写在循环里减少判断
        Point last = calculateCross(borderPath[borderPath.length - 1][0], borderPath[borderPath.length - 1][1], borderPath[0][0], borderPath[0][1], mapPoint1.x, mapPoint1.y, mapPoint2.x, mapPoint2.y);
        if (last != null) {
            crossPointList.add(last);
        }
        //计算交点
        for (int i = 0; i < borderPath.length - 1; i++) {
            Point p = calculateCross(borderPath[i][0], borderPath[i][1], borderPath[i + 1][0], borderPath[i + 1][1], mapPoint1.x, mapPoint1.y, mapPoint2.x, mapPoint2.y);
            if (p != null) {
                crossPointList.add(p);
                Log.i(TAG, "cross point" + i + "  x: " + p.x + "  y: " + p.y);
            }
        }

        Log.i(TAG, "divide point 1 x: " + p1.x + "  y:" + p1.y);
        Log.i(TAG, "divide point 2 x: " + p2.x + "  y:" + p2.y);
        Log.i(TAG, "divide point 1 x: " + mapPoint1.x + "  y:" + mapPoint1.y);
        Log.i(TAG, "divide point 2 x: " + mapPoint2.x + "  y:" + mapPoint2.y);
        Log.i(TAG, "map width x: " + ldMapBean.width + "  height:" + ldMapBean.height);
        Log.i(TAG, "map offset rate: " + PolylineView.rate + "  mul:" + PolylineView.mul);
        Log.i(TAG, "map offset resolution: " + ldMapBean.resolution + "  x_min:" + ldMapBean.x_min);
        Log.i(TAG, "map offset resolution: " + ldMapBean.resolution + "  y_min:" + ldMapBean.y_min);


        return crossPointList;
    }*/

    //判断两个区域是否相邻 已测试 当前地图耗时不超过1ms
    public static boolean isAreaAdjacent(int id1, int id2) {
        LDMapBean ldMapBean = Robot.get().getMapData();
        Log.i(TAG, "id1:" + id1 + " id2:" + id2);
        int[] fullMapData = ldMapBean.fullMapData;
        int w = ldMapBean.width;
        int h = ldMapBean.height;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int areaId = fullMapData[i * w + j];
                if (areaId == id1) {

                    int up = i - 1 < 0 ? 0 : i - 1;
                    int left = j - 1 < 0 ? 0 : j - 1;
                    int right = j + 1 == w ? (w) : j + 1;
                    int bottom = i + 1 == h ? (h - 1) : i + 1;
                    //判断 上下左右是否是area2
                    if (id2 == fullMapData[up * w + j] || id2 == fullMapData[bottom * w + j] || id2 == fullMapData[i * w + left] || id2 == fullMapData[i * w + right]) {
                        Log.i(TAG, "两个区域相邻 判断的点为：i=" + i + " j= " + j);
                        return true;
                    }
/*                    //判断 左上 右上 左下 右下是否是area2
                    if (id2 == fullMapData[up * w + left] || id2 == fullMapData[bottom * w + left] || id2 == fullMapData[up * w + right] || id2 == fullMapData[bottom * w + right]) {
                        return true;
                    }*/
                }
            }
        }
        Log.i(TAG, "判断完了 两个区域不相邻");
        return false;
    }

    public static Point calculateCross(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        //判断两条直线是否平行
        double parallelValue = (y2 - y1) * (x4 - x3) - (y4 - y3) * (x2 - x1);

        //直线平行直接返回
        if (0 == parallelValue) {
            return null;
        }
        //两条直线的交点
        double x = (x1 * (y2 - y1) * (x4 - x3) - x3 * (x2 - x1) * (y4 - y3) + (y3 - y1) * (x2 - x1) * (x4 - x3)) / parallelValue;
        double y = ((x1 - x3) * (y2 - y1) * (y4 - y3) + y3 * (y2 - y1) * (x4 - x3) - y1 * (y4 - y3) * (x2 - x1)) / parallelValue;

        //判断交点是否是在第一条线段上
        boolean b1 = (x >= x1 && x <= x2) || (x <= x1 && x >= x2);
        boolean b2 = (y >= y1 && y <= y2) || (y <= y1 && y >= y2);
        //判断交点是否是在第二条线段上
        boolean b3 = (x >= x3 && x <= x4) || (x <= x3 && x >= x4);
        boolean b4 = (y >= y3 && y <= y4) || (y <= y3 && y >= y4);

        if (b1 && b2 && b3 && b4) {
            Point point = new Point();
            point.x = (int) x;
            point.y = (int) y;
            return point;
        }
        return null;
    }

    public static ArrayList<Float> getWorldPoint(Point p1) {
        LDMapBean ldMapBean = Robot.get().getMapData();

        ArrayList<Float> wordPoint = new ArrayList<>(2);
        float x = (p1.x * ldMapBean.resolution + ldMapBean.x_min) * 1000;
        float y = ((ldMapBean.height - p1.y) * ldMapBean.resolution + ldMapBean.y_min) * 1000;
        wordPoint.add(x);
        wordPoint.add(y);
        Log.i(TAG, "worldpoint x: " + wordPoint.get(0) + " y: " + wordPoint.get(1));
        return wordPoint;
    }

    public static Point getShowPoint(float x, float y) {
        LDMapBean mapInfo = Robot.get().getMapData();
        Log.i(TAG, "mapResolution : " + mapInfo.resolution);
        Point wordPoint = new Point();
        wordPoint.x = (int) ((x / 1000 - mapInfo.x_min) / mapInfo.resolution);
        wordPoint.y = (int) (mapInfo.height - (y / 1000 - mapInfo.y_min) / mapInfo.resolution);
        Log.i(TAG, "show  x: " + wordPoint.x + " y: " + wordPoint.y);
        return wordPoint;
    }


    public static void restoreBaseMap(Context context,String baseMap) {
        SharedPreferences sp = context.getSharedPreferences("base_map", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("baseMap", baseMap);
        editor.apply();
    }

    public static Long getMapId(Context context) {
        //获得SharedPreferences的实例
        SharedPreferences sp = context.getSharedPreferences("base_map", Context.MODE_PRIVATE);
        Long mapID = sp.getLong("mapID", -1L);
        return mapID;
    }

    public static void restoreMapId(Context context,long mapId) {
        SharedPreferences sp = context.getSharedPreferences("base_map", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("mapID", mapId);
        editor.apply();
    }

    public static void restoreFrameNumber(Context context,int frameNumber) {
        SharedPreferences sp = context.getSharedPreferences("base_map", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("frame_number", frameNumber);
        editor.apply();
    }

    public static int getFrameNumber(Context context) {
        SharedPreferences sp = context.getSharedPreferences("base_map", Context.MODE_PRIVATE);
        int frameNumber = sp.getInt("frame_number", 0);
        return frameNumber;
    }

    public static String getBaseMap(Context context) {
        //获得SharedPreferences的实例
        SharedPreferences sp = context.getSharedPreferences("base_map", Context.MODE_PRIVATE);
        String baseMap = sp.getString("baseMap", null);
        return baseMap;
    }

}

