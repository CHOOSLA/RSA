package kr.ac.sch.oopsla.rsa.process;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;


import java.util.Arrays;

import kr.ac.sch.oopsla.rsa.R;

public class CustomGraphView2 extends ImageView{
   private int xMax,xMinA,xMaxA,xRange;//True value of xman, and the actual or 'A' values that will be used.
   private int yMax,yMinA,yMaxA,yRange;//True value of ymax, and the actual or 'A' values that will be used.
   private int xAxisSize;
   private double yAxisMin, yAxisMax;
   private Paint paint;// The paint (e.g. style, color) used for drawing
   private double[] yPoints, sorted;
   private int[] upArr, dwArr;
   private int currentPoint;
   private boolean autoScale,message;
   private double min,max;
   private int leftStart, rightStart;

   // Message Variables
   private Bitmap detection;
   private RectF rect_set_message;
   private int xMinM, xMaxM, yMinM, yMaxM;
   private int messageFlicker;

   private int indexUP, indexDW;
   int k = 50;

   // Constructor
   public CustomGraphView2(Context context) {
      super(context);
      //Apply Paint Attributes
      paint = new Paint();
      paint.setColor(Color.WHITE);
      paint.setStrokeWidth(2);

      currentPoint = 0;
      xAxisSize = 60;
	  yPoints = new double[xAxisSize];
	  sorted = yPoints;

	  detection = BitmapFactory.decodeResource(getResources(), R.drawable.image_detect_message);
	  message = false;
	  messageFlicker = 0;
	  autoScale = true;

	  upArr = null;
	  dwArr = null;
	  indexUP = 0;
	  indexDW = 0;
	  leftStart = 70;
	  rightStart = 185;
   }

   public CustomGraphView2(Context context, AttributeSet attrs) {
	      super(context,attrs);
	      //Apply Paint Attributes
	      paint = new Paint();
	      paint.setColor(Color.BLACK);
	      paint.setStrokeWidth(2);
	      
	      currentPoint = 0;
	      xAxisSize = 60;
		  yPoints = new double[xAxisSize];		  
		  sorted = new double[xAxisSize];
		  
		  detection = BitmapFactory.decodeResource(getResources(), R.drawable.image_detect_message);
		  message = false;
		  messageFlicker = 0;
		  autoScale = true;
		  
		  upArr = null;
		  dwArr = null;
		  indexUP = 0;
		  indexDW = 0;
		  leftStart = 70;
		  rightStart = 185;
	   }
   
   @Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
	    int heightSize = MeasureSpec.getSize(heightMeasureSpec);
	    
	    
	    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
	    /*
	    if(widthSize > (heightSize*5)/4)
	    {
	    	widthSize = (heightSize*5)/4;
	    }else if(heightSize > (widthSize * 4)/5)
	    {
	    	heightSize = (widthSize * 4)/5;
	    }*/
	    //int widthSize = (heightSize*5)/4;
	    
		int yMax_Temp = heightSize;
		//Ratio of width to height in the background image
		setMeasuredDimension(widthSize, heightSize);
		xMax = widthSize;
		yMax = yMax_Temp;
		
		//On Measure Create relevant values
		/*xMinA = xMax/3;
		xMaxA = 2*xMax/3;
		xRange = xMaxA - xMinA;
		yMinA = yMax/4;
		yMaxA = 3*yMax/4;
		yRange = yMaxA - yMinA;
		*/
		xMinA = 60;
		xMaxA = xMinA+690;
		xRange = (xMaxA-xMinA);
		
		yMinA = 50;
		yMaxA = yMinA+640;
		yRange = yMaxA-yMinA;

		//Sets up positioning for detection message
		/*xMinM = xMax/5;
		xMaxM = xMax*4/5;
		yMinM = yMax*2/5;
		yMaxM = (int) ((xMaxM - xMinM)*0.208 + yMinM);
		*/
		xMinM = xMinA;
		xMaxM = xMaxA;
		yMinM = yMinA;
		yMaxM = yMaxA;

		rect_set_message = new RectF(xMinM,yMinM,xMaxM,yMaxM);
		
		//axisLimits(xAxisSize, 0, 256);
		
	}
   
   public void DetectMessage(boolean newMessage)
   {
	   message = newMessage;
   }
  
   // Called back to draw the view. Also called by invalidate().
   // Called every frame preview.
   @Override
   protected void onDraw(Canvas canvas) {
	   
	// Get new (x,y) position
	   int xcd1,ycd1;
	   int xcd2,ycd2;
	   indexUP = 0;
	   indexDW = 0;

	   if(currentPoint > 0)
	   {
		   int greenBoxX1, greenBoxY1, greenBoxX2, greenBoxY2;
		   greenBoxX1 = (int) (xMinA + leftStart*xRange/xAxisSize);
		   greenBoxX2 = (int) (xMinA + rightStart*xRange/xAxisSize);
		   paint.setColor(0xC9F9FCAA);
		   canvas.drawRect(greenBoxX1, 55, greenBoxX2, yMax-55, paint);
		   
//		   if(leftStart <= p && rightStart >= p){
//			   paint.setColor(Color.GREEN);
//			   canvas.drawLine(xcd1,0,xcd1,yMax, paint);
//		   }

		   for(int p = 0; p < currentPoint - 1; p++)
		   {
			   //Plots the graph according to the current ranges,maximum and minumum
			   //Graphing is based off lines with an x1,y1 and x2,y2
			   
			   //for each loop xcd1 and ycd1 represents the point at p, while running thorugh the points
			   xcd1 = (int) (xMinA + p*xRange/xAxisSize);
			   ycd1 = (int) (yMaxA - yRange*((yPoints[p]-yAxisMin)/(yAxisMax - yAxisMin)));
			   
			   //for each loop xcd1 and ycd1 represents the point at p, running thorugh the points
			   xcd2 = (int) (xMinA + (p+1)*xRange/xAxisSize);
			   ycd2 = (int) (yMaxA - yRange*((yPoints[p+1]-yAxisMin)/(yAxisMax - yAxisMin)));
			   

			   paint.setColor(Color.BLACK);
			   canvas.drawLine(xcd1,ycd1,xcd2,ycd2, paint);

			   if(upArr!=null){
				   if(indexUP<upArr.length){
					   if(p+1 == (int)upArr[indexUP]){
						   paint.setColor(Color.RED);
						   canvas.drawCircle(xcd2,ycd2,10, paint);
						   indexUP++;
					   }
				   }
			   }
			   if(dwArr!=null){
				   if(indexDW<dwArr.length){
					   if(p+1 == (int)dwArr[indexDW]){
						   paint.setColor(Color.BLUE);
						   canvas.drawCircle(xcd2,ycd2,10, paint);
						   indexDW++;
					   }
				   }
			   }
			   
		   }
		   
//		   paint.setColor(Color.BLACK);
//		   paint.setTextSize(27);
//		   canvas.drawText(""+(int)yAxisMin, 5, (float) yMaxA+10, paint);
//		   canvas.drawText(""+(int)yAxisMax, 5, (float) yMinA+10, paint);
//		   
//		   canvas.drawText("0", 40, 735, paint);
//		   canvas.drawText("155", 650, 735, paint);

	   }
	   
	   //On Draw for message
	   messageFlicker++;
	   if(messageFlicker >= 2000000000)
	   {
		   messageFlicker = 0;
	   }
	   if(message && messageFlicker%30 > 15)
	   {
		   canvas.drawBitmap(detection, null, rect_set_message, null);
	   }
	   
      //invalidate();  // Force a re-draw on each frame!!!!! IMPORTANT!!!
   }
   
   public void addArr(double[] HrArr, double[] up, double[] dw, int leftStart, int rightStart){
	   yPoints = new double[HrArr.length];
	   sorted = new double[yPoints.length];
	   System.arraycopy(HrArr, 0, yPoints, 0, HrArr.length);
	   
	   upArr = new int[up.length];
	   dwArr = new int[dw.length];
	   for(int i=0;i<up.length;i++){
		   upArr[i] = (int) up[i];
	   }
	   for(int i=0;i<dw.length;i++){
		   dwArr[i] = (int) dw[i];
	   }


	   System.arraycopy(yPoints, 0, sorted, 0, yPoints.length);
	   Arrays.sort(sorted);

	   yAxisMin = 40;
	   yAxisMax = 120;
	   xAxisSize = HrArr.length;
	   
	   currentPoint = yPoints.length;
	   
	   this.leftStart = leftStart;
	   this.rightStart = rightStart;
	   
	   invalidate();
   }
   
   public void addArr(double[] HrArr){
	   yPoints = new double[HrArr.length];
	   sorted = new double[yPoints.length];
	   System.arraycopy(HrArr, 0, yPoints, 0, HrArr.length);
	   	   
	   System.arraycopy(yPoints, 0, sorted, 0, yPoints.length);
	   Arrays.sort(sorted);

	   yAxisMin = 40;
	   yAxisMax = 120;
	   xAxisSize = HrArr.length;
	   
	   currentPoint = yPoints.length;

	   invalidate();
   }

   public void addPoint(double newPoint)
   {
	   
	   yPoints[currentPoint] = newPoint;
		   
	   if (currentPoint >= xAxisSize - 1)
	   {
		   System.arraycopy(yPoints, 1, yPoints, 0, xAxisSize - 1);
	   }
	  
	   //AutoScaling
	   //May not be the most efficent way but is the cleanest way codewise.
	   /*
	   System.arraycopy(yPoints, 0, sorted, 0, currentPoint + 1);
	   Arrays.sort(sorted);
	   axisLimits(xAxisSize, sorted[0], sorted[sorted.length-1]);
	   */
	   if (currentPoint < xAxisSize - 1)
	   {
		   currentPoint++;
		   
		   //Set up autoscaling
		   sorted = new double[currentPoint + 1];
	   }
	   
	   //invalidate();
   }
   /**
    * Rescales the the graph
    * @param xSize represents the amount of points displayed on the graph
    * @param MinY represents the minimum y
    * @param MaxY represents the maximum y
    */
   public void axisLimits(int xSize, double MinY, double MaxY)
   {
	   if(xSize != xAxisSize)
	   {
		   xAxisSize = xSize;
		   yPoints = new double[xAxisSize];
		   currentPoint = 0;
		   
		   System.out.println("WOAH WTH!!!!  " + xAxisSize);
	   }
	   yAxisMin = MinY;
	   yAxisMax = MaxY;
   }
   public void autoScale(boolean auto)
   {
	   autoScale = auto;
   }
}