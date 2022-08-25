package kr.ac.sch.oopsla.rsa.process;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;




public class HeartRate extends TextView{
	
	private boolean imageDisp, mResult, params, oxygen;
	private Bitmap normal, afib, oxBack;
	Typeface font;
	String text;
	private Paint paint,paintPAC;
	float textSize, textWidth;
	int SpO2;
	int minVal;
	private int PAC;
	
	int left,top,right,bottom;
	RectF rect_set_total, rect_set_image;
	
	
	//For the moment this view has 2 uses. If imageDisp is false then this view takes and displays an integer, if it is ture this this displays result images
	public HeartRate(Context context, AttributeSet attrs) {
		super(context, attrs);
		imageDisp = false;
		mResult = true;
		params = false;
		oxygen = false;
		SpO2 = 0;
		minVal = 0;
		PAC = 0;
		
		
		//normal = BitmapFactory.decodeResource(getResources(), R.drawable.res_nor);
		//afib = BitmapFactory.decodeResource(getResources(), R.drawable.res_af);
		// TODO Auto-generated constructor stub
		
		paint = new Paint();
		paint.setColor(Color.WHITE);
		
		paintPAC = new Paint();
		paintPAC.setColor(Color.WHITE);
		
		//font = Typeface.createFromAsset(context.getAssets(), "fonts/DIGIT.TTF");
		//paint.setTypeface(font);
		//setSpO2(0);
	}
	public void setColor(String c){
		paint.setColor(Color.parseColor(c));
		paintPAC.setColor(Color.parseColor(c));

	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		
		minVal = Math.min(heightSize, widthSize);
		//The reason this is done this way is to ensure that this class and progressbar
		// With share the correct dimentions
		setMeasuredDimension((int) (minVal/1.1), minVal);
		
		params = false;
	}
	
	/**
	 * Takes information on whether or not this view
	 * display text or an image.
	 * @param isResult Boolean whether or not this view takes and image
	 * @param result Boolean if good or bad result, false = good, true = bad. If
	 *        isResult = false then the value of result is irrelevant
	 */
	public void setResult(boolean isResult, boolean result)
	{
		imageDisp = isResult;
		mResult = result;
		params = false;
		oxygen = false;
		invalidate();
	}
	
	public void setPAC(int nPAC)
	{
		PAC = nPAC;
		invalidate();
	}
	
	public void setHR(int newHR)
	{
		this.setText(newHR + "");
		params = false;
	}
	
	public void setHR(String st)
	{
		this.setText(st);
		params = false;
	}

	public void onDraw(Canvas canvas)
	{
		if(!params){
			rect_set_total = new RectF(canvas.getClipBounds());
			
			//Sets up graphical location parameters for the plot area.
			left = (int)(rect_set_total.right*0.2);
			top = (int)(rect_set_total.bottom*0.3);
			right = (int) (rect_set_total.right*0.8);
			bottom = (int)(rect_set_total.bottom*0.8);
			
			rect_set_image = new RectF(left,top,right,bottom);
			
			if(!oxygen)
			{
				//textSize = 4*minVal/10;
				textSize = 4*minVal/30;

				paint.setTextSize(textSize);
			}else
			{
				//textSize = 2*minVal/10;
				textSize = 2*minVal/30;

				paint.setTextSize(textSize);
			}
			
			params = true;
		}
			
			if(oxygen)
			{				
				text = (String) this.getText();
				textWidth = paint.measureText(text);
				canvas.drawText(text,14*(rect_set_total.right-rect_set_total.left)/21 - textWidth/2,9*(rect_set_total.bottom-rect_set_total.top)/40 + textSize/2,paint);
				
				text = SpO2 + "";
				textWidth = paint.measureText(text);
				canvas.drawText(text,14*(rect_set_total.right-rect_set_total.left)/21 - textWidth/2,28*(rect_set_total.bottom-rect_set_total.top)/40 + textSize/2,paint);
			}
			else if(!imageDisp)
			{
				text = (String) this.getText();
				
				textWidth = paint.measureText(text);
				//canvas.drawText(text,(right - left)/2 + textWidth/2,(bottom-top)/2 + textSize,paint);
				canvas.drawText(text,(rect_set_total.right-rect_set_total.left)/2 - textWidth/2,(rect_set_total.bottom-rect_set_total.top)/2 + textSize/2,paint);
				
			}else{	
				if(mResult)
				{
					canvas.drawBitmap(afib, null, rect_set_image, null);
				}else
				{
					canvas.drawBitmap(normal, null, rect_set_image, null);
				}
				
				if(PAC == 3)
				{
					text = "PAC:Tri";
					textWidth = paintPAC.measureText(text);
					textSize = minVal/10;
					paintPAC.setTextSize(textSize);
					canvas.drawText(text,(float) (rect_set_total.right*0.85 - textWidth),(float) (rect_set_total.bottom*0.85),paintPAC);
				}else if(PAC == 4)
				{
					text = "PAC:Quad";
					textWidth = paintPAC.measureText(text);
					textSize = minVal/10;
					paintPAC.setTextSize(textSize);
					canvas.drawText(text,(float) (rect_set_total.right*0.85 - textWidth),(float) (rect_set_total.bottom*0.85),paintPAC);
				}
			}

	}
	

}
