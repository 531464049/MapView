package com.aaa.lib.map.imp;

import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.aaa.lib.map.imp.model.AreaBean;
import com.aaa.lib.map.imp.model.LDMapBean;
import com.aaa.lib.map.imp.model.LDPathBean;
import com.aaa.lib.map.imp.model.Robot;
import com.aaa.lib.map.imp.parser.ParseResult;
import com.aaa.lib.map.imp.parser.PathParseResult;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class YXMapViewRefreshHelper {

    private static final String TAG = "YXMapViewRefreshHelper";
    private static final int MSG_REQUEST_PATH = 1;
    private static final int MSG_REQUEST_MAP = 2;
    private static final int MSG_REFRESH_PATH = 3;


    //线程和锁
    private volatile boolean refreshMap = false;
    private ThreadPoolExecutor threadPoolExecutor;
    private volatile Lock refreshMapLock;
    private volatile Condition mapDataUpdate;
    private volatile Lock refreshPathLock;
    private volatile Condition pathDataUpdate;
    private volatile Lock refreshAreaLock;
    private volatile Condition areaInfoUpdate;
    private volatile boolean drawMap = false;
    private volatile boolean drawPath = false;
    private volatile boolean drawArea = false;
    private ViewHandler handler;

    private YXMapView mapView;

    public YXMapViewRefreshHelper(YXMapView mapView) {
        handler = new ViewHandler(this);
        this.mapView = mapView;
    }


    public static class ViewHandler extends Handler {
        SoftReference<YXMapViewRefreshHelper> mainActivitySoftReference;

        public ViewHandler(YXMapViewRefreshHelper context) {
            mainActivitySoftReference = new SoftReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_REQUEST_PATH) {
                if (mainActivitySoftReference.get() != null) {
                    mainActivitySoftReference.get().requestPath();
                }
            }
            if (msg.what == MSG_REFRESH_PATH) {
                if (mainActivitySoftReference.get() != null) {
                    mainActivitySoftReference.get().updatePath(new ParseResult<PathParseResult>(ParseResult.MSG_PARSE_SUCCESS));
                }
            }
            if (msg.what == MSG_REQUEST_MAP) {
                if (mainActivitySoftReference.get() != null) {
//                    TuyaConnector.requestMapData();
                }
            }
        }
    }


    public void updateMap(ParseResult result) {
        //解析地图数据
        refreshMapLock.lock();

        //数据不为空  解析并刷新地图
        if (result.getCode() == ParseResult.MSG_PARSE_SUCCESS) {
            //data parse success  , next
            drawMap = true;
            mapDataUpdate.signal();
        } else if (result.getCode() == ParseResult.MSG_PARSE_FAIL_DATA_NOT_MATCH) {
            handler.removeMessages(MSG_REQUEST_MAP);
            handler.sendEmptyMessageDelayed(MSG_REQUEST_MAP, 2000);
            drawMap = false;
        } else {
            //something error
            drawMap = false;
        }

        refreshMapLock.unlock();
    }

    public void updatePath(ParseResult<PathParseResult> parseResult) {
        refreshPathLock.lock();

        //解析路径数据
        if (parseResult.getCode() == ParseResult.MSG_PARSE_FAIL_DATA_ERROR) {
            drawPath = false;
        } else if (parseResult.getCode() == ParseResult.MSG_PARSE_FAIL_DATA_NOT_MATCH) {
            //虽然缺少了数据 但是还是继续渲染， 缺的再去请求
//            TuyaConnector.requestPathData(parseResult.getResult().getPosFrom(), parseResult.getResult().getPosTo());
            drawPath = true;
            pathDataUpdate.signal();
        } else {
            drawPath = true;
            pathDataUpdate.signal();
            //success
        }

        refreshPathLock.unlock();
    }

    public void updateArea(ParseResult parseResult) {
        Log.i(TAG, "updateArea");
        refreshAreaLock.lock();
        //解析路径数据
        if (parseResult.getCode() == ParseResult.MSG_PARSE_SUCCESS) {
            drawArea = true;
            areaInfoUpdate.signal();
        } else {
            drawArea = false;
        }
        refreshAreaLock.unlock();
    }

    public void open() {
        //初始化线程和锁
        threadPoolExecutor = new ThreadPoolExecutor(3, 3, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
        refreshMapLock = new ReentrantLock();
        mapDataUpdate = refreshMapLock.newCondition();
        refreshPathLock = new ReentrantLock();
        pathDataUpdate = refreshPathLock.newCondition();
        refreshAreaLock = new ReentrantLock();
        areaInfoUpdate = refreshAreaLock.newCondition();

        refreshMap = true;
        // 绘制地图线程
        threadPoolExecutor.execute(runnableMap);
        //绘制路径线程
        threadPoolExecutor.execute(runnablePath);
        //绘制区域信息线程
        threadPoolExecutor.execute(runnableArea);
    }

    public void close() {
        refreshMap = false;
        updateMap(new ParseResult<>(ParseResult.MSG_PARSE_SUCCESS));
        updatePath(new ParseResult<PathParseResult>(ParseResult.MSG_PARSE_SUCCESS));
        updateArea(new ParseResult<>(ParseResult.MSG_PARSE_SUCCESS));
    }

    /**
     * 绘制地图线程
     */
    private Runnable runnableMap = new Runnable() {
        @Override
        public void run() {
            //TODO 这里无法停止 使用handlerThread优化
            while (refreshMap) {
                Log.i(TAG, "update map  " + drawMap);
                refreshMapLock.lock();
                if (drawMap) {
                    //解析地图数据
                    LDMapBean ldMapBean = Robot.get().getMapData();
                    LDPathBean ldPathBean = Robot.get().getPathData();

                    //刷新地图
                    mapView.refreshMap(ldMapBean, ldPathBean);

                    //刷新充电桩
                    if (ldMapBean.dockerPosX != 0 && ldMapBean.dockerPosY != 0) {
                        mapView.refreshPower(YXCoordinateConverter.getScreenPointFromOrigin(ldMapBean.dockerPosX, ldMapBean.dockerPosY), 0);
                    }

                    handler.sendEmptyMessage(MSG_REFRESH_PATH);
                    drawMap = false;
                } else {
                    try {
                        mapDataUpdate.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                refreshMapLock.unlock();
            }
        }
    };


    /**
     * 绘制路径线程
     */
    private Runnable runnablePath = new Runnable() {

        @Override
        public void run() {
            while (refreshMap) {
                refreshPathLock.lock();
                Log.i(TAG, "update path " + drawPath);
                if (drawPath) {
                    LDMapBean ldMapBean = Robot.get().getMapData();
                    LDPathBean ldPathBean = Robot.get().getPathData();

                    //刷新路径
                    mapView.refreshPath(ldMapBean, ldPathBean);

                    //刷新当前位置
                    if (YXCoordinateConverter.devicePosX != 0 && YXCoordinateConverter.devicePosY != 0) {
                        mapView.refreshSweeper(new PointF(YXCoordinateConverter.devicePosX, YXCoordinateConverter.devicePosY), 0);
                    }

                    drawPath = false;
                } else {
                    try {
                        pathDataUpdate.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                refreshPathLock.unlock();
            }
        }
    };

    /**
     * 绘制路径线程
     */
    private Runnable runnableArea = new Runnable() {

        @Override
        public void run() {
            while (refreshMap) {
                refreshAreaLock.lock();
                Log.i(TAG, "updateArea " + drawArea);
                if (drawArea) {
                    Log.i(TAG, "updateArea 11111111111 ");
                    ArrayList<AreaBean> areaList = Robot.get().getAreaData().getAreaList();
                    mapView.refreshArea(areaList);
                    drawArea = false;
                } else {
                    try {
                        Log.i(TAG, "updateArea waite ");
                        areaInfoUpdate.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                refreshAreaLock.unlock();
            }
        }
    };

    private void requestPath() {
//        TuyaConnector.requestPathInfo();
        handler.removeMessages(MSG_REQUEST_PATH);
        handler.sendEmptyMessageDelayed(MSG_REQUEST_PATH, 2000);
    }
}
