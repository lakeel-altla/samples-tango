package com.lakeel.altla.sample.tango.streamingtexture;

import com.google.atap.tango.ux.TangoUx;
import com.google.atap.tango.ux.TangoUxLayout;
import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoCameraIntrinsics;
import com.google.atap.tangoservice.TangoEvent;

import com.lakeel.altla.tango.OnFrameAvailableListener;
import com.lakeel.altla.tango.OnTangoEventListener;

import org.rajawali3d.view.SurfaceView;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class MainFragment extends Fragment implements OnFrameAvailableListener, OnTangoEventListener {

    @BindView(R.id.surface_view)
    SurfaceView mSurfaceView;

    @BindView(R.id.layout_tango_ux)
    TangoUxLayout mTangoUxLayout;

    private TangoHost mTangoHost;

    private Tango mTango;

    private TangoUx mTangoUx;

    private MainRenderer mRenderer;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mTangoHost = TangoHost.class.cast(context);
        mTango = mTangoHost.getTango();
        mTangoUx = mTangoHost.getTangoUx();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        mTangoUx.setLayout(mTangoUxLayout);

        mRenderer = new MainRenderer(getContext());
        mSurfaceView.setSurfaceRenderer(mRenderer);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mRenderer.connectToTangoCamera(mTango);
        mTangoHost.getTangoUpdateDispatcher().getOnFrameAvailableListeners().add(this);
        mTangoHost.getTangoUpdateDispatcher().getOnTangoEventListeners().add(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        mTangoHost.getTangoUpdateDispatcher().getOnFrameAvailableListeners().remove(this);
        mTangoHost.getTangoUpdateDispatcher().getOnTangoEventListeners().remove(this);
        mRenderer.disconnectFromTangoCamera();
    }

    @Override
    public void onFrameAvailable(int cameraId) {
        // Check if the frame available is for the camera we want and update its frame
        // on the view.
        if (cameraId == TangoCameraIntrinsics.TANGO_CAMERA_COLOR) {
            // Mark a camera frame is available for rendering in the OpenGL thread
            mRenderer.onFrameAvailable();
            mSurfaceView.requestRender();
        }
    }

    @Override
    public void onTangoEvent(TangoEvent event) {
        mTangoUx.updateTangoEvent(event);
    }
}
