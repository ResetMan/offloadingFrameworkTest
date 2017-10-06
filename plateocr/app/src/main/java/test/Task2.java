package test;

import java.io.Serializable;

/**
 * Created by admin on 2017/9/29.
 */

public class Task2 implements Task2Intf ,Serializable{

    @Override
    public byte[][] task2(byte[] curbitmapbs) {
        byte[][] bitmapsbs = SegInEachChar.Math(curbitmapbs);
        byte[] Bmpbs = RecEachCharInMinDis.ClearSmall(bitmapsbs[2]);
        Bmpbs = RecEachCharInMinDis.GetRegion(Bmpbs);
        Bmpbs = RecEachCharInMinDis.Zoom(Bmpbs);
        return bitmapsbs;
    }
}
