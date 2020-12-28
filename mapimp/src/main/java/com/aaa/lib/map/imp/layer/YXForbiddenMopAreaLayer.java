package com.aaa.lib.map.imp.layer;

import com.aaa.lib.map.MapView;
import com.aaa.lib.map.imp.R;

public class YXForbiddenMopAreaLayer extends YXForbiddenAreaLayer {
    private static final String TAG = "YXForbiddenLineLayer";

    public YXForbiddenMopAreaLayer(MapView mapView) {
        super(mapView);
        mPaint.setColor(mResource.getColor(R.color.restrict_area_no_mop_stroke));
    }
}
