package com.youle.view;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class ImageZoomView extends View implements Observer
{
	private Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
	private Rect mRectSrc = new Rect();
	private Rect mRectDst = new Rect();
	private float mAspectQuotient;

	private Bitmap myBitmap;
	private ImageZoomState mZoomState;

	public ImageZoomView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	@Override
	public void update(Observable observable, Object data)
	{
		this.invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		if (myBitmap != null && mZoomState != null)
		{
			int viewWidth = this.getWidth();
			int viewHeight = this.getHeight();
			int bitmapWidth = myBitmap.getWidth();
			int bitmapHeight = myBitmap.getHeight();

			float panX = mZoomState.getmPanX();
			float panY = mZoomState.getmPanY();
			float zoomX = mZoomState.getZoomX(mAspectQuotient) * viewWidth
					/ bitmapWidth;// 相当于viewHeight/bitmapHeight*mZoom
			float zoomY = mZoomState.getZoomY(mAspectQuotient) * viewHeight
					/ bitmapHeight;// 相当于viewWidth/bitmapWidth*mZoom

			// Setup source and destination rectangles
			// 这里假定图片的高和宽都大于显示区域的高和宽，如果不是在下面做调整
			mRectSrc.left = (int) (panX * bitmapWidth - viewWidth / (zoomX * 2));
			mRectSrc.top = (int) (panY * bitmapHeight - viewHeight
					/ (zoomY * 2));
			mRectSrc.right = (int) (mRectSrc.left + viewWidth / zoomX);
			mRectSrc.bottom = (int) (mRectSrc.top + viewHeight / zoomY);

			mRectDst.left = this.getLeft();
			mRectDst.top = this.getTop();
			mRectDst.right = this.getRight();
			mRectDst.bottom = this.getBottom();

			// Adjust source rectangle so that it fits within the source image.
			// 如果图片宽或高小于显示区域宽或高（组件大小）或者由于移动或缩放引起的下面条件成立则调整矩形区域边界
			if (mRectSrc.left < 0)
			{
				mRectDst.left += -mRectSrc.left * zoomX;
				mRectSrc.left = 0;
			}
			if (mRectSrc.right > bitmapWidth)
			{
				mRectDst.right -= (mRectSrc.right - bitmapWidth) * zoomX;
				mRectSrc.right = bitmapWidth;
			}

			if (mRectSrc.top < 0)
			{
				mRectDst.top += -mRectSrc.top * zoomY;
				mRectSrc.top = 0;
			}
			if (mRectSrc.bottom > bitmapHeight)
			{
				mRectDst.bottom -= (mRectSrc.bottom - bitmapHeight) * zoomY;
				mRectSrc.bottom = bitmapHeight;
			}

			// 把bitmap的一部分(就是src所包括的部分)绘制到显示区中dst指定的矩形处.关键就是dst,它确定了bitmap要画的大小跟位置
			// 注：两个矩形中的坐标位置是相对于各自本身的而不是相对于屏幕的。
			canvas.drawBitmap(myBitmap, mRectSrc, mRectDst, mPaint);
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom)
	{
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
		this.calculateAspectQuotient();
	}

	public void setImageZoomState(ImageZoomState zoomState)
	{
		if (mZoomState != null)
		{
			mZoomState.deleteObserver(this);
		}
		mZoomState = zoomState;
		mZoomState.addObserver(this);
		invalidate();
	}

	public void setImage(Bitmap bitmap)
	{
		myBitmap = bitmap;
		this.calculateAspectQuotient();
		invalidate();

	}

	private void calculateAspectQuotient()
	{
		if (myBitmap != null)
		{
			mAspectQuotient = (float) (((float) myBitmap.getWidth() / myBitmap
					.getHeight()) / ((float) this.getWidth() / this.getHeight()));
		}
	}
}
