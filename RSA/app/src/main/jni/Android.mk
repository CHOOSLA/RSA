LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

# opencv library
# 이것은 인수인계 받을 때 상황에 맞게 경로 설정을 해야함
OPENCVROOT:= E:\RSA\RSA\sdk
OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED
include ${OPENCVROOT}\native\jni\OpenCV.mk


LOCAL_MODULE    := native-lib
LOCAL_SRC_FILES := ImageProcess.cpp
LOCAL_LDLIBS += -llog

include $(BUILD_SHARED_LIBRARY)