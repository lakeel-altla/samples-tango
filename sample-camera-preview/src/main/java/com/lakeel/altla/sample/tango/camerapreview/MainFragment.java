package com.lakeel.altla.sample.tango.camerapreview;

import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoCameraIntrinsics;
import com.google.atap.tangoservice.TangoCameraPreview;
import com.google.atap.tangoservice.TangoConfig;
import com.google.atap.tangoservice.TangoCoordinateFramePair;
import com.google.atap.tangoservice.TangoPoseData;

import com.lakeel.altla.tango.BaseOnTangoUpdateListener;

import org.rajawali3d.view.SurfaceView;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class MainFragment extends Fragment {

    @BindView(R.id.view_top)
    FrameLayout mLayout;

    @BindView(R.id.surface_view)
    SurfaceView mSurfaceView;

    private Tango mTango;

    private TangoCameraPreview mTangoCameraPreview;

    private MainRenderer mRenderer;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTango = new Tango(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        mTangoCameraPreview = new TangoCameraPreview(getContext());
        mLayout.addView(mTangoCameraPreview, 0);

        // API ドキュメントによると setSurfaceRenderer よりも前に呼び出せとのこと。
        // もしも後に呼び出すと、setTransparent が効かなくなるのみならず、bringToFront も機能しなくなる。
        mSurfaceView.setTransparent(true);

        mRenderer = new MainRenderer(getContext());
        mSurfaceView.setSurfaceRenderer(mRenderer);
        mSurfaceView.bringToFront();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mTangoCameraPreview.connectToTangoCamera(mTango, TangoCameraIntrinsics.TANGO_CAMERA_COLOR);

        TangoConfig config = mTango.getConfig(TangoConfig.CONFIG_TYPE_DEFAULT);
        mTango.connect(config);

        List<TangoCoordinateFramePair> framePairs = new ArrayList<>();
        framePairs.add(new TangoCoordinateFramePair(TangoPoseData.COORDINATE_FRAME_START_OF_SERVICE,
                                                    TangoPoseData.COORDINATE_FRAME_DEVICE));

        mTango.connectListener(framePairs, new BaseOnTangoUpdateListener() {
            @Override
            public void onPoseAvailable(TangoPoseData tangoPoseData) {
                Pose pose = ScenePoseCalculator.toOpenGLPose(tangoPoseData);
                mRenderer.setPose(pose);
            }

            @Override
            public void onFrameAvailable(int cameraId) {
                // Check if the frame available is for the camera we want and
                // update its frame on the camera preview.
                if (cameraId == TangoCameraIntrinsics.TANGO_CAMERA_COLOR) {
                    mTangoCameraPreview.onFrameAvailable();
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        mTango.disconnect();

        mTangoCameraPreview.disconnectFromTangoCamera();
    }
}
