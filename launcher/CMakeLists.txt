cmake_minimum_required(VERSION 3.10)

project(launcher
        VERSION 3
        LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 11)

find_package(JNI REQUIRED)
include_directories(${JNI_INCLUDE_DIRS})
include_directories(${CMAKE_CURRENT_BINARY_DIR})

add_executable(ClassViewer launcher.rc launcher.h launcher.cpp)
target_link_libraries(ClassViewer pathcch)

if (NOT DEFINED APP_SHOW_CONSOLE)
    set_property(TARGET ClassViewer PROPERTY WIN32_EXECUTABLE true)
else ()
    add_compile_definitions("APPLICATION_SHOW_CONSOLE")
    target_link_libraries(ClassViewer Shlwapi)
endif ()
