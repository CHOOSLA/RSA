package kr.ac.sch.oopsla.rsa.androidqa;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

class AnimationView extends View {
  private static final float RADIUS = 20;
  private float x = 30;
  private float y = 30;
  private float initialX;
  private float initialY;
  private float offsetX;
  private float offsetY;
  private Paint myPaint;
  private Paint backgroundPaint;

  public AnimationView(Context context, AttributeSet attrs) {
    super(context, attrs);

    backgroundPaint = new Paint();
    backgroundPaint.setColor(Color.BLUE);

    myPaint = new Paint();
    myPaint.setColor(Color.WHITE);
    myPaint.setAntiAlias(true);
  }

  public boolean onTouchEvent(MotionEvent event) {
    int action = event.getAction();
    switch (action) {
    case MotionEvent.ACTION_DOWN:
      initialX = x;
      initialY = y;
      offsetX = event.getX();
      offsetY = event.getY();
      break;
    case MotionEvent.ACTION_MOVE:
    case MotionEvent.ACTION_UP:
    case MotionEvent.ACTION_CANCEL:
      x = initialX + event.getX() - offsetX;
      y = initialY + event.getY() - offsetY;
      break;
    }
    return (true);
  }

  public void draw(Canvas canvas) {
    super.draw(canvas);
    int width = canvas.getWidth();
    int height = canvas.getHeight();
    canvas.drawRect(0, 0, width, height, backgroundPaint);
    canvas.drawCircle(x, y, RADIUS, myPaint);
    invalidate();
  }
}