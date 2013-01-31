#include <string.h>
#include <jni.h>

jstring
Java_com_github_avereshchagin_hpc_utils_NativeLibraryLoader_getString(
    JNIEnv* env,
    jobject thiz )
{
    return (*env)->NewStringUTF(env, "From native code with love");
}
