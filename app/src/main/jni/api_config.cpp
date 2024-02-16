#include <jni.h>
#include <string>
#include <jni.h>

std::string AU = "YUhSMGNITTZMeTlrWVhOb0xtMXlibTl5WkM1amIyMHY=";
std::string AK = "BP3ZbAcfRZSzi3BiEjMgErXy2qVm2e";
std::string AV = "9876543210fedcba";


extern "C" jstring
Java_com_app_earningpoints_util_Const_getAU(JNIEnv *env, jclass clazz) {
    return env->NewStringUTF(AU.c_str());
}

extern "C" jstring Java_com_app_earningpoints_util_Const_getAK(JNIEnv *env, jclass clazz) {
    return env->NewStringUTF(AK.c_str());
}

extern "C" jstring
Java_com_app_earningpoints_util_Const_getAV(JNIEnv *env, jclass clazz) {
    return env->NewStringUTF(AV.c_str());
}