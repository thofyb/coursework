#include <sys/syscall.h>
#include <pthread.h>
#include <sys/types.h>
#include <unistd.h>
#include <cerrno>
#include <android/log.h>
#include <jni.h>

//#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "my module name", __VA_ARGS__)


extern "C"
JNIEXPORT void JNICALL
Java_com_example_inlining_MainActivity_00024CalcTask_testJNI(JNIEnv *env, jobject thiz) {
    __android_log_print(ANDROID_LOG_DEBUG, "INLINING_JNI", "lmao, this works");
}

void setCurrentThreadAffinityMask(int mask) {
    int err, syscallres;
    pid_t pid = gettid();
    syscallres = syscall(__NR_sched_setaffinity, pid, sizeof(mask), &mask);
    if (syscallres) {
        err = errno;
        __android_log_print(ANDROID_LOG_ERROR, "threadAffinity", "Error in the syscall setaffinity: mask=%d=0x%x err=%d=0x%x", mask, mask, err, err);
//        LOGE("Error in the syscall setaffinity: mask=%d=0x%x err=%d=0x%x", mask, mask, err, err);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_inlining_MainActivity_00024CalcTask_checkAffinity(JNIEnv *env, jobject thiz) {
    unsigned long mask = 0;
    int err, syscallres;
    pid_t pid = gettid();
    syscallres = syscall(__NR_sched_getaffinity, pid, sizeof(mask), &mask);
    if (syscallres) {
        err = errno;
        __android_log_print(ANDROID_LOG_ERROR, "INLINING_JNI", "Error in the syscall getaffinity: mask=%d=0x%x err=%d=0x%x", mask, mask, err, err);
//        LOGE("Error in the syscall setaffinity: mask=%d=0x%x err=%d=0x%x", mask, mask, err, err);
    } else {
        __android_log_print(ANDROID_LOG_DEBUG, "INLINING_JNI", "%d=0x%x", mask, mask);
    }
}


extern "C"
JNIEXPORT void JNICALL
Java_com_example_inlining_MainActivity_00024CalcTask_setAffinity(JNIEnv *env, jobject thiz) {
    unsigned int mask = 1;
    int err, syscallres;
    pid_t pid = gettid();
    syscallres = syscall(__NR_sched_setaffinity, pid, sizeof(mask), &mask);
    if (syscallres) {
        err = errno;
        __android_log_print(ANDROID_LOG_ERROR, "INLINING_JNI", "Error in the syscall setaffinity: mask=%d=0x%x err=%d=0x%x", mask, mask, err, err);
//        LOGE("Error in the syscall setaffinity: mask=%d=0x%x err=%d=0x%x", mask, mask, err, err);
    } else {
        __android_log_print(ANDROID_LOG_DEBUG, "INLINING_JNI", "set mask %d=0x%x", mask, mask);
    }
}