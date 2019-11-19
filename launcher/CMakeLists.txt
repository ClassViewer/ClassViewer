cmake_minimum_required(VERSION 3.15)
project(launcher
        VERSION 1.0.0
        LANGUAGES C
        )

set(CMAKE_C_STANDARD 99)

if (WIN32 AND MSVC)
    configure_file(
            launcher.rc.in
            ${CMAKE_CURRENT_BINARY_DIR}/launcher.rc
            @ONLY
    )

    add_executable(ClassViewer launcher_win32.c ${CMAKE_CURRENT_BINARY_DIR}/launcher.rc)
    target_link_libraries(ClassViewer pathcch)
    set_property(TARGET ClassViewer PROPERTY WIN32_EXECUTABLE true)
endif ()