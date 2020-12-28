package com.aaa.lib.map.imp.layer;

import android.graphics.Canvas;

import com.aaa.lib.map.MapView;
import com.aaa.lib.map.area.CircleArea;
import com.aaa.lib.map.imp.model.AreaBean;

import java.util.List;

public class YXPointAroundAreaLayer extends YXAreaLayer<CircleArea> {
    public YXPointAroundAreaLayer(MapView mapView) {
        super(mapView);
        area = new CircleArea();
    }

    @Override
    public void draw(Canvas canvas) {
        //定点清扫 暂无绘制
        return;
    }

    @Override
    public void initAreaLayer(AreaBean areaBean) {
        List<double[]> areaVetexs = areaBean.getPoints();
        area.setCenter((float)areaVetexs.get(0)[0], (float)areaVetexs.get(0)[1]);
        setAreaInfo(areaBean);
    }
}
