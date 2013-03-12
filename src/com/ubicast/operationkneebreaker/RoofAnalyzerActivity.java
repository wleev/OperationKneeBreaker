package com.ubicast.operationkneebreaker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.ubicast.operationkneebreaker.drawing.DrawableImageView;
import com.ubicast.operationkneebreaker.util.BitmapDecoder;
import com.ubicast.operationkneebreaker.util.BitmapDecoder.BitmapDecoderTask;
import com.ubicast.operationkneebreaker.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.PathShape;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class RoofAnalyzerActivity extends Activity {

	private DrawableImageView photoView;
	private ProgressBar loadingBar;
	private Uri imageUri;
	private Button clearButton, calculateButton;
	
	private int drawableCounter = 0;
	private Bitmap decodedBitmap;
	
	private double estimatedLength;
	private double pixelLength;
	private double imageLength;
	private boolean calculationDoable = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_roof_analyzer);
		
		imageUri = this.getIntent().getParcelableExtra("photoUri");
		photoView = (DrawableImageView) findViewById(R.id.drawableView);
		loadingBar = (ProgressBar) findViewById(R.id.progressBar);
		clearButton = (Button) findViewById(R.id.clr_btn);
		calculateButton = (Button) findViewById(R.id.button1);
		
		input = new EditText(getApplicationContext());
 		
		photoView.setOnTouchListener(photoTouchListener);
		clearButton.setOnClickListener(clearButtonListener);
		calculateButton.setOnClickListener(calculateButtonListener);
	}

	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		
		loadingBar.setVisibility(View.VISIBLE);
		loadingBar.setActivated(true);
		
		BitmapDecoder.BitmapDecoderTask task = new BitmapDecoder.BitmapDecoderTask(photoView);
		System.out.println("image view size :("+photoView.getWidth() +", "+photoView.getHeight()+")");
		
		task.execute(imageUri.getPath());
		try {
			decodedBitmap = task.get();
			photoView.setImageBitmap(decodedBitmap);
		} catch (InterruptedException e) {
			Toast.makeText(getApplicationContext(), "Image loading failed.", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} catch (ExecutionException e) {
			Toast.makeText(getApplicationContext(), "Image loading failed.", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} finally {
			loadingBar.setActivated(false);
			loadingBar.setVisibility(View.INVISIBLE);
		}
		//Toast.makeText(getApplicationContext(), "Image loaded.", Toast.LENGTH_LONG).show();
	}
	
	
	private OnTouchListener photoTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(event.getAction() == MotionEvent.ACTION_UP){
				if(drawableCounter < 2){
					ShapeDrawable boundaryPoint = new ShapeDrawable(new OvalShape());
					boundaryPoint.getPaint().setColor(getResources().getColor(R.color.chestnut_rose));
					boundaryPoint.setBounds((int)event.getX()-5, (int)event.getY()-5, (int)event.getX() + 5, (int)event.getY() + 5);
					photoView.addDrawable(boundaryPoint);
					drawableCounter++;
					
					System.out.println("Drawing oval at ("+event.getX()+", "+event.getY()+")");
				}
				
				if(drawableCounter == 2){
					List<ShapeDrawable> points = photoView.getDrawables();
					Path straight = new Path();
					PointF startPoint = new PointF(points.get(0).getBounds().exactCenterX(), points.get(0).getBounds().exactCenterY());
					PointF endPoint = new PointF(points.get(1).getBounds().exactCenterX(), points.get(1).getBounds().exactCenterY());
					straight.moveTo(startPoint.x, startPoint.y);
					straight.lineTo(endPoint.x, endPoint.y);
					straight.close();
					
					pixelLength =  Math.sqrt(Math.pow((endPoint.x - startPoint.x), 2) + Math.pow((endPoint.y - startPoint.y), 2));

					ShapeDrawable connectingLine = new ShapeDrawable(new PathShape(straight, photoView.getWidth(), photoView.getHeight()));
					connectingLine.getPaint().setColor(getResources().getColor(R.color.chestnut_rose));
					connectingLine.getPaint().setStyle(Paint.Style.STROKE);
					connectingLine.setBounds(0, 0, photoView.getWidth(), photoView.getHeight());
					photoView.addDrawable(connectingLine);
					System.out.println("Drawing straight...");
					
					
					getInputDialog();
				}
			}
			return true;
		}
	};	
	
	private OnClickListener clearButtonListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			photoView.clearDrawables();
			drawableCounter = 0;
			calculationDoable = false;
		}
	};
	
	private OnClickListener calculateButtonListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Camera c = Camera.open(0);
			double focalLength = c.getParameters().getFocalLength();
			double sensorLength = 2.67;
			c.release();
			
			imageLength = photoView.getHeight();
			double distanceToObject = (focalLength * estimatedLength * imageLength)/(sensorLength * pixelLength);
			Toast.makeText(getApplicationContext(), "Distance to the object is should be around "+distanceToObject+" milimetres." , Toast.LENGTH_LONG).show();
		}
	};
	
	private EditText input;
	private void getInputDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Estimated size");
		alert.setMessage("Enter the estimate real world size of the indicated line.");

		// Set an EditText view to get user input 
		
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  estimatedLength = Double.parseDouble(input.getText().toString());
		  calculationDoable = true;
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});

		alert.show();
	}
}
