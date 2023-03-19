cmake_minimum_required(VERSION 3.18.1)
project("pcapplusplus_interface")
set(CMAKE_CXX_STANDARD 17)
add_library(
        pcapplusplus_interface
        SHARED
        pcapplusplus_interface.cpp)
find_library(log-lib log)
target_include_directories(pcapplusplus_interface PUBLIC ${CMAKE_SOURCE_DIR}/../../../libs/pcapplusplus/include)
target_link_libraries(
        pcapplusplus_interface
        ${CMAKE_SOURCE_DIR}/../../../libs/pcapplusplus/${ANDROID_ABI}/30/libPcap++.a
        ${CMAKE_SOURCE_DIR}/../../../libs/pcapplusplus/${ANDROID_ABI}/30/libPacket++.a
        ${CMAKE_SOURCE_DIR}/../../../libs/pcapplusplus/${ANDROID_ABI}/30/libCommon++.a
        ${log-lib})
