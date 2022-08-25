package kr.ac.sch.oopsla.rsa.process;


public class ImageProcess {	
	public static boolean ImageProcessing(int width, int height, 
			byte[] NV21FrameData, int [] pixels) {
		return nativeImageProcessing(width,height,NV21FrameData,pixels);
	}

	private static native boolean nativeImageProcessing(int width, int height, 
			byte[] NV21FrameData, int [] pixels);
}
