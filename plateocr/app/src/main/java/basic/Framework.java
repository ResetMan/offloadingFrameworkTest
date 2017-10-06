package basic;

import android.app.Application;
import android.content.Context;

/**
 * Created by admin on 2017/10/6.
 */

public class Framework extends Application{
    private static Context context;

    public void onCreate() {
        context = getApplicationContext();
        Utils.frameworkInit();
    }

    public static Context getContext() {
        return context;
    }
}
