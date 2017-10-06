package test;

/**
 * Created by admin on 2017/9/29.
 */

public class Task3 implements Task3Intf {
    @Override
    public String task3(byte[][] bitmapsbs) {
        String cph = RecEachCharInMinDis.Math(bitmapsbs);
        return cph;
    }
}
