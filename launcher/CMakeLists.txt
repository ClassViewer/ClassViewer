cmake_minimum_required(VERSION 3.10)

project(launcher
        VERSION 3
        LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 11)
set(CMAKE_SYSTEM_VERSION 8.1)

add_definitions(/D_WIN32_WINNT=0x0A00)

add_executable(ClassViewer launcher.rc launcher.h launcher.cpp)
target_link_libraries(ClassViewer pathcch)

set_property(TARGET ClassViewer PROPERTY
        MSVC_RUNTIME_LIBRARY "MultiThreaded")

if (NOT DEFINED APP_SHOW_CONSOLE)
    set_property(TARGET ClassViewer PROPERTY WIN32_EXECUTABLE true)
else ()
    add_compile_definitions("APPLICATION_SHOW_CONSOLE")
    target_link_libraries(ClassViewer Shlwapi)
endif ()
