package com.ubicast.operationkneebreaker;

import java.io.File;

import com.ubicast.operationkneebreaker.state.MainMenuActivityState;
import com.ubicast.operationkneebreaker.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MainMenuActivity extends Activity {

    private static final int TAKE_A_PHOTO_CODE = 7331;

    
    /**
     * The state of the choices in which the user finds himself.
     */
    private MainMenuActivityState mMainMenuState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mMainMenuState = MainMenuActivityState.RoofQuestion;

        setContentView(R.layout.activity_main_menu);        
        
        //add button click handlers
        findViewById(R.id.btn_no).setOnClickListener(mNoButtonClickHandler);
        findViewById(R.id.btn_yes).setOnClickListener(mYesButtonClickHandler);
    }
    
    View.OnClickListener mYesButtonClickHandler = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(mMainMenuState)
			{
				case RoofQuestion:
					transitionToState(MainMenuActivityState.TakeAPicture);
					break;
				case GoingToARoofQuestion:
					transitionToState(MainMenuActivityState.RoofQuestion);
					break;
				default:
					transitionToState(MainMenuActivityState.TakeAPicture);
					break;
			}			
		}
	};
	
    View.OnClickListener mNoButtonClickHandler = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(mMainMenuState)
			{
				case RoofQuestion:
					transitionToState(MainMenuActivityState.GoingToARoofQuestion);
					break;
				case GoingToARoofQuestion:
					finish();
					break;
				default:
					transitionToState(MainMenuActivityState.TakeAPicture);
					break;
			}			
		}
	};
	
	private void transitionToState(MainMenuActivityState state){
		TextView textView = (TextView) findViewById(R.id.textView1);
		Button yesButton = (Button) findViewById(R.id.btn_yes);
		Button noButton = (Button) findViewById(R.id.btn_no);

		//in most cases we need these buttons
		yesButton.setVisibility(View.VISIBLE);
		noButton.setVisibility(View.VISIBLE);
		switch(state)
		{
		case TakeAPicture:
			this.mMainMenuState = state;
			textView.setText(R.string.tk_pic_roof);
			yesButton.setVisibility(View.INVISIBLE);
			noButton.setVisibility(View.INVISIBLE);
			loadCameraActivityWithDelay(3);
			break;
		case GoingToARoofQuestion:
			this.mMainMenuState = state;
			textView.setText(R.string.u_get_roof);
			noButton.setText("Fuck dat shit, I'm a pussy");
		case RoofQuestion:
			this.mMainMenuState = state;
			textView.setText(R.string.u_get_roof);
			noButton.setText(R.string.No);
		}
	}

	private void loadCameraActivityWithDelay(int i) {
		Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File roofDir = new File(Environment.getExternalStorageDirectory()+"/yo_roofs/");
		if(!roofDir.exists()){
			roofDir.mkdir();
		}
		File image = new File(roofDir,"yo_last_roof.jpg");
		camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
		
		startActivityForResult(camera, TAKE_A_PHOTO_CODE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == TAKE_A_PHOTO_CODE){
			if(resultCode == Activity.RESULT_OK){
				Uri photoUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory()+"/yo_roofs/"+"yo_last_roof.jpg"));
				Toast.makeText(this, "YO ROOF ON FIRE BRAH, IT SAVED AT" +
	                     photoUri, Toast.LENGTH_LONG).show();
			
				Intent intent = new Intent(getApplicationContext(), RoofAnalyzerActivity.class);
				intent.putExtra("photoUri", photoUri);
				
				startActivity(intent);
			}
		}
	}
}
