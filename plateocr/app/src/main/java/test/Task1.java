package test;

import android.graphics.Bitmap;

/**
 * Created by admin on 2017/9/29.
 */

public class Task1 implements Task1Intf {


    @Override
    public byte[] task1(byte[] bitmapbs) {
        byte[] curbitmapbs = null;
        curbitmapbs = ColorKMeans.Math(bitmapbs);
        curbitmapbs = Oritenation.Math(curbitmapbs, bitmapbs);
        return curbitmapbs;
    }
}
