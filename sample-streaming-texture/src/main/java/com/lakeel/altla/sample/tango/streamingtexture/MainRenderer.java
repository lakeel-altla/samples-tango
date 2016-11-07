package com.lakeel.altla.sample.tango.streamingtexture;

import com.lakeel.altla.tango.rajawali.TangoCameraRenderer;

import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.primitives.Cube;
import org.rajawali3d.primitives.Sphere;

import android.content.Context;
import android.graphics.Color;

public final class MainRenderer extends TangoCameraRenderer {

    public MainRenderer(Context context) {
        super(context);
    }

    @Override
    protected void initSceneOverride() {
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
        // NOTE:
        // デフォルトは GLES20.GL_TRIANGLES で面を普通に描画。
        // ワイヤーフレーム描画を行いたい場合には、GLES20.GL_LINE_LOOP により全頂点を線分として連結して描画。
//        cube.setDrawingMode(GLES20.GL_LINE_LOOP);
        getCurrentScene().addChild(cube);

        Sphere sphere = new Sphere(1, 8, 8);
        sphere.setPosition(10, 0, -10);
        sphere.setMaterial(material);
        getCurrentScene().addChild(sphere);
    }
}
