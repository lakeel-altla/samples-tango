package com.lakeel.altla.sample.tango.streamingtexture;

import com.google.atap.tango.ux.TangoUx;
import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoConfig;

import com.lakeel.altla.tango.TangoUpdateDispatcher;
import com.projecttango.tangosupport.TangoSupport;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public final class MainActivity extends AppCompatActivity implements TangoHost {

    private Tango mTango;

    private TangoUx mTangoUx;

    private TangoUpdateDispatcher mTangoUpdateDispatcher = new TangoUpdateDispatcher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTango = new Tango(this);
        mTangoUx = new TangoUx(this);

        MainFragment fragment = MainFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                                   .replace(R.id.fragment_container, fragment)
                                   .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        TangoConfig config = mTango.getConfig(TangoConfig.CONFIG_TYPE_DEFAULT);
        // NOTE: Low latency integration is necessary to achieve a precise alignment of
        // virtual objects with the RBG image and produce a good AR effect.
        config.putBoolean(TangoConfig.KEY_BOOLEAN_LOWLATENCYIMUINTEGRATION, true);
        config.putBoolean(TangoConfig.KEY_BOOLEAN_COLORCAMERA, true);

        mTango.connectListener(null, mTangoUpdateDispatcher);

        TangoUx.StartParams startParams = new TangoUx.StartParams();
        startParams.showConnectionScreen = false;
        mTangoUx.start(startParams);

        mTango.connect(config);
        TangoSupport.initialize();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTango.disconnect();
        mTangoUx.stop();
    }

    @Override
    public Tango getTango() {
        return mTango;
    }

    @Override
    public TangoUx getTangoUx() {
        return mTangoUx;
    }

    @Override
    public TangoUpdateDispatcher getTangoUpdateDispatcher() {
        return mTangoUpdateDispatcher;
    }
}
