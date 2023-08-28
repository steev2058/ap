//
// Created by ASUS on 6/26/2022.
//

#include <jni.h>
#include <string>
extern "C"
JNIEXPORT jstring JNICALL

Java_com_apps2you_albaraka_Keys_encryptionKey(JNIEnv *env, jobject thiz) {
    std::string ENC_KEY = "Pl4WODjq4k";
    return env->NewStringUTF(ENC_KEY.c_str());
}