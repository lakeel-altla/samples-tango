package com.lakeel.altla.sample.tango.planedetection;

import com.lakeel.altla.tango.PointCloud;
import com.lakeel.altla.tango.rajawali.TangoCameraRenderer;
import com.projecttango.tangosupport.TangoSupport;

import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public final class MainRenderer extends TangoCameraRenderer {

    private static final String TAG = "MainRenderer";

    private final PlaneData mPlaneData = new PlaneData();

    private final Vector3 cameraForward = new Vector3();

    private Plane mPlane;

    private boolean mIsPlaneDataUpdated;

    public MainRenderer(Context context) {
        super(context);
    }

    @Override
    protected void initSceneOverride() {
        // DirectionalLight
        DirectionalLight light = new DirectionalLight(0, -1, -1);
        getCurrentScene().addLight(light);

        // NOTE
        //
        // サンプルとしては文字列を描画する必要性はなく、ここでは平面の上方向がどこにあるかを見るために文字列を描画している。

        Bitmap bitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);
        Texture texture = new Texture("planeTexture", bitmap);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setTextSize(35);
        canvas.drawColor(Color.WHITE);
        canvas.drawText("ABC",
                        canvas.getWidth() / 2,
                        (int) (canvas.getHeight() / 2 - (paint.descent() + paint.ascent()) / 2),
                        paint);

        // Material
        Material material = new Material();
        material.enableLighting(true);
        material.setDiffuseMethod(new DiffuseMethod.Lambert());
        material.setAmbientColor(Color.WHITE);
        material.setColorInfluence(1);
        try {
            material.addTexture(texture);
        } catch (ATexture.TextureException e) {
            Log.e(TAG, "Can not add a texture.", e);
        }

        mPlane = new Plane(0.5f, 0.5f, 1, 1);
        mPlane.setMaterial(material);
        mPlane.setColor(Color.WHITE);
        mPlane.setDoubleSided(true);
        getCurrentScene().addChild(mPlane);
    }
//
//    @Override
//    protected void updateCameraPose(TangoPoseData cameraPose) {
//        super.updateCameraPose(cameraPose);
//
//        // カメラの回転に合わせてカメラの方向ベクトルを更新。
//        cameraForward.x = 0;
//        cameraForward.y = 0;
//        cameraForward.z = -1;
//        cameraForward.rotateBy(getCurrentCamera().getOrientation());
//    }

    @Override
    protected void updateCameraPose(TangoSupport.TangoMatrixTransformData cameraTransform) {
        super.updateCameraPose(cameraTransform);

        // カメラの回転に合わせてカメラの方向ベクトルを更新。
        cameraForward.x = 0;
        cameraForward.y = 0;
        cameraForward.z = -1;
        cameraForward.rotateBy(getCurrentCamera().getOrientation());
    }

    @Override
    protected void onRender(long ellapsedRealtime, double deltaTime) {
        synchronized (this) {
            if (mIsPlaneDataUpdated) {
                mPlane.setPosition(mPlaneData.position);

                Vector3 up = Vector3.Y;
                if (0.9 < Math.abs(Vector3.dot(mPlaneData.normal, up))) {
                    // 法線が Y 軸と平行ならばカメラの方向ベクトルを UP ベクトルとする。
                    up = cameraForward;
                }

                Quaternion orientation = Quaternion.lookAtAndCreate(mPlaneData.normal, up);
                mPlane.setOrientation(orientation);

                mIsPlaneDataUpdated = false;
            }
        }
        super.onRender(ellapsedRealtime, deltaTime);
    }

    public synchronized void updatePlaneData(PointCloud.Plane plane) {
        // Rajawali 形式へデータ変換
        mPlaneData.position.x = plane.center[0];
        mPlaneData.position.y = plane.center[1];
        mPlaneData.position.z = plane.center[2];
        mPlaneData.normal.x = plane.normal[0];
        mPlaneData.normal.y = plane.normal[1];
        mPlaneData.normal.z = plane.normal[2];
        mIsPlaneDataUpdated = true;
    }

    private class PlaneData {

        Vector3 position = new Vector3();

        Vector3 normal = new Vector3();
    }
}
