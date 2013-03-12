package com.ubicast.operationkneebreaker.drawing;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class DrawableImageView extends ImageView {

	public DrawableImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public DrawableImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public DrawableImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	private List<ShapeDrawable> drawables = new ArrayList<ShapeDrawable>();
	
	

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for(ShapeDrawable sd : drawables){
			System.out.println("Drawing shapes ...");
			sd.draw(canvas);
		}
		canvas.save();
	}
	
	public void addDrawable(ShapeDrawable drawable){
		drawables.add(drawable);
		this.invalidate();
	}
	
	public void clearDrawables(){	
		drawables.clear();
		this.invalidate();
	}
	
	public List<ShapeDrawable> getDrawables(){
		return this.drawables;
	}

}
