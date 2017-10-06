package test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by admin on 2017/9/28.
 */

public class Tools {

    public static byte[] Bitmap2Bytes(Bitmap bm) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();

    }


    public static Bitmap Bytes2Bitmap(byte[] b) {
        if(b.length != 0) {
//            mutableBitmap=immutableBmp.copy(Bitmap.Config.ARGB_8888, true);
            return BitmapFactory.decodeByteArray(b, 0, b.length).copy(Bitmap.Config.ARGB_8888, true);
        } else {
            return null;
        }
    }

}
