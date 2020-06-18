package com.angopapo.datoo.modules.charting.interfaces.dataprovider;

import com.angopapo.datoo.modules.charting.data.BubbleData;

public interface BubbleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    BubbleData getBubbleData();
}
