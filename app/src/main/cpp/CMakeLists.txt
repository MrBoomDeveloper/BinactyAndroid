
# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html.
# For more examples on how to use CMake, see https://github.com/android/ndk-samples.

# Sets the minimum CMake version required for this project.
cmake_minimum_required(VERSION 3.22.1)

# Declares the project name. The project name can be accessed via ${ PROJECT_NAME},
# Since this is the top level CMakeLists.txt, the project name is also accessible
# with ${CMAKE_PROJECT_NAME} (both CMake variables are in-sync within the top level
# build script scope).
project("platformer")

add_library(${CMAKE_PROJECT_NAME} SHARED platformer.cpp)
add_library(EOSSDK SHARED IMPORTED)

set(EOSSDKDIR ${CMAKE_SOURCE_DIR}/../../../../epic)
set_property(TARGET EOSSDK PROPERTY IMPORTED_LOCATION ${EOSSDKDIR}/Bin/Android/static-stdc++/libs/${ANDROID_ABI}/libEOSSDK.so)
set_property(TARGET EOSSDK PROPERTY INTERFACE_INCLUDE_DIRECTORIES ${EOSSDKDIR}/Include/)

target_link_libraries(${CMAKE_PROJECT_NAME}
    EOSSDK
    android
    log)