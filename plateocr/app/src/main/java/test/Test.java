package test;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.xjhtxx.pojecker.plateocr.R;


public class Test extends Activity {

//	private Bitmap bitmap = null;
//	private Bitmap curbitmap = null;
//	private Bitmap[] bitmaps = null;

	private byte[] bitmapbs = null;
	private byte[] curbitmapbs = null;
	private byte[][] bitmapsbs = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final ImageView imageView1 = (ImageView)findViewById(R.id.imageView1);
        final EditText editText1 = (EditText)findViewById(R.id.editText1);
        
        final ImageView imageP1 = (ImageView)findViewById(R.id.imageP1);
        final ImageView imageP2 = (ImageView)findViewById(R.id.imageP2);
        final ImageView imageP3 = (ImageView)findViewById(R.id.imageP3);
        final ImageView imageP4 = (ImageView)findViewById(R.id.imageP4);
        final ImageView imageP5 = (ImageView)findViewById(R.id.imageP5);
        final ImageView imageP6 = (ImageView)findViewById(R.id.imageP6);
        final ImageView imageP7 = (ImageView)findViewById(R.id.imageP7);
        
        final EditText editP = (EditText)findViewById(R.id.editP);
        
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/xjhxcp/"+editText1.getText().toString().trim()+".jpg", options);
        Bitmap curbitmap = bitmap;
        imageView1.setImageBitmap(bitmap);

		bitmapbs = Tools.Bitmap2Bytes(bitmap);
		curbitmapbs = Tools.Bitmap2Bytes(curbitmap);

        Button button3 = (Button)findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {		
			public void onClick(View arg0) {
				BitmapFactory.Options options = new BitmapFactory.Options();
		        options.inPreferredConfig = Config.ARGB_8888;
		        Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/xjhxcp/"+editText1.getText().toString().trim()+".jpg", options);
		        Bitmap curbitmap = bitmap;

				bitmapbs = Tools.Bitmap2Bytes(bitmap);
				curbitmapbs = Tools.Bitmap2Bytes(curbitmap);

		        imageView1.setImageBitmap(bitmap);
			}
		});
        //
        Button button1 = (Button)findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
//				curbitmap = ColorKMeans.Math(bitmap);
//				curbitmap = Oritenation.Math(curbitmap, bitmap);
//				imageView1.setImageBitmap(curbitmap);


				// 我修改了
				curbitmapbs = ColorKMeans.Math(bitmapbs);
				curbitmapbs = Oritenation.Math(curbitmapbs, bitmapbs);
				Bitmap curbitmap = Tools.Bytes2Bitmap(curbitmapbs);
				imageView1.setImageBitmap(curbitmap);

			}
		});
        
        Button button2 = (Button)findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				if (PlateNumberGroup.AlreadyChecked) {
//					bitmaps = SegInEachChar.Math(curbitmap);
//					imageP1.setImageBitmap(bitmaps[0]);
//					imageP2.setImageBitmap(bitmaps[1]);
//					imageP3.setImageBitmap(bitmaps[2]);
//
//					Bitmap Bmp = RecEachCharInMinDis.ClearSmall(bitmaps[2]);
//				    Bmp = RecEachCharInMinDis.GetRegion(Bmp);
//				    Bmp = RecEachCharInMinDis.Zoom(Bmp);
//				    imageP4.setImageBitmap(Bmp);
//
//					//imageP4.setImageBitmap(bitmaps[3]);
//					imageP5.setImageBitmap(bitmaps[4]);
//					imageP6.setImageBitmap(bitmaps[5]);
//					imageP7.setImageBitmap(bitmaps[6]);
					bitmapsbs = SegInEachChar.Math(curbitmapbs);
					imageP1.setImageBitmap(Tools.Bytes2Bitmap(bitmapsbs[0]));
					imageP2.setImageBitmap(Tools.Bytes2Bitmap(bitmapsbs[1]));
					imageP3.setImageBitmap(Tools.Bytes2Bitmap(bitmapsbs[2]));

					byte[] Bmpbs = RecEachCharInMinDis.ClearSmall(bitmapsbs[2]);
					Bmpbs = RecEachCharInMinDis.GetRegion(Bmpbs);
					Bmpbs = RecEachCharInMinDis.Zoom(Bmpbs);
					Bitmap Bmp = Tools.Bytes2Bitmap(Bmpbs);
					imageP4.setImageBitmap(Bmp);


					imageP5.setImageBitmap(Tools.Bytes2Bitmap(bitmapsbs[4]));
					imageP6.setImageBitmap(Tools.Bytes2Bitmap(bitmapsbs[5]));
					imageP7.setImageBitmap(Tools.Bytes2Bitmap(bitmapsbs[6]));
				}
			}
		});
        
        Button button4 = (Button)findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {		
			public void onClick(View arg0) {
				if (PlateNumberGroup.AlreadyChecked) {
					AssetsResource.context = Test.this;
					String cph = RecEachCharInMinDis.Math(bitmapsbs);
					editP.setText(cph);
				}
			}
        });
    }
}