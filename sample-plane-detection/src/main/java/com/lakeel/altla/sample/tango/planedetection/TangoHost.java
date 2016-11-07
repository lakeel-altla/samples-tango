package com.lakeel.altla.sample.tango.planedetection;

import com.google.atap.tango.ux.TangoUx;
import com.google.atap.tangoservice.Tango;

import com.lakeel.altla.tango.TangoUpdateDispatcher;

public interface TangoHost {

    Tango getTango();

    TangoUx getTangoUx();

    TangoUpdateDispatcher getTangoUpdateDispatcher();
}
