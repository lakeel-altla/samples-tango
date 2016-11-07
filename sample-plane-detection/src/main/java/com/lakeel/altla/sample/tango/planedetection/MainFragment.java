package com.lakeel.altla.sample.tango.planedetection;

import com.google.atap.tango.ux.TangoUxLayout;
import com.google.atap.tangoservice.TangoCameraIntrinsics;
import com.google.atap.tangoservice.TangoException;

import com.lakeel.altla.tango.OnFrameAvailableListener;
import com.lakeel.altla.tango.PointCloud;

import org.rajawali3d.view.SurfaceView;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;

public final class MainFragment extends Fragment implements OnFrameAvailableListener {

    // NOTE:
    //
    // TangoUx と Rajawali SurfaceView の組み合わせは、
    // Android Strudio からのデバッグ起動と Android からの通常起動とで振る舞いがことなる。
    // 前者では TangoUx の HOLD 画面がフリーズし、SurfaceView が非表示のままとなるが、
    // 後者ではどちらも正常に稼働する。
    // この振る舞いの違いは公式の java_point_cloud_example でも見られ、こちらもデバッグ起動では正常に稼働しない。

    @BindView(R.id.surface_view)
    SurfaceView mSurfaceView;

    @BindView(R.id.layout_tango_ux)
    TangoUxLayout mTangoUxLayout;

    private static final String TAG = "MainFragment";

    private TangoHost mTangoHost;

    private MainRenderer mRenderer;

    private PointCloud mPointCloud = new PointCloud();

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mTangoHost = TangoHost.class.cast(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        mTangoHost.getTangoUx().setLayout(mTangoUxLayout);

        mRenderer = new MainRenderer(getContext());
        mSurfaceView.setSurfaceRenderer(mRenderer);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mRenderer.connectToTangoCamera(mTangoHost.getTango());
        mTangoHost.getTangoUpdateDispatcher().getOnFrameAvailableListeners().add(this);
        mTangoHost.getTangoUpdateDispatcher().getOnPointCloudAvailableListeners().add(mPointCloud);
    }

    @Override
    public void onPause() {
        super.onPause();

        mTangoHost.getTangoUpdateDispatcher().getOnFrameAvailableListeners().remove(this);
        mTangoHost.getTangoUpdateDispatcher().getOnPointCloudAvailableListeners().remove(mPointCloud);
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

    @OnTouch(R.id.surface_view)
    boolean onTouchSurfaceView(View view, MotionEvent motionEvent) {
        if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
            float u = motionEvent.getX() / view.getWidth();
            float v = motionEvent.getY() / view.getHeight();

            try {
                // Fit a plane on the clicked point using the latest poiont cloud data
                // Synchronize against concurrent access to the RGB timestamp in the OpenGL thread
                // and a possible service disconnection due to an onPause event.
                PointCloud.Plane plane = mPointCloud.findPlane(mRenderer.getUpdateTextureTimestamp(), u, v);
                if (plane != null) {
                    mRenderer.updatePlaneData(plane);
                }
            } catch (TangoException t) {
                // SurfaceView 利用の場合は Snackbar が機能しない点に注意。
                // ここでは Toast の利用なので大丈夫。
                Toast.makeText(getContext(), R.string.failed_measurement, Toast.LENGTH_SHORT).show();
                Log.w(TAG, getString(R.string.failed_measurement).toString(), t);
            } catch (SecurityException t) {
                Toast.makeText(getContext(), R.string.failed_permissions, Toast.LENGTH_SHORT).show();
                Log.e(TAG, getString(R.string.failed_permissions), t);
            }
        }

        // ここで全てを消費しなければ ACTION_UP が発生しなくなる。
        return true;
    }
}
