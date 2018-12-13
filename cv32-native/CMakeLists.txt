cmake_minimum_required(VERSION 3.4.1)
set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_SOURCE_DIR}/build/outputs/jniLibs/${ANDROID_ABI})

set( CPP_DIR src/main/cpp)

#查找系统库
find_library( log-lib log )

set(opencv_include_dir src/main/cpp/include)
include(AndroidNdkGdb)
include(AndroidNdkModules)
include_directories( ${opencv_include_dir} )
add_library( opencv_java3
             SHARED
             IMPORTED )
set_target_properties( opencv_java3
                       PROPERTIES IMPORTED_LOCATION
                       libs/${ANDROID_ABI}/libopencv_java3.so )
set( SRC_LIST )
aux_source_directory(${CPP_DIR} CPP_SRC_LIST)
aux_source_directory(${CPP_DIR}/cutout CUT_SRC_LIST)
list(APPEND SRC_LIST
     ${CPP_SRC_LIST}
     ${CUT_SRC_LIST}
)
add_library( opencv32
             SHARED
             ${SRC_LIST})

target_link_libraries( opencv32
                       opencv_java3
                       ${log-lib})
