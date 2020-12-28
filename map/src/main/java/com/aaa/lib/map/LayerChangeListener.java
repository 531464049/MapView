package com.aaa.lib.map;

import com.aaa.lib.map.layer.BaseLayer;

public interface LayerChangeListener {
    void onRemove(BaseLayer layer);

    void onMove(BaseLayer layer);

    void onRotate(BaseLayer layer);

    void onScale(BaseLayer layer);

    void onAdd(BaseLayer layer);
}
