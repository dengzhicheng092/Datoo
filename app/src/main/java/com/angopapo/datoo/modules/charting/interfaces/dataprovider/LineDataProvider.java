package com.angopapo.datoo.modules.charting.interfaces.dataprovider;

import com.angopapo.datoo.modules.charting.components.YAxis;
import com.angopapo.datoo.modules.charting.data.LineData;

public interface LineDataProvider extends BarLineScatterCandleBubbleDataProvider {

    LineData getLineData();

    YAxis getAxis(YAxis.AxisDependency dependency);
}
