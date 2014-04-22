package ru.jango.j0util.test;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import ru.jango.j0util.BmpUtil;

/**
 * Activity for manual testing
 * {@link ru.jango.j0util.BmpUtil#rotate(android.graphics.Bitmap, ru.jango.j0util.BmpUtil.ScaleType, int)}.
 *
 * Not included into automatic tests.
 */
public class BitmapRotationTestActivity extends Activity {

    private ImageView test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final RelativeLayout layout = createLayout();
        final ImageView src = createImgView();
        test = createImgView();

        layout.addView(src);
        layout.addView(test);
        src.setImageBitmap(createBitmap(Color.RED));

        setContentView(layout);
        new Thread(run).start();
    }

    private RelativeLayout createLayout() {
        final RelativeLayout v = new RelativeLayout(this);
        v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));

        return v;
    }

    private ImageView createImgView() {
        final ImageView v = new ImageView(this);
        v.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));
        v.setScaleType(ImageView.ScaleType.CENTER);
        v.setBackgroundColor(Color.TRANSPARENT);

        return v;
    }

    private Bitmap createBitmap(int color) {
        final Bitmap bmp = Bitmap.createBitmap(200, 100, Bitmap.Config.ARGB_8888);
        final Canvas c = new Canvas();

        c.setBitmap(bmp);
        c.drawColor(color);

        return bmp;
    }

    private final Runnable run = new Runnable() {
        @Override
        public void run() {
            for (BmpUtil.ScaleType scaleType : BmpUtil.ScaleType.values())
                doTest(scaleType);

            doTest(null);
        }

        private void doTest(BmpUtil.ScaleType scaleType) {
            final Bitmap testB = createBitmap(Color.WHITE);
            Bitmap buff = Bitmap.createBitmap(testB);

            for (int i = 0; i <= 180; i += 30) {
                final Bitmap tmp = BmpUtil.rotate(testB, scaleType, i);
                buff.recycle();
                buff = tmp;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() { test.setImageBitmap(tmp); }
                });

                synchronized (this) { try { wait(1000); } catch (Exception ignored) {} }
            }

            testB.recycle();
            buff.recycle();
        }
    };

}
