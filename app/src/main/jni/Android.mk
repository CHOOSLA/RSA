LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

OPENCVROOT:=C:\Users\Administrator\Downloads\OpenCV-android-sdk\sdk
OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED

include	$(OPENCVROOT)\native\jni\OpenCV.mk

#include C:\Users\YANG\Desktop\OpenCV-2.4.9-android-sdk\sdk\native\jni\OpenCV.mk
#LOCAL_SRC_FILES  += ImageProcessing_jni.cpp
#LOCAL_SRC_FILES  += spline.cpp

LOCAL_MODULE		:= jnilib
LOCAL_SRC_FILES		:= ImageProcessing_jni.cpp
LOCAL_C_INCLUDES	+= $(LOCAL_PATH)
LOCAL_LDLIBS		+= -llog -ldl
LOCAL_CPPFLAGS := -frtti -fexceptions

include $(BUILD_SHARED_LIBRARY)