package test;

import java.io.Serializable;

/**
 * Created by admin on 2017/9/29.
 */

public class Task3 implements Task3Intf ,Serializable{
    @Override
    public String task3(byte[][] bitmapsbs) {
        String cph = RecEachCharInMinDis.Math(bitmapsbs);
        return cph;
    }
}
