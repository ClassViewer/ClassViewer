@echo off
cd %~dp0
IF EXIST build (
    del /f /s /q build
)
mkdir build
cd build
cmake -D CMAKE_BUILD_TYPE=Release -G "NMake Makefiles" ..
cmake --build . --target ClassViewer ..

