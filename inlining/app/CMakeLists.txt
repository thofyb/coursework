cmake_minimum_required(VERSION 3.4.1)

add_library( affinity-lib
             SHARED
             src/main/cpp/setAffinityMethod.cpp )

find_library(
        log-lib
        log )

target_link_libraries(
        affinity-lib
        ${log-lib} )