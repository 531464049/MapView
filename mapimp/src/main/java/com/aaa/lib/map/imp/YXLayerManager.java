package com.aaa.lib.map.imp;

import android.util.Log;

import com.aaa.lib.map.LayerManager;
import com.aaa.lib.map.MapUtils;
import com.aaa.lib.map.MapView;
import com.aaa.lib.map.imp.model.AreaBean;
import com.aaa.lib.map.imp.layer.YXAreaLayer;
import com.aaa.lib.map.imp.layer.YXForbiddenAreaLayer;
import com.aaa.lib.map.imp.layer.YXForbiddenLineLayer;
import com.aaa.lib.map.imp.layer.YXPowerLayer;
import com.aaa.lib.map.layer.BaseLayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class YXLayerManager extends LayerManager {
    private static final String TAG = "YXLayerManager";


    public YXLayerManager(MapView mapView) {
        super(mapView);
    }

    /**
     * 重新设置所有区域图层
     *
     * @param yxAreaLayers
     */
    public void setAreaLayer(List<YXAreaLayer> yxAreaLayers) {
        //移除所有区域图层
        Iterator<BaseLayer> it = mLayerList.iterator();
        while (it.hasNext()) {
            if (it.next() instanceof YXAreaLayer) {
                it.remove();
            }
        }
        //添加所有新获取的区域图层
        mLayerList.addAll(yxAreaLayers);
    }

    /**
     * 通过ID判断是否存在
     *
     * @param areaBean
     * @return
     */
    public YXAreaLayer isAreaLayerExist(AreaBean areaBean) {
        for (BaseLayer layer : mLayerList) {
            if (layer instanceof YXAreaLayer && ((YXAreaLayer) layer).getAreaInfo().getAreaID() == areaBean.getAreaID()) {
                return (YXAreaLayer) layer;
            }
        }
        return null;
    }

    /**
     * 获取选择的区域图层
     *
     * @return
     */
    public List<BaseLayer> getSelectedArea() {
        List<BaseLayer> layers = new ArrayList<>();
        for (BaseLayer layer : mLayerList) {
            if (layer instanceof YXAreaLayer && ((YXAreaLayer) layer).isSelected()) {
                layers.add(layer);
                if (!YXAreaLayer.isMultiSelect) {
                    //如果是单选 那么就结束
                    break;
                }
            }
        }
        return layers;
    }

    /**
     * 获取勾选的指定类型的区域图层
     *
     * @param cls
     * @return
     */
    public List<BaseLayer> getSelectedAreaByType(Class cls) {
        List<BaseLayer> layers = new ArrayList<>();
        for (BaseLayer layer : mLayerList) {
            if (layer.getClass() == cls && ((YXAreaLayer) layer).isSelected()) {
                layers.add(layer);
                if (!YXAreaLayer.isMultiSelect) {
                    //如果是单选 那么就结束
                    break;
                }
            }
        }
        return layers;
    }

    /**
     * 获取所有图层
     *
     * @return
     */
    public List<YXAreaLayer> getAllArea() {
        List<YXAreaLayer> layers = new ArrayList<>();
        for (BaseLayer layer : mLayerList) {
            if (layer.getClass() == YXAreaLayer.class) {
                layers.add((YXAreaLayer) layer);
            }
        }
        return layers;
    }

    /**
     * 获取指定类型的图层
     *
     * @param cls
     * @return
     */
    public List<BaseLayer> getLayerByType(Class cls) {
        List<BaseLayer> layers = new ArrayList<>();
        for (BaseLayer layer : mLayerList) {
            if (layer instanceof YXAreaLayer) {
                layers.add(layer);
            }
        }
        return layers;
    }

    /**
     * 判断禁区是否与电源保护区域相交
     *
     * @param powerLayer
     * @return
     */
    public boolean isPowerCrossForbidArea(YXPowerLayer powerLayer) {
        for (BaseLayer layer : mLayerList) {
            //判断禁区是否与电源保护区域相交
            if (layer instanceof YXForbiddenAreaLayer) {
                if (MapUtils.isAreaCrossPower(((YXForbiddenAreaLayer) layer).getArea(), powerLayer.getArea())) {
                    return true;
                }
            }
            //判断禁过线是否与电源保护区域相交
            if (layer instanceof YXForbiddenLineLayer) {
                if (MapUtils.isLineCrossPower(((YXForbiddenLineLayer) layer).getArea(), powerLayer.getArea())) {
                    return true;
                }
            }

        }
        return false;
    }

    /**
     * 判断禁区是否改变
     *
     * @return
     */
    public boolean isForbiddenAreaChange() {
        for (BaseLayer layer : mLayerList) {
            if (layer instanceof YXForbiddenAreaLayer) {
                if (((YXForbiddenAreaLayer) layer).isChanged()) {
                    Log.i(TAG,"isForbiddenAreaChange Area");
                    return true;
                }
            }
            if (layer instanceof YXForbiddenLineLayer) {
                if (((YXForbiddenLineLayer) layer).isChanged()) {
                    Log.i(TAG,"isForbiddenAreaChange Line");
                    return true;
                }
            }
        }
        return false;
    }

}

