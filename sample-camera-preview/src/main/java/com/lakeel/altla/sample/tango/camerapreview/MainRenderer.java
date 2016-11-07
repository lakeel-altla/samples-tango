package com.lakeel.altla.sample.tango.camerapreview;

import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.primitives.Cube;
import org.rajawali3d.primitives.Sphere;
import org.rajawali3d.renderer.Renderer;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;

public final class MainRenderer extends Renderer {

    private Pose mPose;

    private boolean mIsPoseUpdated;

    private final Object mPoseLock = new Object();

    public MainRenderer(Context context) {
        super(context);
    }

    @Override
    protected void initScene() {
        // Transparent
        getCurrentScene().setBackgroundColor(0);

        // DirectionalLight
        DirectionalLight light = new DirectionalLight(0, -1, -1);
        getCurrentScene().addLight(light);

        // Material
        Material material = new Material();
        material.enableLighting(true);
        material.setDiffuseMethod(new DiffuseMethod.Lambert());
        material.setAmbientColor(Color.WHITE);
        material.setColor(Color.WHITE);

        // Cube
        Cube cube = new Cube(1);
        cube.setPosition(0, 0, -10);
        cube.setRotation(45, 45, 45);
        cube.setMaterial(material);
        getCurrentScene().addChild(cube);

        Sphere sphere = new Sphere(1, 8, 8);
        sphere.setPosition(10, 0, -10);
        sphere.setMaterial(material);
        getCurrentScene().addChild(sphere);
    }

    @Override
    protected void onRender(long ellapsedRealtime, double deltaTime) {
        super.onRender(ellapsedRealtime, deltaTime);

        synchronized (mPoseLock) {
            if (mIsPoseUpdated) {
                mIsPoseUpdated = false;
                getCurrentCamera().setPosition(mPose.position);
                getCurrentCamera().setRotation(mPose.rotation);
            }
        }
    }

    @Override
    public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset,
                                 int yPixelOffset) {
    }

    @Override
    public void onTouchEvent(MotionEvent event) {
    }

    public void setPose(Pose pose) {
        synchronized (mPoseLock) {
            mPose = pose;
            mIsPoseUpdated = true;
        }
    }
}
