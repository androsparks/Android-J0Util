package ru.jango.j0util.test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.test.AndroidTestCase;

import junit.framework.Assert;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import ru.jango.j0util.BmpUtil;
import ru.jango.j0util.LogUtil;

public class BmpUtilTest extends AndroidTestCase {

    private BmpUtilWrapper buw;

    public void setUp() throws Exception {
        super.setUp();
        buw = new BmpUtilWrapper();
    }

    public void testExtractSize() throws Exception {
        final byte[] data = BmpUtil.bmpToByte(Bitmap.createBitmap(200, 300, Bitmap.Config.ARGB_8888),
                Bitmap.CompressFormat.JPEG, 70);
        final Point size = buw.extractSize2(data);

        Assert.assertEquals(200, size.x);
        Assert.assertEquals(300, size.y);
    }

    public void testResolveScale() throws Exception {
        Assert.assertEquals(0.5f, BmpUtil.ScaleType.PROPORTIONAL_CROP.resolveScale(1, 2, 3, 4).x);
        Assert.assertEquals(3f, BmpUtil.ScaleType.PROPORTIONAL_CROP.resolveScale(3, 4, 1, 2).x);
        Assert.assertEquals(1.5f, BmpUtil.ScaleType.PROPORTIONAL_CROP.resolveScale(3, 2, 2, 3).x);

        Assert.assertEquals(1f / 3f, BmpUtil.ScaleType.PROPORTIONAL_FIT.resolveScale(1, 2, 3, 4).x);
        Assert.assertEquals(2f, BmpUtil.ScaleType.PROPORTIONAL_FIT.resolveScale(3, 4, 1, 2).x);
        Assert.assertEquals(2f / 3f, BmpUtil.ScaleType.PROPORTIONAL_FIT.resolveScale(3, 2, 2, 3).x);
    }

    public void testGenBFOptions() throws Exception {
        Assert.assertEquals(1, buw.genBFOptions2(BmpUtil.ScaleType.PROPORTIONAL_CROP, 300, 400, 100, 200).inSampleSize);
        Assert.assertEquals(2, buw.genBFOptions2(BmpUtil.ScaleType.PROPORTIONAL_CROP, 100, 200, 300, 400).inSampleSize);
        Assert.assertEquals(1, buw.genBFOptions2(BmpUtil.ScaleType.PROPORTIONAL_CROP, 300, 200, 200, 300).inSampleSize);

        Assert.assertEquals(1, buw.genBFOptions2(BmpUtil.ScaleType.PROPORTIONAL_FIT, 300, 400, 100, 200).inSampleSize);
        Assert.assertEquals(4, buw.genBFOptions2(BmpUtil.ScaleType.PROPORTIONAL_FIT, 100, 200, 300, 400).inSampleSize);
        Assert.assertEquals(2, buw.genBFOptions2(BmpUtil.ScaleType.PROPORTIONAL_FIT, 300, 200, 200, 300).inSampleSize);

        Assert.assertEquals(4, buw.genBFOptions2(BmpUtil.ScaleType.PROPORTIONAL_FIT, 2048, 2048, 6150, 2500).inSampleSize);
        Assert.assertEquals(2, buw.genBFOptions2(BmpUtil.ScaleType.PROPORTIONAL_FIT, 2048, 2048, 2738, 1500).inSampleSize);
        Assert.assertEquals(1, buw.genBFOptions2(BmpUtil.ScaleType.PROPORTIONAL_FIT, 2048, 2048, 1600, 1000).inSampleSize);
        Assert.assertEquals(1, buw.genBFOptions2(BmpUtil.ScaleType.PROPORTIONAL_FIT, 2048, 2048, 425, 554).inSampleSize);
    }

    public void testIsTooBig() throws Exception {
        final byte[] bmp1 = BmpUtil.bmpToByte(Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888),
                Bitmap.CompressFormat.JPEG, 10);
        final byte[] bmp2 = BmpUtil.bmpToByte(Bitmap.createBitmap(1999, 1999, Bitmap.Config.ARGB_8888),
                Bitmap.CompressFormat.JPEG, 10);

        Assert.assertFalse(BmpUtil.isTooBig(bmp1));
        Assert.assertFalse(BmpUtil.isTooBig(bmp2));
        Assert.assertTrue(BmpUtil.isTooBig(genLargeData()));
        Assert.assertTrue(BmpUtil.isTooBig(genHugeData()));
    }

    public void testFixMaxSize() throws Exception {
        final byte[] hugeData = genHugeData();
        Point size = buw.extractSize2(hugeData);
        Assert.assertTrue(size.x >= BmpUtil.MAX_TEXTURE_SIZE ||
                size.y >= BmpUtil.MAX_TEXTURE_SIZE);
        doTestFixMaxSize(hugeData, 1538, 625);

        final byte[] largeData = genLargeData();
        size = buw.extractSize2(largeData);
        Assert.assertTrue(size.x >= BmpUtil.MAX_TEXTURE_SIZE ||
                size.y >= BmpUtil.MAX_TEXTURE_SIZE);
        doTestFixMaxSize(largeData, 1369, 750);

        doTestFixMaxSize(genNormalData(), 1600, 1000);
        doTestFixMaxSize(genSmallData(), 425, 554);
    }

    private void doTestFixMaxSize(byte[] data, int w, int h) throws Exception {
        final Point size = buw.extractSize2(data);
        LogUtil.d(BmpUtilTest.class, "original size: " + size.x + ":" + size.y);

        final long timestamp = System.currentTimeMillis();
        final byte[] scaledData = BmpUtil.subsampleToMaxSize(data);
        LogUtil.d(BmpUtilTest.class, "scaled in: " + (System.currentTimeMillis() - timestamp) + "ms");

        final Point scaledSize = buw.extractSize2(scaledData);
        LogUtil.d(BmpUtilTest.class, "scaled size: " + scaledSize.x + ":" + scaledSize.y);
        Assert.assertTrue(scaledSize.x <= BmpUtil.MAX_TEXTURE_SIZE ||
                scaledSize.y <= BmpUtil.MAX_TEXTURE_SIZE);
        Assert.assertEquals(w, scaledSize.x);
        Assert.assertEquals(h, scaledSize.y);
    }

    public void testSubsample() throws Exception {
        final byte[] data = genLargeData();

        long t = System.currentTimeMillis();
        Bitmap b = BmpUtil.subsample(data, BmpUtil.ScaleType.PROPORTIONAL_CROP, 3000, 3000);
        LogUtil.d(BmpUtilTest.class, "t1 = " + (System.currentTimeMillis() - t));
        Assert.assertEquals(1369, b.getWidth());
        Assert.assertEquals(750, b.getHeight());
        b.recycle();

        t = System.currentTimeMillis();
        b = BmpUtil.subsample(data, BmpUtil.ScaleType.PROPORTIONAL_CROP, 1000, 1000);
        LogUtil.d(BmpUtilTest.class, "t2 = " + (System.currentTimeMillis() - t));
        Assert.assertEquals(1369, b.getWidth());
        Assert.assertEquals(750, b.getHeight());
        b.recycle();

        t = System.currentTimeMillis();
        b = BmpUtil.subsample(data, BmpUtil.ScaleType.PROPORTIONAL_CROP, 500, 500);
        LogUtil.d(BmpUtilTest.class, "t3 = " + (System.currentTimeMillis() - t));
        Assert.assertEquals(685, b.getWidth());
        Assert.assertEquals(375, b.getHeight());
        b.recycle();

        t = System.currentTimeMillis();
        b = BmpUtil.subsample(data, BmpUtil.ScaleType.PROPORTIONAL_FIT, 500, 500);
        LogUtil.d(BmpUtilTest.class, "t4 = " + (System.currentTimeMillis() - t));
        Assert.assertEquals(343, b.getWidth());
        Assert.assertEquals(188, b.getHeight());
        b.recycle();
    }

    // byte array as source for scaling: scale(byte[], ScaleType, int, int)
    public void testScale() throws Exception {
        final byte[] data = genLargeData();

        long t = System.currentTimeMillis();
        Bitmap b = BmpUtil.scale(data, BmpUtil.ScaleType.PROPORTIONAL_CROP, 3000, 3000);
        LogUtil.d(BmpUtilTest.class, "t1 = " + (System.currentTimeMillis() - t));
        Assert.assertEquals(1999, b.getWidth());
        Assert.assertEquals(1095, b.getHeight());
        b.recycle();

        t = System.currentTimeMillis();
        b = BmpUtil.scale(data, BmpUtil.ScaleType.PROPORTIONAL_FIT, 3000, 3000);
        LogUtil.d(BmpUtilTest.class, "t2 = " + (System.currentTimeMillis() - t));
        Assert.assertEquals(1999, b.getWidth());
        b.recycle();
        Assert.assertEquals(1095, b.getHeight());

        t = System.currentTimeMillis();
        b = BmpUtil.scale(data, BmpUtil.ScaleType.FIT_XY, 3000, 3000);
        LogUtil.d(BmpUtilTest.class, "t3 = " + (System.currentTimeMillis() - t));
        Assert.assertEquals(1999, b.getWidth());
        Assert.assertEquals(2000, b.getHeight());
        b.recycle();

        t = System.currentTimeMillis();
        b = BmpUtil.scale(data, BmpUtil.ScaleType.PROPORTIONAL_CROP, 1000, 1000);
        LogUtil.d(BmpUtilTest.class, "t4 = " + (System.currentTimeMillis() - t));
        Assert.assertEquals(1825, b.getWidth());
        Assert.assertEquals(1000, b.getHeight());
        b.recycle();

        t = System.currentTimeMillis();
        b = BmpUtil.scale(data, BmpUtil.ScaleType.PROPORTIONAL_FIT, 1500, 1500);
        LogUtil.d(BmpUtilTest.class, "t5 = " + (System.currentTimeMillis() - t));
        Assert.assertEquals(1500, b.getWidth());
        Assert.assertEquals(821, b.getHeight());
        b.recycle();

        t = System.currentTimeMillis();
        b = BmpUtil.scale(data, BmpUtil.ScaleType.FIT_XY, 1500, 1500);
        LogUtil.d(BmpUtilTest.class, "t6 = " + (System.currentTimeMillis() - t));
        Assert.assertEquals(1500, b.getWidth());
        Assert.assertEquals(1500, b.getHeight());
        b.recycle();
    }

    // Bitmap as source for scaling: scale(Bitmap, ScaleType, int, int)
    public void testScale2() throws Exception {
        final Bitmap src = Bitmap.createBitmap(200, 100, Bitmap.Config.ARGB_8888);

        long t = System.currentTimeMillis();
        Bitmap b = BmpUtil.scale(src, BmpUtil.ScaleType.PROPORTIONAL_CROP, 3000, 3000);
        LogUtil.d(BmpUtilTest.class, "t1 = " + (System.currentTimeMillis() - t));
        Assert.assertEquals(2000, b.getWidth());
        Assert.assertEquals(1000, b.getHeight());
        b.recycle();

        t = System.currentTimeMillis();
        b = BmpUtil.scale(src, BmpUtil.ScaleType.PROPORTIONAL_FIT, 3000, 3000);
        LogUtil.d(BmpUtilTest.class, "t2 = " + (System.currentTimeMillis() - t));
        Assert.assertEquals(2000, b.getWidth());
        b.recycle();
        Assert.assertEquals(1000, b.getHeight());

        t = System.currentTimeMillis();
        b = BmpUtil.scale(src, BmpUtil.ScaleType.FIT_XY, 3000, 3000);
        LogUtil.d(BmpUtilTest.class, "t3 = " + (System.currentTimeMillis() - t));
        Assert.assertEquals(2000, b.getWidth());
        Assert.assertEquals(2000, b.getHeight());
        b.recycle();

        t = System.currentTimeMillis();
        b = BmpUtil.scale(src, BmpUtil.ScaleType.PROPORTIONAL_CROP, 1000, 1000);
        LogUtil.d(BmpUtilTest.class, "t4 = " + (System.currentTimeMillis() - t));
        Assert.assertEquals(2000, b.getWidth());
        Assert.assertEquals(1000, b.getHeight());
        b.recycle();

        t = System.currentTimeMillis();
        b = BmpUtil.scale(src, BmpUtil.ScaleType.PROPORTIONAL_FIT, 1500, 1500);
        LogUtil.d(BmpUtilTest.class, "t5 = " + (System.currentTimeMillis() - t));
        Assert.assertEquals(1500, b.getWidth());
        Assert.assertEquals(750, b.getHeight());
        b.recycle();

        t = System.currentTimeMillis();
        b = BmpUtil.scale(src, BmpUtil.ScaleType.FIT_XY, 1500, 1500);
        LogUtil.d(BmpUtilTest.class, "t6 = " + (System.currentTimeMillis() - t));
        Assert.assertEquals(1500, b.getWidth());
        Assert.assertEquals(1500, b.getHeight());
        b.recycle();
    }

    public void testRotate() {
        // tested manually with BitmapRotationTestActivity
    }

    private byte[] genSmallData() throws Exception {
        //noinspection ConstantConditions
        return doLoad(getContext().getResources().openRawResource(R.drawable.small));
    }

    private byte[] genNormalData() throws Exception {
        //noinspection ConstantConditions
        return doLoad(getContext().getResources().openRawResource(R.drawable.normal));
    }

    private byte[] genLargeData() throws Exception {
        //noinspection ConstantConditions
        return doLoad(getContext().getResources().openRawResource(R.drawable.large));
    }

    private byte[] genHugeData() throws Exception {
        //noinspection ConstantConditions
        return doLoad(getContext().getResources().openRawResource(R.drawable.huge));
    }

    private byte[] doLoad(InputStream in) throws Exception {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();

        final byte[] buffer = new byte[512];
        while (in.read(buffer, 0, buffer.length) != -1) out.write(buffer);
        out.flush();

        final byte[] data = out.toByteArray();
        in.close();
        out.close();

        return data;
    }

    private class BmpUtilWrapper extends BmpUtil {
        public Point extractSize2(byte[] data) {
            return extractSize(data);
        }

        public BitmapFactory.Options genBFOptions2(ScaleType scaleType, int destW, int destH, int srcW, int srcH) {
            return genBFOptions(scaleType, destW, destH, srcW, srcH);
        }
    }

}
