#include <sys/syscall.h>
#include <pthread.h>
#include <sys/types.h>
#include <unistd.h>
#include <cerrno>
#include <android/log.h>

//#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "my module name", __VA_ARGS__)


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