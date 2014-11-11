package com.cloay.crefreshlayout.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 
 * @ClassName: BarItem 
 * @author cloay Email:shangrody@gmail.com 
 * @date 2014-11-10 ÏÂÎç7:58:00 
 *
 */
public class BarItem extends View {

	private Point middlePoint;
	private Point startPoint;
	private Point endPoint;
	
	private float lineWidth;
	private int lineColor;
	
	public float translationX;
	public BarItem(Context context) {
		super(context);
	}
	
	public BarItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public BarItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	
	public BarItem(Context context, Point startPoint, Point endPoint, int lineColor, float lineWidth){
		super(context);
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		int x = (startPoint.x + endPoint.x)/2;
		int y = (startPoint.y + endPoint.y)/2;
		this.middlePoint = new Point(x, y);
		
		this.lineColor = lineColor;
		this.lineWidth = lineWidth;
	}

	public void setupFrame(){
		this.layout(this.startPoint.x  + this.middlePoint.x - this.getWidth()/2, 
				this.startPoint.y + this.middlePoint.y - this.getHeight()/2, 
				this.getWidth() + this.startPoint.x + this.middlePoint.x - this.getWidth()/2, 
				this.getHeight() + this.startPoint.y + this.middlePoint.y - this.getHeight()/2);
		Log.v("CRefresh", "X=" + this.getX() + " Y=" + this.getY() + 
				" width=" + this.getWidth() + " height=" + this.getHeight());
	}
	
	public void setHorizontalRandomness(int horizontalRandomness, int dropHeight){
		int randomNum = -horizontalRandomness + (int)(Math.random()*horizontalRandomness*2) + 1;
		this.translationX = randomNum;
		AnimatorSet mAnimatorSet = new AnimatorSet();
		mAnimatorSet.setDuration(800);
		mAnimatorSet.playTogether(
					ObjectAnimator.ofFloat(this, "translationY", this.translationX, -dropHeight)
				);
		mAnimatorSet.start();
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint();  
	    paint.setAntiAlias(true);
	    paint.setColor(lineColor);  
	    paint.setStrokeWidth(lineWidth);
	    paint.setStrokeCap(Cap.ROUND);
	    paint.setStrokeJoin(Join.ROUND);
	    paint.setStyle(Style.STROKE);
	    Path path = new Path();
	    path.moveTo(startPoint.x, startPoint.y);
	    path.lineTo(endPoint.x, endPoint.y);
	    canvas.drawPath(path, paint);
	}
	
	
}
