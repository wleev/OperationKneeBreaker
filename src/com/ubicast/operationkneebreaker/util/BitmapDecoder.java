package com.ubicast.operationkneebreaker.util;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

public class BitmapDecoder {

	public static int calculateInSampleSize(
	            BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	
	        // Calculate ratios of height and width to requested height and width
	        final int heightRatio = Math.round((float) height / (float) reqHeight);
	        final int widthRatio = Math.round((float) width / (float) reqWidth);
	
	        // Choose the smallest ratio as inSampleSize value, this will guarantee
	        // a final image with both dimensions larger than or equal to the
	        // requested height and width.
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }
	
	    return inSampleSize;
	}
	
	public static Bitmap decodeSampledBitmapFromFilePath(String path, int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(path, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(path, options);
	}
	
	private static int findClosestPowerOfTwo(int number)
	{
		double powerTwo = Math.log((double) number) / Math.log(2);
		return (int) Math.pow(2, (int) Math.round(powerTwo));
	}
	
	public static class BitmapDecoderTask extends AsyncTask<String, Void, Bitmap>
	{
		private final WeakReference<View> imageViewReference;


		public BitmapDecoderTask(View view)
		{
			imageViewReference = new WeakReference<View>(view);
		}
		
		@Override
		protected Bitmap doInBackground(String... params) {
			String path = params[0];
			return decodeSampledBitmapFromFilePath(path, imageViewReference.get().getWidth(), imageViewReference.get().getHeight());
		}

		@Override
		protected void onPostExecute(Bitmap result) {

		}
		
		
		
	}
}
