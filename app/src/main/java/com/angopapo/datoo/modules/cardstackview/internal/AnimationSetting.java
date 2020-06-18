package com.angopapo.datoo.modules.cardstackview.internal;

import android.view.animation.Interpolator;

import com.angopapo.datoo.modules.cardstackview.Direction;


public interface AnimationSetting {
    Direction getDirection();
    int getDuration();
    Interpolator getInterpolator();
}
