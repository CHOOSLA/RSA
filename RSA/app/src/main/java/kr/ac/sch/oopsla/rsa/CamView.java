package kr.ac.sch.oopsla.rsa;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import kr.ac.sch.oopsla.rsa.process.DataSet;
import kr.ac.sch.oopsla.rsa.process.CustomGraphView;
import kr.ac.sch.oopsla.rsa.process.HeartRate;
import kr.ac.sch.oopsla.rsa.process.ImageProcess;
import kr.ac.sch.oopsla.rsa.process.PeakSync;


import android.content.Context;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;

class CamView extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private final String TAG = "CAM_VIEW";

    // Camera Object
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Context context;
    private int pixel_count;
    private double avgR;
    final static int RESOLUTION_X = 64; // 176
    final static int RESOLUTION_Y = 32; // 144

    double value = 0.0;
    int frameHeight;
    int frameWidth;
    int rgb[];
    byte[] data2;

    private HeartRate mHeartRate = null;
    private CustomGraphView mHeartView = null;

    private Button startButton = null, stopButton = null;
    private DataSet fullSignal;
    private DataSet signal, leftNormal, rightNormal, Deepbreathing;
    private Parameters parameters;

    // signal check
    private boolean isHrRemovable = false;
    private boolean isStartClicked = false;
    private boolean isCleanSignal = false;
    private boolean haveToStopProgress = true;
    private boolean clicked = false;
    private double start_time = 0;
    private double interval_time = 0;
    private double clicked_time = 0;

    private boolean isMessageReady = false;
    private double beforeTime = 0;
    private int FS = 30;

    private int lastHrAvg = 0, recentHr = 0, lastHR = 0, lastHRNotSaving = 0;
    private int sigCheckFlag = 0; // 사이즈 미달 : 1, 첨도 : 2, 변동정도 : 3, hr 비교 : 4
    private int count = 0;
    private PeakSync sync;
    AsyncTask<DataSet, Void, Integer> HRsync;
    private double HR = 0;

    int temp = 0;

    private String fn,ln, posi, exer;

    public CamView(Context context, Camera camera, CustomGraphView heartView, HeartRate heartRate,
                    String PerFn, String PerLn, String pos, String exe,
                    Button startButton, Button stopButton) {
        super(context);

        this.context = context;
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
        fullSignal = new DataSet();
        signal = new DataSet();

        leftNormal = new DataSet();
        rightNormal = new DataSet();
        Deepbreathing = new DataSet();

        mHeartView = heartView;
        mHeartRate = heartRate;
        fn = PerFn;
        ln = PerLn;
        posi = pos;
        exer = exe;

        this.startButton = startButton;
        this.stopButton = stopButton;

        startButton.setEnabled(false);
        stopButton.setEnabled(false);

        mHeartView.setMessageLoading();
        mHeartView.startFlicking();
        start_time = System.currentTimeMillis();

    }
    public DataSet getleftNormal() {
        return leftNormal;
    }

    public DataSet getrightNormal() {
        return rightNormal;
    }

    public DataSet getDeepbreathing() {
        return Deepbreathing;
    }

    public DataSet getSignal() {
        return signal;
    }

    public DataSet getFulsignal(){
        return fullSignal;
    }


    // set Flash
    public void setFlash(boolean set) {
        Parameters params = mCamera.getParameters();
        if (set) {
            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
        } else {
            params.setFlashMode(Parameters.FLASH_MODE_OFF);
        }

        mCamera.setParameters(params);
    }

    private Size getMinimumPreviewSize(List<Size> sizes, int w, int h) {
        if (sizes == null)
            return null;

        Size optimalSize = null;

        double previousRatio = 0;
        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            System.out.println(size.width + " " + size.height);
            double ratio = size.width * size.height;
            if (optimalSize == null || ratio < previousRatio) {
                optimalSize = size;
                previousRatio = ratio;
            }
        }
        System.out.println("Optimal: " + optimalSize.width + " " + optimalSize.height);
        return optimalSize;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub
        try {
            // init camera
            Parameters params = mCamera.getParameters();

            List<int[]> fpslist = params.getSupportedPreviewFpsRange();
            Log.d("camera","size= " + fpslist.size());
            for(int i=0; i<fpslist.size(); i++){
                Log.d("[GENE] cemera", i + " fps= " + fpslist.get(i)[Parameters.PREVIEW_FPS_MIN_INDEX]);
                Log.d("[GENE] cemera", i + " fps= " + fpslist.get(i)[Parameters.PREVIEW_FPS_MAX_INDEX]);
            }

            List<Size> sizes = params.getSupportedPreviewSizes();
            Size optimalSize = getMinimumPreviewSize(sizes, width, height);
            params.setPreviewFpsRange(FS*1000, FS*1000);
            params.setPreviewSize(optimalSize.width, optimalSize.height);
            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
            params.setExposureCompensation(params.getMaxExposureCompensation());
            mCamera.setParameters(params);
            mCamera.setPreviewDisplay(mHolder);
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        mCamera = Camera.open(0);
    }

    // A method to check to see if this cameras has torch mode available.
    private boolean hasTorch(Camera mCamera) {
        Parameters params = mCamera.getParameters();
        List<String> flashModes = params.getSupportedFlashModes();
        if (flashModes == null) {
            return false;
        }

        for (String flashMode : flashModes) {
            if (Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
                return true;
            }
        }

        return false;
    }

    public void stopView() {
        parameters = mCamera.getParameters();
        if (hasTorch(mCamera)) {
            parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
        }
        mCamera.setParameters(parameters);
        mCamera.stopPreview();
    }

    // get RGB Average ROI (not only Red)
    private double getRedAvrage(int[] rgb) {
        int sum = 0;

        for (int i = 0; i < RESOLUTION_Y; i++) {
            for (int j = 0; j < RESOLUTION_X; j++) {
                int red = rgb[i * (RESOLUTION_X) + j];
                red = Color.green(red); // get green
                sum += red;
            }
        }
        return ((double) sum / (double) ((RESOLUTION_Y) * (RESOLUTION_X)));
    }

    public void signal_initialization() {
        fullSignal.clear();
        signal.clear();
        isStartClicked = false;
        start_time = System.currentTimeMillis();
        beforeTime = 0;
        isMessageReady = false;
        mHeartView.setMessageLoading();
        mHeartView.startFlicking();
        startButton.setEnabled(false);
        stopButton.setEnabled(false);


        mHeartRate.setHR(0);
        haveToStopProgress = true;

        isHrRemovable = false;
        isCleanSignal = false;
        lastHR = 0;
        count = 0;
        lastHRNotSaving = 0;
    }

    public void setStartState(){
        isStartClicked = true;
    }

    public boolean getStartState(){
        return isStartClicked;
    }

    public boolean getHaveToStopProgress(){
        return haveToStopProgress;
    }
    // Camera.PreviewCallback stuff:
    // ------------------------------------------------------
    public void onPreviewFrame(byte[] data, Camera cam) {
        frameHeight = mCamera.getParameters().getPreviewSize().height;
        frameWidth = mCamera.getParameters().getPreviewSize().width;
        int rgb[] = new int[frameWidth * frameHeight];

        // get RGB Data using C code
        ImageProcess.ImageProcessing(frameWidth, frameHeight, data, rgb);

        // 그리기
        value = getRedAvrage(rgb);

        interval_time = (double) (System.currentTimeMillis() - start_time);

        mHeartView.addPoint(value); // graph reversal
        signal.addPointTime(value, interval_time);

        if(interval_time >= 10000){
            if(isStartClicked){
                haveToStopProgress = false;
                mHeartView.stopFlicking();
                Deepbreathing.addPointTime(value, interval_time);
            }
            else{
                startButton.setEnabled(true);
                stopButton.setEnabled(true);
                isMessageReady = true;
            }

            // Every 2 seconds.
            if (interval_time >= beforeTime) {
                beforeTime += 2000;

                try {
                    if (sync != null)
                        HR = HRsync.get();
                    temp = (int) HR;
                    Log.e(TAG, "ppg:" + HR);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                sync = new PeakSync();
                HRsync = sync.execute(signal);

                mHeartRate.setHR((int) HR);
                signal.addHRPoint(HR);
            }
        }
    }
}

