package com.lakeel.altla.sample.tango.camerapreview;

import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;

/**
 * Convenience class to encapsulate a position and rotation combination using Rajawali classes.
 *
 * This class is ported from Tango example utils.
 */
final class Pose {

    final Vector3 position;

    final Quaternion rotation;

    Pose(Vector3 position, Quaternion rotation) {
        this.rotation = rotation;
        this.position = position;
    }

    public String toString() {
        return "position = " + position + ", rotation = " + rotation;
    }
}
