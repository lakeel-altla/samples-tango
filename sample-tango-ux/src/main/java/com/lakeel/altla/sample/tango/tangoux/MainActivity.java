package com.lakeel.altla.sample.tango.tangoux;

import com.google.atap.tango.ux.TangoUx;
import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoConfig;
import com.google.atap.tangoservice.TangoCoordinateFramePair;
import com.google.atap.tangoservice.TangoPoseData;

import com.lakeel.altla.tango.TangoUpdateDispatcher;
import com.projecttango.tangosupport.TangoSupport;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TangoHost {

    private Tango mTango;

    private TangoUx mTangoUx;

    private TangoUpdateDispatcher mTangoUpdateDispatcher = new TangoUpdateDispatcher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTango = new Tango(this);
        mTangoUx = new TangoUx(this);

        mTangoUpdateDispatcher.getOnPoseAvailableListeners().add(pose -> mTangoUx.updatePoseStatus(pose.statusCode));
        mTangoUpdateDispatcher.getOnTangoEventListeners().add(event -> mTangoUx.updateTangoEvent(event));

        MainFragment fragment = MainFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                                   .replace(R.id.fragment_container, fragment)
                                   .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // NOTE:
        //
        // TangoUX の HOLD 画面は updatePoseStatus の変更で自動的に表示・非表示が切り替わる。
        // updatePoseStatus の呼び出しは OnTangoUpdateListener#onPoseAvailable で行う。
        // onPoseAvailable が呼び出されるには、リスナ指定時にフレーム ペアの指定が必須であり、かつ、
        // ポーズ データを検出するための機能を TangoConfig で有効化しなければならない。
        //
        // 注意点として、OnPoseAvailable はポーズが利用可能となった時点で呼び出されるため、
        // Tango#connect の呼び出し前にそのためのリスナが登録されている必要がある。
        // 例えば、Fragment でリスナを登録しようとした場合、登録が OnPoseAvailable の呼び出し後となり危険である。
        // また、そのような場合はリスナの重複登録も招きやすい。
        // 基本的には Activity でリスナを登録し、TangoUX の制御を行うべきと言える。
        List<TangoCoordinateFramePair> framePairs = new ArrayList<>();
        framePairs.add(new TangoCoordinateFramePair(TangoPoseData.COORDINATE_FRAME_START_OF_SERVICE,
                                                    TangoPoseData.COORDINATE_FRAME_DEVICE));
        mTango.connectListener(framePairs, mTangoUpdateDispatcher);

        TangoUx.StartParams startParams = new TangoUx.StartParams();
        mTangoUx.start(startParams);

        TangoConfig config = mTango.getConfig(TangoConfig.CONFIG_TYPE_DEFAULT);
        config.putBoolean(TangoConfig.KEY_BOOLEAN_COLORCAMERA, true);
//        config.putBoolean(TangoConfig.KEY_BOOLEAN_DEPTH, true);
//        config.putBoolean(TangoConfig.KEY_BOOLEAN_DRIFT_CORRECTION, true);
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
    public TangoUx getTangoUx() {
        return mTangoUx;
    }
}
