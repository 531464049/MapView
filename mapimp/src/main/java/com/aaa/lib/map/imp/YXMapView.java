package com.aaa.lib.map.imp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;

import com.aaa.lib.map.MapView;
import com.aaa.lib.map.MatrixUtil;
import com.aaa.lib.map.imp.layer.YXAreaDivideLineLayer;
import com.aaa.lib.map.imp.layer.YXAreaLayer;
import com.aaa.lib.map.imp.layer.YXCleanAreaLayer;
import com.aaa.lib.map.imp.layer.YXForbiddenAreaLayer;
import com.aaa.lib.map.imp.layer.YXForbiddenLineLayer;
import com.aaa.lib.map.imp.layer.YXForbiddenMopAreaLayer;
import com.aaa.lib.map.imp.layer.YXImageMarkerLayer;
import com.aaa.lib.map.imp.layer.YXPathLayer;
import com.aaa.lib.map.imp.layer.YXPointAroundAreaLayer;
import com.aaa.lib.map.imp.layer.YXPowerLayer;
import com.aaa.lib.map.imp.layer.YXRoomTagLayer;
import com.aaa.lib.map.imp.model.LDAreaBean;
import com.aaa.lib.map.imp.model.LDMapBean;
import com.aaa.lib.map.imp.model.LDPathBean;
import com.aaa.lib.map.layer.MapLayer;
import com.aaa.lib.map.layer.YXAreaDivideLineLayer;
import com.aaa.lib.map.layer.YXCleanAreaLayer;
import com.aaa.lib.map.layer.YXForbiddenAreaLayer;
import com.aaa.lib.map.layer.YXForbiddenLineLayer;
import com.aaa.lib.map.layer.YXForbiddenMopAreaLayer;
import com.aaa.lib.map.layer.YXImageMarkerLayer;
import com.aaa.lib.map.layer.YXPathLayer;
import com.aaa.lib.map.layer.YXPointAroundAreaLayer;
import com.aaa.lib.map.layer.YXPowerLayer;
import com.aaa.lib.map.layer.YXRoomTagLayer;

import java.util.ArrayList;
import java.util.List;

public class YXMapView extends MapView<YXLayerManager> {
    private static final String TAG = "YXMapView";


    //各种图层
    private MapLayer mapLayer;
    private YXPathLayer pathLayer;
    private YXImageMarkerLayer deviceLayer;
    private YXPowerLayer powerLayer;
    private List<YXForbiddenAreaLayer> forbiddenAreaLayers;
    private List<YXForbiddenLineLayer> forbiddenLineLayers;
    private List<YXForbiddenMopAreaLayer> forbiddenMopAreaLayers;
    private List<YXCleanAreaLayer> cleanAreaLayers;
    private List<YXRoomTagLayer> roomTagLayers;
    private List<YXPointAroundAreaLayer> locationLayers;
    private volatile YXAreaDivideLineLayer areaDivideLineLayer;

    private YXMapViewRefreshHelper refreshHelper;


    public YXMapView(Context context) {
        this(context, null);
    }

    public YXMapView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YXMapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mLayerManager = new YXLayerManager(this);
        refreshHelper = new YXMapViewRefreshHelper(this);
        Log.i(TAG, "YXMapView init");
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                float width = (float) getWidth();
                float height = (float) getHeight();
                Log.i(TAG, "YXMapView onGlobalLayout " + width + " height : " + height);
                if (width != 0 && height != 0) {
                    refreshMap(Robot.get().getMapData(), Robot.get().getPathData());
                }
            }
        });
        initLayer();
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        refreshHelper.open();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        refreshHelper.close();
    }

    private void initLayer() {
        //初始化mapview  添加layer
        LDMapBean ldMapBean = Robot.get().getMapData();
        LDPathBean ldPathBean = Robot.get().getPathData();
        LDAreaBean ldAreaBean = Robot.get().getAreaData();

        //添加地图
        mapLayer = new MapLayer(this);
        mLayerManager.addLayer(mapLayer);
        refreshMap(ldMapBean, ldPathBean);

        //添加路径
        pathLayer = new YXPathLayer(this);
        mLayerManager.addLayer(pathLayer);
        refreshPath(ldMapBean, ldPathBean);

        //添加电源图层
        powerLayer = new YXPowerLayer(this);
        powerLayer.initPowerLayer((float) ldMapBean.dockerPosX, (float) ldMapBean.dockerPosY, 0.5f, 0);
        mLayerManager.addLayer(powerLayer);

        //添加扫地机
        Bitmap sweeperBitmap = BitmapFactory.decodeResource(getResources(), com.aaa.lib.map.R.mipmap.robot_inmap);
        deviceLayer = new YXImageMarkerLayer(this, sweeperBitmap);
        deviceLayer.setMarker(YXCoordinateConverter.devicePosX, YXCoordinateConverter.devicePosY, 0);
        mLayerManager.addLayer(deviceLayer);

        //设置区域信息
        initAllArea();
        refreshArea(ldAreaBean.getAreaList());
    }

    public void refreshMap(LDMapBean ldMapBean, LDPathBean ldPathBean) {
        Bitmap mapBitmap = Render.renderMap(ldMapBean, ldPathBean);
        MatrixUtil.loadMap(mapBitmap, this);
        mapLayer.setMapBitmap(mapBitmap);
        refresh();
    }

    public void refreshPath(LDMapBean ldMapBean, LDPathBean ldPathBean) {
        Bitmap pathBitmap = Render.renderPath(ldMapBean, ldPathBean);
        this.pathLayer.setPathBitmap(pathBitmap);
        refresh();
    }

    public void refreshArea(List<AreaBean> areaList) {
        //移除所有区域图层
        clearAllArea();
        Log.i(TAG, areaList.toString());
        List<YXAreaLayer> newAreaLayerList = new ArrayList<>();
        for (AreaBean areaBean : areaList) {
//            YXAreaLayer layer = isLayerExist(areaBean);  判断图层是否存在 如果图层不存在就新建
            if (areaBean.getType() == YXAreaLayer.TYPE_CLEAN_AREA) {
                YXCleanAreaLayer layer = new YXCleanAreaLayer(this);
                layer.initAreaLayer(areaBean);
                cleanAreaLayers.add(layer);
            } else if (areaBean.getType() == YXAreaLayer.TYPE_FORBIDDEN_AREA) {
                YXForbiddenAreaLayer layer = new YXForbiddenAreaLayer(this);
                layer.initAreaLayer(areaBean);
                forbiddenAreaLayers.add(layer);
            } else if (areaBean.getType() == YXAreaLayer.TYPE_FORBIDDEN_LINE) {
                YXForbiddenLineLayer layer = new YXForbiddenLineLayer(this);
                layer.initAreaLayer(areaBean);
                forbiddenLineLayers.add(layer);
            } else if (areaBean.getType() == YXAreaLayer.TYPE_LOCATION) {
                YXPointAroundAreaLayer layer = new YXPointAroundAreaLayer(this);
                layer.initAreaLayer(areaBean);
                locationLayers.add(layer);
            } else if (areaBean.getType() == YXAreaLayer.TYPE_ROOM) {
                YXRoomTagLayer layer = new YXRoomTagLayer(this);
                layer.initAreaLayer(areaBean);
                roomTagLayers.add(layer);
            } else if (areaBean.getType() == YXAreaLayer.TYPE_FORBIDDEN_MOP) {
                YXForbiddenMopAreaLayer layer = new YXForbiddenMopAreaLayer(this);
                layer.initAreaLayer(areaBean);
                forbiddenMopAreaLayers.add(layer);
            } else {
                YXAreaLayer layer = new YXAreaLayer(this);
                layer.initAreaLayer(areaBean);
            }
        }
        newAreaLayerList.addAll(cleanAreaLayers);
        newAreaLayerList.addAll(forbiddenAreaLayers);
        newAreaLayerList.addAll(forbiddenLineLayers);
        newAreaLayerList.addAll(forbiddenMopAreaLayers);
        newAreaLayerList.addAll(locationLayers);
        newAreaLayerList.addAll(roomTagLayers);

        mLayerManager.setAreaLayer(newAreaLayerList);
        refresh();
    }


    public void refreshSweeper(PointF position, float direction) {
        this.deviceLayer.setMarker(position, direction);
        refresh();
    }

    /**
     * 刷新电源
     *
     * @param position  电源位置
     * @param direction 电源朝向
     */
    public void refreshPower(PointF position, float direction) {
        refreshPower(position.x, position.y, direction);
    }

    public void refreshPower(float centerX, float centerY, float rotation) {
        powerLayer.setCenter(centerX, centerY);
        powerLayer.setRotation(rotation);
        refresh();
    }

    /**
     * 添加区域分割线
     * 限制只能添加一个
     */
    public void addAreaDivideLineLayer() {
        if (areaDivideLineLayer != null) {
            areaDivideLineLayer = new YXAreaDivideLineLayer(this);
            areaDivideLineLayer.setLine(new PointF(200, getHeight() / 2), new PointF(getWidth() / 2, getHeight() / 2));
            mLayerManager.addLayer(areaDivideLineLayer);
        }
    }

    /**
     * 移除区域分割线
     */
    public void removeAreaDivideLineLayer() {
        mLayerManager.removeLayer(areaDivideLineLayer);
        areaDivideLineLayer = null;
    }

    /**
     * 设置房间标签是否可见
     *
     * @param visible 可见性
     */
    public void setRoomTagMode(boolean visible, boolean canSelect, boolean showOrder) {
        YXRoomTagLayer.setMode(visible, canSelect, showOrder);
        refresh();
    }

    /**
     * 设置禁区是否可编辑
     *
     * @param editable 可编辑
     */
    public void setForbiddenAreaEditable(boolean editable) {
        for (YXAreaLayer areaLayer : forbiddenAreaLayers) {
            if (areaLayer instanceof YXForbiddenAreaLayer) {
                areaLayer.setEditMode(editable);
            }
        }
    }

    /**
     * 设置禁过线是否可编辑
     *
     * @param editable 可编辑
     */
    public void setForbiddenLineEditable(boolean editable) {
        for (YXAreaLayer areaLayer : forbiddenAreaLayers) {
            if (areaLayer instanceof YXForbiddenLineLayer) {
                areaLayer.setEditMode(editable);
            }
        }
    }

    /**
     * 设置自定义区域清扫是否可编辑
     *
     * @param editable 可编辑
     */
    public void setCleanAreaEditable(boolean editable) {
        for (BaseLayer areaLayer : cleanAreaLayers) {
            ((YXCleanAreaLayer) areaLayer).setEditMode(editable);
        }
    }

    /**
     * 是否显示电源保护区域
     *
     * @param show 显示/不显示
     */
    public void showPowerProtectArea(boolean show) {
        powerLayer.setShowProtectArea(show);
    }

    public void updateMap(ParseResult result) {
        //解析地图数据
        refreshHelper.updateMap(result);
    }

    public void updatePath(ParseResult<PathParseResult> parseResult) {
        refreshHelper.updatePath(parseResult);
    }

    public void updateArea(ParseResult parseResult) {
        refreshHelper.updateArea(parseResult);
    }


    private void initAllArea() {
        forbiddenAreaLayers = new ArrayList<>();
        forbiddenLineLayers = new ArrayList<>();
        forbiddenMopAreaLayers = new ArrayList<>();
        cleanAreaLayers = new ArrayList<>();
        locationLayers = new ArrayList<>();
        roomTagLayers = new ArrayList<>();
    }

    private void clearAllArea() {
        cleanAreaLayers.clear();
        forbiddenAreaLayers.clear();
        forbiddenLineLayers.clear();
        forbiddenMopAreaLayers.clear();
        roomTagLayers.clear();
        locationLayers.clear();
    }

    public List getAreaLayerListByType(Class cls) {
        if (cls == YXForbiddenAreaLayer.class) {
            return forbiddenAreaLayers;
        } else if (cls == YXForbiddenLineLayer.class) {
            return forbiddenLineLayers;
        } else if (cls == YXForbiddenMopAreaLayer.class) {
            return forbiddenMopAreaLayers;
        } else if (cls == YXPointAroundAreaLayer.class) {
            return locationLayers;
        } else if (cls == YXRoomTagLayer.class) {
            return roomTagLayers;
        } else {
            return null;
        }
    }

    public void addAreaLayer(YXAreaLayer layer) {
        if (layer instanceof YXForbiddenMopAreaLayer) {
            forbiddenMopAreaLayers.add((YXForbiddenMopAreaLayer) layer);
        } else if (layer instanceof YXForbiddenAreaLayer) {
            forbiddenAreaLayers.add((YXForbiddenAreaLayer) layer);
        } else if (layer instanceof YXForbiddenLineLayer) {
            forbiddenLineLayers.add((YXForbiddenLineLayer) layer);
        } else if (layer instanceof YXPointAroundAreaLayer) {
            locationLayers.add((YXPointAroundAreaLayer) layer);
        } else if (layer instanceof YXRoomTagLayer) {
            roomTagLayers.add((YXRoomTagLayer) layer);
        } else if (layer instanceof YXCleanAreaLayer) {
            cleanAreaLayers.add((YXCleanAreaLayer) layer);
        }

        mLayerManager.addLayer(layer);
    }


    public int getUniqueAreaId(int type) {
        int startID = 0;
        if (type == YXAreaLayer.TYPE_CLEAN_AREA) {
            startID = 200;
            return loopToGetUniqueAreaId(cleanAreaLayers, startID);
        } else if (type == YXAreaLayer.TYPE_FORBIDDEN_AREA) {
            startID = 300;
            return loopToGetUniqueAreaId(forbiddenAreaLayers, startID);
        } else if (type == YXAreaLayer.TYPE_FORBIDDEN_LINE) {
            startID = 400;
            return loopToGetUniqueAreaId(forbiddenLineLayers, startID);
        }
        return 0;
    }

    private int loopToGetUniqueAreaId(List<? extends YXAreaLayer> layers, int startId) {
        while (isIdExist(layers, startId)) {
            startId++;
        }
        return startId;
    }

    private boolean isIdExist(List<? extends YXAreaLayer> layers, int id) {
        for (YXAreaLayer areaLayer : layers) {
            if (id == areaLayer.getAreaInfo().getAreaID()) {
                return true;
            }
        }
        return false;
    }

    public boolean isPowerInLayer() {
        return mLayerManager.isPowerInLayer(powerLayer);
    }

    public YXPowerLayer getPowerLayer() {
        return powerLayer;
    }

    public List<YXForbiddenAreaLayer> getForbiddenAreaLayers() {
        return forbiddenAreaLayers;
    }

    public List<YXForbiddenLineLayer> getForbiddenLineLayers() {
        return forbiddenLineLayers;
    }

    public List<YXForbiddenMopAreaLayer> getForbiddenMopAreaLayers() {
        return forbiddenMopAreaLayers;
    }

    public List<YXCleanAreaLayer> getCleanAreaLayers() {
        return cleanAreaLayers;
    }

    public List<YXRoomTagLayer> getRoomTagLayers() {
        return roomTagLayers;
    }

    public YXAreaDivideLineLayer getAreaDivideLineLayer() {
        return areaDivideLineLayer;
    }

    public List<YXRoomTagLayer> getSelectedRoomTag() {
        List<YXRoomTagLayer> selectedRoom = new ArrayList<>();
        for (YXRoomTagLayer layer : roomTagLayers) {
            if (layer.isSelected()) {
                selectedRoom.add(layer);
            }
        }
        return selectedRoom;
    }

    @Override
    public void clearMap() {
        super.clearMap();
        clearAllArea();
    }
}
