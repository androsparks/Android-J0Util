package ru.jango.j0util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Utility class with methods for working with {@link android.graphics.Bitmap}'s.
 * Mostly for scaling.
 */
public class BmpUtil {

    /**
     * Maximum texture size, witch Android could handle. That is, maximum {@link android.graphics.Bitmap}
     * size, witch won't through {@link java.lang.OutOfMemoryError}.
     * <br /><br />
     * <p/>
     * Actually maximum texture size for now is 2048x2048, but here was chosen 2000x2000
     * just in case.
     */
    public static final int MAX_TEXTURE_SIZE = 2000;

    /**
     * Simple scaling options.
     */
    public enum ScaleType {

        /**
         * With this option the resulting image size will be LESS OR EQUAL
         * to the passed dimensions. Whole resulting image could be fit into those dimensions.
         * Also image will be scaled saving proportions.
         */
        PROPORTIONAL_FIT {
            @SuppressWarnings("SuspiciousNameCombination")
            @Override
            public PointF resolveScale(int destW, int destH, int srcW, int srcH) {
                final PointF scales = getScales(destW, destH, srcW, srcH);
                if (((float) destW) / ((float) destH) < ((float) srcW) / ((float) srcH))
                    return new PointF(scales.x, scales.x);
                else return new PointF(scales.y, scales.y);
            }
        },

        /**
         * With this option the resulting image size will be GREATER OR EQUAL
         * to the passed dimensions. Whole rectangle with those dimensions could be fit
         * into the resulting image. Also image will be scaled saving proportions.
         */
        PROPORTIONAL_CROP {
            @SuppressWarnings("SuspiciousNameCombination")
            @Override
            public PointF resolveScale(int destW, int destH, int srcW, int srcH) {
                final PointF scales = getScales(destW, destH, srcW, srcH);
                if (((float) destW) / ((float) destH) > ((float) srcW) / ((float) srcH))
                    return new PointF(scales.x, scales.x);
                else return new PointF(scales.y, scales.y);
            }
        },

        /**
         * With this option the resulting image will have the exactly passed dimensions,
         * without saving proportions.
         */
        FIT_XY {
            @Override
            public PointF resolveScale(int destW, int destH, int srcW, int srcH) {
                return new PointF(((float) destW) / ((float) srcW), ((float) destH) / ((float) srcH));
            }
        };

        protected static PointF getScales(int destW, int destH, int srcW, int srcH) {
            return new PointF(((float) destW) / ((float) srcW), ((float) destH) / ((float) srcH));
        }

        public abstract PointF resolveScale(int destW, int destH, int srcW, int srcH);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //
    //              Decoding from byte array methods
    //
    //////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Checks, if the passed image's dimensions are greater, than acceptable.
     *
     * @param data raw (not decoded) image data as byte array
     * @see #MAX_TEXTURE_SIZE
     */
    public static boolean isTooBig(byte[] data) {
        final Point size = extractSize(data);
        return (size.x >= MAX_TEXTURE_SIZE || size.y >= MAX_TEXTURE_SIZE);
    }

    /**
     * If passed image's dimensions are greater than acceptable (than
     * {@link #MAX_TEXTURE_SIZE}), scales image down and converts
     * it back into byte array.
     * <br /><br />
     * <p/>
     * <b>ATTENTION</b>: conversion back into byte array uses
     * {@link android.graphics.Bitmap#compress(android.graphics.Bitmap.CompressFormat, int, java.io.OutputStream)},
     * that is very long running operation.
     * <br /><br />
     * <p/>
     * <b>ATTENTION</b>: scaling is done by {@link android.graphics.BitmapFactory.Options#inSampleSize},
     * witch means that resulting image wouldn't be exactly MAX_TEXTURE_SIZE*MAX_TEXTURE_SIZE,
     * likely it would be much smaller. It may be batter to use
     * {@link #scale(byte[], ru.jango.j0util.BmpUtil.ScaleType, int, int)}.
     *
     * @param data raw (not decoded) image data as byte array
     * @return raw (not decoded) image data with fixed dimensions, or original byte array
     * @see #MAX_TEXTURE_SIZE
     * @see #bmpToByte(android.graphics.Bitmap, android.graphics.Bitmap.CompressFormat, int)
     * @see #scale(byte[], ru.jango.j0util.BmpUtil.ScaleType, int, int)
     */
    public static byte[] subsampleToMaxSize(byte[] data) {
        final Point size = extractSize(data);
        final BitmapFactory.Options ops = genBFOptions(ScaleType.PROPORTIONAL_FIT,
                MAX_TEXTURE_SIZE, MAX_TEXTURE_SIZE, size.x, size.y);

        if (ops.inSampleSize == 1) return data;
        final Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length, ops);
        final byte[] scaledData = bmpToByte(bmp, Bitmap.CompressFormat.PNG, 100);
        bmp.recycle();

        return scaledData;
    }

    /**
     * Scales image according to the passed params. Scaling is done by
     * {@link android.graphics.BitmapFactory.Options#inSampleSize}, witch means: <br />
     * - scaling would be done very fast <br />
     * - resulting image wouldn't exactly match the passed params, but would be
     * as close to them as possible <br />
     * - method can only scale image down, not expand
     * <br /><br />
     * <p/>
     * Also checks bounds to be smaller than {@link #MAX_TEXTURE_SIZE}.
     *
     * @param data      raw (not decoded) image data as byte array
     * @param scaleType scaling option
     * @param w         target width
     * @param h         target height
     * @return decoded and scaled bitmap
     * @see ru.jango.j0util.BmpUtil.ScaleType#PROPORTIONAL_CROP
     * @see ru.jango.j0util.BmpUtil.ScaleType#PROPORTIONAL_FIT
     * @see ru.jango.j0util.BmpUtil.ScaleType#FIT_XY
     */
    public static Bitmap subsample(byte[] data, ScaleType scaleType, int w, int h) {
        final Point size = extractSize(data);
        final BitmapFactory.Options ops = genBFOptions(
                (w >= MAX_TEXTURE_SIZE || h >= MAX_TEXTURE_SIZE) ? ScaleType.PROPORTIONAL_FIT : scaleType,
                Math.min(w, MAX_TEXTURE_SIZE),
                Math.min(h, MAX_TEXTURE_SIZE),
                size.x, size.y);

        return BitmapFactory.decodeByteArray(data, 0, data.length, ops);
    }

    /**
     * Scales image according to the passed params. First subsamples image data and then scales image
     * with {@link android.graphics.Bitmap#createScaledBitmap(android.graphics.Bitmap, int, int, boolean)}.
     * Also checks bounds to be smaller than {@link #MAX_TEXTURE_SIZE}.
     *
     * @param data      raw (not decoded) image data as byte array
     * @param scaleType scaling option
     * @param w         target width
     * @param h         target height
     * @return decoded and scaled bitmap
     * @see #subsample(byte[], ru.jango.j0util.BmpUtil.ScaleType, int, int)
     * @see ru.jango.j0util.BmpUtil.ScaleType#PROPORTIONAL_CROP
     * @see ru.jango.j0util.BmpUtil.ScaleType#PROPORTIONAL_FIT
     * @see ru.jango.j0util.BmpUtil.ScaleType#FIT_XY
     */
    public static Bitmap scale(byte[] data, ScaleType scaleType, int w, int h) {
        final Bitmap ssBmp = subsample(data, ScaleType.PROPORTIONAL_CROP, w, h);
        final PointF scales = resolveScale(scaleType, w, h, ssBmp.getWidth(), ssBmp.getHeight());
        final Bitmap ret = Bitmap.createScaledBitmap(ssBmp,
                (int) (ssBmp.getWidth() * scales.x),
                (int) (ssBmp.getHeight() * scales.y), true);

        if (ret != ssBmp) ssBmp.recycle();
        return ret;
    }

    /**
     * Converts image to byte array.
     * <br /><br />
     * <p/>
     * <b>ATTENTION</b>: image conversion is done by compression, witch is very long running operation.
     *
     * @param bmp     original bitmap
     * @param format  compress format
     * @param quality compress quality
     * @return compress image as byte array
     * @see android.graphics.Bitmap#compress(android.graphics.Bitmap.CompressFormat, int, java.io.OutputStream)
     */
    public static byte[] bmpToByte(Bitmap bmp, Bitmap.CompressFormat format, int quality) {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(format, quality, stream);

        try {
            stream.flush();
            byte[] data = stream.toByteArray();
            stream.close();

            return data;
        } catch (IOException e) {
            return null;
        }
    }

    protected static Point extractSize(byte[] data) {
        final BitmapFactory.Options ops = new BitmapFactory.Options();
        ops.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, ops);

        return new Point(ops.outWidth, ops.outHeight);
    }

    /**
     * Generates sampling factor for BitmapFactory.Options.inSampleSize according
     * to the passed ScaleType.
     */
    protected static BitmapFactory.Options genBFOptions(ScaleType scaleType, int destW, int destH, int srcW, int srcH) {
        final BitmapFactory.Options ops = new BitmapFactory.Options();
        ops.inSampleSize = Math.max(1, (int) Math.ceil(1 / scaleType.resolveScale(destW, destH, srcW, srcH).x));
        if (ops.inSampleSize == 1) return ops;

        // 2^pow = ops.inSampleSize
        double pow = Math.log(ops.inSampleSize) / Math.log(2);
        // incPow == true, if ops.inSampleSize should be increased; by default it
        // is decreased - look "Note" at the end of inSampleSize description
        boolean incPow;

        if (scaleType != ScaleType.PROPORTIONAL_CROP)
            // incPow = true, if ops.inSampleSize is NOT a power of 2 (that is, pow is not integer)
            incPow = (pow - Math.ceil(pow) + 1) > 0.00001;
        else {
            // square of the resulting image, if we scale it with increased ops.inSampleSize
            int largeSquare = (int) (srcW * srcH / Math.pow((int) Math.ceil(pow), 2));
            // square of the resulting image, if we scale it with ops.inSampleSize as is
            int smallSquare = (int) (srcW * srcH / Math.pow((int) (Math.ceil(pow) - 1), 2));
            // incPow = true, if resulting size after scaling with increased ops.inSampleSize is CLOSER to
            // the target size, than resulting size after scaling without increasing ops.inSampleSize;
            // and it is still bigger, than the target size (scaleType == PROPORTIONAL_CROP)
            incPow = Math.abs(destH * destW - largeSquare) < Math.abs(destH * destW - smallSquare) &&
                    destH * destW <= largeSquare;
        }

        // finally, increasing ops.inSampleSize if needed
        if (incPow) ops.inSampleSize = (int) Math.pow(2, Math.ceil(pow));
        return ops;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                      Other processing methods
    //
    //////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * More advanced scaling than simple
     * {@link android.graphics.Bitmap#createScaledBitmap(android.graphics.Bitmap, int, int, boolean)}.
     * <br /><br />
     * <p/>
     * Method will create totally new Bitmap, and the source Bitmap will stay safe
     * ({@link android.graphics.Bitmap#recycle()} won't be called). So, if you don't need the source
     * Bitmap after scaling, you should manually call {@link android.graphics.Bitmap#recycle()} for
     * better memory usage.
     *
     * @param src       source image for scaling
     * @param scaleType scaling option
     * @param w         target image width
     * @param h         target image height
     * @return scaled image
     *
     * @see #subsample(byte[], ru.jango.j0util.BmpUtil.ScaleType, int, int)
     * @see #scale(byte[], ru.jango.j0util.BmpUtil.ScaleType, int, int)
     * @see android.graphics.Bitmap#createScaledBitmap(android.graphics.Bitmap, int, int, boolean)
     * @see android.graphics.Bitmap#recycle()
     */
    public static Bitmap scale(Bitmap src, ScaleType scaleType, int w, int h) {
        final PointF scales = resolveScale(scaleType, w, h, src.getWidth(), src.getHeight());

        final Matrix m = new Matrix();
        m.setScale(scales.x, scales.y);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, true);
    }

    /**
     * More advanced rotation than with help of {@link android.graphics.Matrix}. After rotation
     * source image could be automatically scaled according to the passed <b>scaleType</b>; width and
     * height of the source image will be treated as target width and height for scaling.
     * <br /><br />
     * <b>scaleType</b> could be NULL, than the image would be just rotated without any post scaling.
     * <br /><br />
     * Method will create totally new Bitmap, and the source Bitmap will stay safe
     * ({@link android.graphics.Bitmap#recycle()} won't be called). So, if you don't need the source
     * Bitmap after rotation, you should manually call {@link android.graphics.Bitmap#recycle()} for
     * better memory usage.
     *
     * @param src       source image for rotation
     * @param scaleType scaling option, may be NULL
     * @param degrees   rotation angle in degrees
     * @return rotated image
     *
     * @see android.graphics.Bitmap#recycle()
     */
    public static Bitmap rotate(Bitmap src, ScaleType scaleType, int degrees) {
        degrees = degrees % 360;

        final Matrix m = new Matrix();
        m.setRotate(degrees, src.getWidth() / 2, src.getHeight() / 2);

        if (scaleType != null) {
            double radAngl = Math.toRadians(degrees);
            int resultW = (int) (Math.abs(src.getWidth() * Math.cos(radAngl)) + Math.abs(src.getHeight() * Math.sin(radAngl)));
            int resultH = (int) (Math.abs(src.getWidth() * Math.sin(radAngl)) + Math.abs(src.getHeight() * Math.cos(radAngl)));

            final PointF scales = resolveScale(scaleType, src.getWidth(), src.getHeight(), resultW, resultH);
            m.postScale(scales.x, scales.y);
        }

        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, true);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                      Other helper methods
    //
    //////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Wrapper for {@link ru.jango.j0util.BmpUtil.ScaleType#resolveScale(int, int, int, int)}, witch
     * also checks bounds for {@link #MAX_TEXTURE_SIZE}.
     */
    protected static PointF resolveScale(ScaleType scaleType, int destW, int destH, int srcW, int srcH) {
        destW = Math.min(destW, MAX_TEXTURE_SIZE);
        destH = Math.min(destH, MAX_TEXTURE_SIZE);

        PointF scales = scaleType.resolveScale(destW, destH, srcW, srcH);
        if (srcW * scales.x > MAX_TEXTURE_SIZE || srcH * scales.y > MAX_TEXTURE_SIZE)
            scales = ScaleType.PROPORTIONAL_FIT.resolveScale(destW, destH, srcW, srcH);

        return scales;
    }

}
