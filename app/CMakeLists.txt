cmake_minimum_required(VERSION 3.4.1)
set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

#查找系统库
find_library( log-lib log )

set(distribution_DIR ../../../../libs)
set(pathToProject /Users/silen/CodeLibs/0-lib/OpenCVDemo/XOpenCV)
set(pathToOpenCv /Users/silen/CodeLibs/0-lib/OpenCV-android-sdk)
include(AndroidNdkGdb)
include(AndroidNdkModules)
include_directories( ${pathToOpenCv}/sdk/native/jni/include )
add_library( opencv_java3
             SHARED
             IMPORTED )
set_target_properties( opencv_java3
                       PROPERTIES IMPORTED_LOCATION
                       ${distribution_DIR}/${ANDROID_ABI}/libopencv_java3.so )
set( CPP_DIR src/main/cpp)
set( SRC_LIST )
aux_source_directory(${CPP_DIR} CPP_SRC_LIST)
aux_source_directory(${CPP_DIR}/cutout CUT_SRC_LIST)
list(APPEND SRC_LIST
     ${CPP_SRC_LIST}
     ${CUT_SRC_LIST}
)
add_library( g2uopen_v1
             SHARED
             ${SRC_LIST})

target_link_libraries( g2uopen_v1
                       opencv_java3
                       ${log-lib})
