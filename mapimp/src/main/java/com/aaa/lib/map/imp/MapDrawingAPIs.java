package com.aaa.lib.map.imp;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * Created by Administrator on 2018/9/12 0012.
 */

public class MapDrawingAPIs {
    private final static String TAG = "MapDrawingAPIs";

    static {
        try {
            System.loadLibrary("MapDrawingAPIs");
            System.err.println("load MapDrawingAPIs lib success!");
        } catch (UnsatisfiedLinkError ule) {
            Log.d("ostest", ule.getMessage());
            System.err.println("MapDrawingAPIs lib load err!");
        }
    }

    public native static Bitmap Geo(int[] data, int width, int height, int[] path_x, int[] path_y, int len, float x0, float y0, float f, boolean hasPower, Bitmap bitmapInfo);

    public native static Bitmap Path(int width, int height, int[] path_x, int[] path_y, int len, float x0, float y0, float f, boolean hasPower, Bitmap bitmapInfo);

    public native static int[] getPathPoint();

    public native static int[][] getContoursPointsX();

    public native static int[][] getContoursPointsY();

    public native static int[][] getBorderPath(int[] data, int width, int height, int roomTag);
    //解压并翻转
    public native static String decompressAndFlip(String src, int w, int h, int length);
    //将未解压和翻转的增量数据直接添加到 已解压翻转的基础数据上
    public native static String increasingMap(String src, int w, int h, String increase, int length, int newW, int newH);
    //解码地图成1112222000 这样的格式
    public native static String rawMap(String src, int w, int h);

    //全量加增量融合后显示(012)  结果显示
    //public native static String mapDecodeDiffBindRestore_Map(String base_map, int w, int h);


    private static int ASCII_DIFF = 0x2e;

    public static char[] pixels_split(String mapSrc, int w, int h) {
        char[] src = mapSrc.toCharArray();



        char[] des = new char[w * h + 4];

        int wr = w % 4;
        int wi = ((wr == 0) ? w / 4 : w / 4 + 1);
        int i = 0, j = 0;
        int high = 0, low = 0;
        int high_map = 0, low_map = 0;

        for (i = 0; i < h; i++) {
            for (j = 0; j < wi; j++) {
                high = (src[wi * i + j] - ASCII_DIFF) / 9;
                low = (src[wi * i + j] - ASCII_DIFF) % 9;

                if (high <= 2) {
                    high_map = high;
                } else if (high > 2 && high <= 5) {
                    high_map = high + 1;
                } else if (high > 5 && high <= 8) {
                    high_map = high + 2;
                }
                if (low <= 2) {
                    low_map = low;
                } else if (low > 2 && low <= 5) {
                    low_map = low + 1;
                } else if (low > 5 && low <= 8) {
                    low_map = low + 2;
                }
                des[w * i + j * 4 + 0] = (char) ((high_map >> 2 & 0x3) + 0x30);
                des[w * i + j * 4 + 1] = (char) ((high_map >> 0 & 0x3) + 0x30);
                des[w * i + j * 4 + 2] = (char) ((low_map >> 2 & 0x3) + 0x30);
                des[w * i + j * 4 + 3] = (char) ((low_map >> 0 & 0x3) + 0x30);

            }

            if (wr == 3) {
                high = (src[wi * i + wi] - ASCII_DIFF) % 9;
                low = (src[wi * i + wi] - ASCII_DIFF) / 9;

                if (high <= 2) {
                    high_map = high;
                } else if (high > 2 && high <= 5) {
                    high_map = high + 1;
                } else if (high > 5 && high <= 8) {
                    high_map = high + 2;
                }
                if (low <= 2) {
                    low_map = low;
                } else if (low > 2 && low <= 5) {
                    low_map = low + 1;
                } else if (low > 5 && low <= 8) {
                    low_map = low + 2;
                }
                des[w * i + w - 3] = (char) ((high_map >> 2 & 0x3) + 0x30);
                des[w * i + w - 2] = (char) ((high_map >> 0 & 0x3) + 0x30);
                des[w * i + w - 1] = (char) ((low_map >> 2 & 0x3) + 0x30);
            }

            if (wr == 2) {
                high = (src[wi * i + wi] - ASCII_DIFF) % 9;

                if (high <= 2) {
                    high_map = high;
                } else if (high > 2 && high <= 5) {
                    high_map = high + 1;
                } else if (high > 5 && high <= 8) {
                    high_map = high + 2;
                }
                des[w * i + w - 2] = (char) ((high_map >> 2 & 0x3) + 0x30);
                des[w * i + w - 1] = (char) ((high_map >> 0 & 0x3) + 0x30);
            }

            if (wr == 1) {
                high = (src[wi * i + wi] - ASCII_DIFF) % 9;

                if (high <= 2) {
                    high_map = high;
                } else if (high > 2 && high <= 5) {
                    high_map = high + 1;
                } else if (high > 5 && high <= 8) {
                    high_map = high + 2;
                }
                des[w * i + w - 1] = (char) ((high_map >> 2 & 0x3) + 0x30);
            }

            if (i != h - 1) {
                des[w * i + w] = '\0';
                Log.i(TAG, " : wwww---vvvv---: " + des.length);
            }
        }
        return des;
    }


}
