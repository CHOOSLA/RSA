#APP_STL := gnustl_static
APP_STL := c++_static
APP_CPPFLAGS := -frtti -fexceptions
#APP_ABI := armeabi-v7a
APP_ABI := arm64-v8a x86 x86_64
#APP_ABI := all
APP_PLATFORM := android-29

NDK_TOOLCHAIN_VERSION := clang

APP_BUILD_SCRIPT := Android.mk