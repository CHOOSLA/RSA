#include <jni.h>
#include "kr_ac_sch_oopsla_rsa_process_ImageProcess.h"

#include <opencv2/opencv.hpp>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc_c.h>
#include <opencv2/highgui.hpp>
#include <android/log.h>
using namespace std;
using namespace cv;


extern "C" {
void inline process(unsigned char *src, unsigned char *dst, int width, int height) {
    Mat mGraySrc(height + height / 2, width, CV_8UC1, (unsigned char *) src);
    Mat mSrc(height, width, CV_8UC4);
    Mat mResult(height, width, CV_8UC4, (unsigned char *) dst);
    Mat channel[4];
    cvtColor(mGraySrc, mResult, CV_YUV420sp2BGRA);
}

JNIEXPORT jboolean JNICALL Java_kr_ac_sch_oopsla_rsa_process_ImageProcess_nativeImageProcessing
        (JNIEnv *env, jclass thiz, jint width, jint height, jbyteArray NV21FrameData,
         jintArray outPixels) {
    jbyte *pNV21FrameData = env->GetByteArrayElements(NV21FrameData, 0);
    jint *poutPixels = env->GetIntArrayElements(outPixels, 0);
    process((unsigned char *) pNV21FrameData, (unsigned char *) poutPixels, (int) width,
            (int) height);

    env->ReleaseByteArrayElements(NV21FrameData, pNV21FrameData, 0);
    env->ReleaseIntArrayElements(outPixels, poutPixels, 0);
    return true;
}
}

