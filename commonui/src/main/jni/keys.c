#include <jni.h>

JNIEXPORT jstring JNICALL

Java_cessini_technology_commonui_activity_GridActivity_AKey(JNIEnv *env, jobject instance) {

return (*env)-> NewStringUTF(env, "AKIASGPFZLBMV25FCLWJ");
}


JNIEXPORT jstring JNICALL
Java_cessini_technology_commonui_activity_GridActivity_ASecret(JNIEnv *env, jobject instance) {

return (*env)-> NewStringUTF(env, "II7gOEaglLiEg0sGXxNk85IEFA0LfBpxoTJwttUH");
}
