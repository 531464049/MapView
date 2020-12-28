package com.aaa.lib.map.imp;

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

    private boolean isLayerListChanged = false;

    public YXLayerManager(MapView mapView) {
        super(mapView);
        setLayerListChangeListener(new LayerListChangeListener() {
            @Override
            public void onLayerAdd(BaseLayer layer) {
                isLayerListChanged = true;
            }

            @Override
            public void onLayerRemove(BaseLayer layer) {
                isLayerListChanged = true;
            }
        });
    }

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

    public YXAreaLayer isAreaLayerExist(AreaBean areaBean) {
        for (BaseLayer layer : mLayerList) {
            if (layer instanceof YXAreaLayer && ((YXAreaLayer) layer).getAreaInfo().getAreaID() == areaBean.getAreaID()) {
                return (YXAreaLayer) layer;
            }
        }
        return null;
    }

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

    public List<YXAreaLayer> getAllArea() {
        List<YXAreaLayer> layers = new ArrayList<>();
        for (BaseLayer layer : mLayerList) {
            if (layer.getClass() == YXAreaLayer.class) {
                layers.add((YXAreaLayer) layer);
            }
        }
        return layers;
    }

    public List<BaseLayer> getLayerByType(Class cls) {
        List<BaseLayer> layers = new ArrayList<>();
        for (BaseLayer layer : mLayerList) {
            if (layer instanceof YXAreaLayer) {
                layers.add(layer);
            }
        }
        return layers;
    }

    public boolean isPowerInLayer(YXPowerLayer powerLayer) {
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

    public boolean isForbiddenAreaChange() {
        if (isLayerListChanged) {
            return true;
        }

        for (BaseLayer layer : mLayerList) {
            if (layer instanceof YXForbiddenAreaLayer) {
                if (((YXForbiddenAreaLayer) layer).isChanged()) {
                    return true;
                }
            }
            if (layer instanceof YXForbiddenLineLayer) {
                if (((YXForbiddenLineLayer) layer).isChanged()) {
                    return true;
                }
            }
        }
        return false;
    }

}
