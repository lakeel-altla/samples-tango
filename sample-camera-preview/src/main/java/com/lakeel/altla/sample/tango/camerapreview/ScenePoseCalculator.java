package com.lakeel.altla.sample.tango.camerapreview;

import com.google.atap.tangoservice.TangoPoseData;

import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;

/**
 * Convenient class for calculating transformations from the Tango world to the OpenGL world,
 * using Rajawali specific classes and conventions.
 *
 * This class is ported from Tango example utils.
 */
final class ScenePoseCalculator {

    // Transformation from the Tango Area Description or Start of Service coordinate frames
    // to the OpenGL coordinate frame.
    // NOTE: Rajawali uses column-major for matrices.
    private static final Matrix4 OPENGL_T_TANGO_WORLD = new Matrix4(new double[] {
            1, 0, 0, 0,
            0, 0, -1, 0,
            0, 1, 0, 0,
            0, 0, 0, 1
    });

    /**
     * Avoid instantiating the class since it will only be used statically.
     */
    private ScenePoseCalculator() {
    }

    /**
     * Converts from TangoPoseData to a Matrix4 for transformations.
     */
    private static Matrix4 tangoPoseToMatrix(TangoPoseData tangoPose) {
        Vector3 v = new Vector3(tangoPose.translation[0], tangoPose.translation[1], tangoPose.translation[2]);
        Quaternion q = new Quaternion(tangoPose.rotation[3], tangoPose.rotation[0],
                                      tangoPose.rotation[1], tangoPose.rotation[2]);
        // NOTE: Rajawali quaternions use a left-hand rotation around the axis convention.
        q.conjugate();
        Matrix4 m = new Matrix4();
        m.setAll(v, new Vector3(1, 1, 1), q);
        return m;
    }

    /**
     * Helper method to extract a Pose object from a transformation matrix taking into account
     * Rajawali conventions.
     */
    private static Pose matrixToPose(Matrix4 m) {
        // Get translation and rotation components from the transformation matrix.
        Vector3 p = m.getTranslation();
        Quaternion q = new Quaternion();
        q.fromMatrix(m);

        return new Pose(p, q);
    }

    /**
     * Given a pose in start of service or area description frame calculate the corresponding
     * position and orientation for a 3D object in the Rajawali world.
     */
    static Pose toOpenGLPose(TangoPoseData tangoPose) {
        Matrix4 start_service_T_device = tangoPoseToMatrix(tangoPose);

        // Get device pose in OpenGL world frame.
        Matrix4 opengl_world_T_device = OPENGL_T_TANGO_WORLD.clone()
                                                            .multiply(start_service_T_device);

        return matrixToPose(opengl_world_T_device);
    }
}
