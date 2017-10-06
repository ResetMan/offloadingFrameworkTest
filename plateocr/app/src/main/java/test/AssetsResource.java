package test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.InputStream;

/**
 * @程序员 蒲小东
 * @功能 读取资源
 */
public class AssetsResource {

	public static Context context = null;
	
	public static Bitmap getBitmap(String fileName) {
		 
		try {
			 InputStream inStream = context.getAssets().open(fileName);
			 Bitmap oriBmp = BitmapFactory.decodeStream(inStream);
			 
			 int Width = oriBmp.getWidth();
			 int Height = oriBmp.getHeight();
			 int dst[] = new int[Width * Height];	 
			 oriBmp.getPixels(dst, 0, Width, 0, 0, Width, Height);
			 
			 Bitmap bitmap = Bitmap.createBitmap(Width, Height, Bitmap.Config.ARGB_8888);
			 bitmap.setPixels(dst, 0, Width, 0, 0, Width, Height);
				
			 return bitmap;
		 } catch (Exception e) {
			 Log.e("xjhxplateocr", e.getMessage());
		 }
		 return null;
	}
	
}
