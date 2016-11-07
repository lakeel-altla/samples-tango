package com.lakeel.altla.sample.tango.planedetection;

import com.google.atap.tango.ux.TangoUx;
import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoConfig;
import com.google.atap.tangoservice.TangoCoordinateFramePair;
import com.google.atap.tangoservice.TangoPoseData;

import com.lakeel.altla.tango.TangoUpdateDispatcher;
import com.lakeel.altla.tango.TangoUxListener;
import com.projecttango.tangosupport.TangoSupport;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public final class MainActivity extends AppCompatActivity implements TangoHost {

    private Tango mTango;

    private TangoUx mTangoUx;

    private TangoUpdateDispatcher mTangoUpdateDispatcher = new TangoUpdateDispatcher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 復旧時や Instant Run 時には既に Fragment が存在するので、
        // Fragment から利用するオブジェクトは super.onCreate よりも前に初期化する必要がある。
        mTango = new Tango(this);
        mTangoUx = new TangoUx(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TangoUxListener tangoUxListener = new TangoUxListener(mTangoUx);
        mTangoUpdateDispatcher.getOnPoseAvailableListeners().add(tangoUxListener);
        mTangoUpdateDispatcher.getOnPointCloudAvailableListeners().add(tangoUxListener);
        mTangoUpdateDispatcher.getOnTangoEventListeners().add(tangoUxListener);

        MainFragment fragment = MainFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                                   .replace(R.id.fragment_container, fragment)
                                   .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        List<TangoCoordinateFramePair> framePairs = new ArrayList<>();
        framePairs.add(new TangoCoordinateFramePair(TangoPoseData.COORDINATE_FRAME_START_OF_SERVICE,
                                                    TangoPoseData.COORDINATE_FRAME_DEVICE));
        mTango.connectListener(framePairs, mTangoUpdateDispatcher);

        mTangoUx.start(new TangoUx.StartParams());

        TangoConfig config = mTango.getConfig(TangoConfig.CONFIG_TYPE_DEFAULT);
        // NOTE: Low latency integration is necessary to achieve a precise alignment of
        // virtual objects with the RBG image and produce a good AR effect.
        config.putBoolean(TangoConfig.KEY_BOOLEAN_LOWLATENCYIMUINTEGRATION, true);
        // Depth Perseption を有効化。
        config.putBoolean(TangoConfig.KEY_BOOLEAN_DEPTH, true);
        config.putInt(TangoConfig.KEY_INT_DEPTH_MODE, TangoConfig.TANGO_DEPTH_MODE_POINT_CLOUD);
        // カラー カメラを有効化。
        config.putBoolean(TangoConfig.KEY_BOOLEAN_COLORCAMERA, true);
        // NOTE:
        //
        // トラッキング ロストからの復旧を検知するにはドリフト コレクションを有効にする。
        // ドリフトを正したポーズ データは、ベース フレーム TangoPoseData.COORDINATE_FRAME_AREA_DESCRIPTION から
        // 任意のターゲット フレームに対してのフレーム ペアで利用可能。
        // 公式サンプルの java_plane_fitting_example では、
        // コメント文でターゲット フレームが TangoPoseData.COORDINATE_FRAME_DEVICE であるものとしているが、
        // 同サンプルにもあるように TangoPoseData.COORDINATE_FRAME_CAMERA_COLOR としても利用可能。
        //
        // なお、ドリフト コレクションを有効にしなければ、COORDINATE_FRAME_AREA_DESCRIPTION をベースにしたフレーム ペアは
        // 機能しない模様。
        // モーション トラッキングを有効にすれば機能しそうなものだが、
        // KEY_BOOLEAN_MOTIONTRACKING を true に設定しただけでは機能しない。
        config.putBoolean(TangoConfig.KEY_BOOLEAN_DRIFT_CORRECTION, true);
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
