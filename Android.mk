LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_STATIC_JAVA_LIBRARIES := \
        droideic \
        android-support-v4 \
        android-support-v7-appcompat \
        android-support-design

LOCAL_SRC_FILES := \
        $(call all-java-files-under, java)

LOCAL_RESOURCE_DIR += $(LOCAL_PATH)/res \
        frameworks/support/v7/appcompat/res \
        frameworks/support/design/res

LOCAL_PROGUARD_ENABLED := disabled
LOCAL_AAPT_FLAGS += \
        --auto-add-overlay \
        --extra-packages android.support.v7.appcompat \
        --extra-packages android.support.design \
        --extra-packages com.droideic.app

LOCAL_PACKAGE_NAME := txrxservice
LOCAL_SDK_VERSION := current

LOCAL_CERTIFICATE := platform

LOCAL_MODULE_TARGET_ARCHS := arm arm64

LOCAL_MULTILIB := both

LOCAL_PROPRIETARY_MODULE := true

include $(BUILD_PACKAGE)
