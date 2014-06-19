/*
 * The MIT License Copyright (c) 2014 Krayushkin Konstantin (jangokvk@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package ru.jango.j0util;

import android.content.Context;
import android.hardware.SensorManager;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.WindowManager;

/**
 * Utility class for device rotation handling. Also wraps
 * {@link android.view.OrientationEventListener}.
 * <br /><br />
 *
 * -- in any time current rotation could be saved and then retrieved <br />
 * -- could be managed rotation changes threshold
 *
 * @see #rememberOrientation()
 * @see #setOrientationListener(ru.jango.j0util.RotationUtil.OrientationListener, int)
 * @see ru.jango.j0util.RotationUtil.OrientationListener
 */
public class RotationUtil extends OrientationEventListener {

    private int orientation;
    private int memOrientation;
    private int normOrientation;
    private int normMemOrientation;

    private int updateAspect;
    private int lastSentToListener;
    private OrientationListener listener;

    public RotationUtil(Context context) {
        super(context, SensorManager.SENSOR_DELAY_NORMAL);

        normOrientation = normMemOrientation = orientation =
                memOrientation = lastSentToListener = 0;
    }

    @Override
    public void onOrientationChanged(int orientation) {
        if (orientation != ORIENTATION_UNKNOWN) {
            this.orientation = orientation;
            this.normOrientation = normalize(orientation);

            if (Math.abs(lastSentToListener - orientation) > updateAspect && listener != null) {
                listener.onOrientationChanged(lastSentToListener, orientation, updateAspect);
                lastSentToListener = orientation;
            }
        }
    }

    private int normalize(int degrees) {
        if (degrees > 45 && degrees <= 135) return 90;
        else if (degrees > 135 && degrees <= 225) return 180;
        else if (degrees > 225 && degrees <= 315) return 270;

        return 0;
    }

    public void rememberOrientation() {
        memOrientation = orientation;
        normMemOrientation = memOrientation;
    }

    public int getRememberedOrientation() {
        return memOrientation;
    }

    public int getOrientation() {
        return orientation;
    }

    /**
     * Normalized orientation could be 0, 90, 180 or 270. The closest to
     * the remembered will be chosen.
     */
    public int getNormalizedRememberedOrientation() {
        return normMemOrientation;
    }

    /**
     * Normalized orientation could be 0, 90, 180 or 270. The closest to
     * the current will be chosen.
     */
    public int getNormalizedOrientation() {
        return normOrientation;
    }

    public OrientationListener getOrientationListener() {
        return listener;
    }

    /**
     * Returns the rotation changes threshold.
     *
     * @see #setOrientationListener(ru.jango.j0util.RotationUtil.OrientationListener, int)
     */
    public int getUpdateAspect() {
        return updateAspect;
    }

    /**
     * Set listener to the sensors. <b>updateAspect</b> is a value in degrees, on witch the
     * rotation should change to activate the listener. This way you can vary sensitivity of
     * the the sensors.
     *
     * @param listener listener for orientation change callbacks
     * @param updateAspect rotation changes threshold
     */
    public void setOrientationListener(OrientationListener listener, int updateAspect) {
        this.listener = listener;
        this.updateAspect = updateAspect;
    }

    /**
     * Returns window orientation.
     */
    public static int getLayoutOrientation(Context ctx) {
        return ctx.getResources().getConfiguration().orientation;
    }

    /**
     * Returns rotation angle in degrees, witch should be passed into the
     * {@link android.hardware.Camera#setDisplayOrientation(int)} for normal camera
     * preview image scaling.
     *
     * @see android.hardware.Camera
     */
    public static int getCameraRotation(Context ctx) {
        final WindowManager winManager =
                (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);

        switch (winManager.getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_0: return 90;
            case Surface.ROTATION_270: return 180;
            default: return 0;
        }
    }

    /**
     * Returns display rotation in degrees, not predefined constants.
     *
     * @see android.view.Display#getRotation()
     */
    public static int getDisplayRotation(Context ctx) {
        final WindowManager winManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);

        switch (winManager.getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_90: return 90;
            case Surface.ROTATION_180: return 180;
            case Surface.ROTATION_270: return 270;
            default: return 0;
        }
    }

    public interface OrientationListener {

        /**
         * Called, when the rotation was changed by <b>updateAspect</b> or greater. That is,
         * if the rotation goes slowly, |newVal - oldVal| = updateAspect; otherwise, if the
         * device is rotated fast, |newVal - oldVal| > updateAspect.
         *
         * @param oldVal previous rotation value
         * @param newVal new rotation value
         * @param updateAspect rotation changes threshold
         */
        public void onOrientationChanged(int oldVal, int newVal, int updateAspect);

    }
}
