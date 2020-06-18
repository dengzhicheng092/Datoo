package com.angopapo.datoo.modules.charting.interfaces.dataprovider;

import com.angopapo.datoo.modules.charting.data.ScatterData;

public interface ScatterDataProvider extends BarLineScatterCandleBubbleDataProvider {

    ScatterData getScatterData();
}
