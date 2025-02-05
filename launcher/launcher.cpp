//
// Created by Glavo on 2020.1.8.
//

#include "launcher.h"
#include <windows.h>
#include <shlwapi.h>
#include <pathcch.h>
#include <string>
#include <vector>

#define MAX_LONG_PATH_SIZE 32768

#ifdef APPLICATION_SHOW_CONSOLE
int main() {
    LPWSTR pCmdLine = PathGetArgsW(GetCommandLineW());
#else

int WINAPI wWinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, PWSTR pCmdLine, int nCmdShow) {
#endif
    auto appName = static_cast<LPWSTR>(std::calloc(sizeof(WCHAR), MAX_LONG_PATH_SIZE));

    if (!GetModuleFileNameW(nullptr, appName, MAX_LONG_PATH_SIZE)) {
        MessageBoxW(nullptr, L"Failed to get module file name.", nullptr, MB_OK);
        return EXIT_FAILURE;
    }

    PathCchRemoveFileSpec(appName, MAX_LONG_PATH_SIZE);
    PathCchAppendEx(appName, MAX_LONG_PATH_SIZE, APPLICATION_JRE_PATH "\\bin\\" JAVA_BIN,
                    PATHCCH_ALLOW_LONG_PATHS);

    std::wstring cmdLine;
    cmdLine += L'"';
    cmdLine += appName;
    cmdLine += L'"';

    free(appName);

    cmdLine += L" -m " APPLICATION_MAIN_MODULE;

    if (wcslen(pCmdLine) > 0) {
        cmdLine += L' ';
        cmdLine += pCmdLine;
    }

    STARTUPINFOW si;
    PROCESS_INFORMATION pi;
    si.cb = sizeof(si);
    ZeroMemory(&si, sizeof(si));
    ZeroMemory(&pi, sizeof(pi));

    return CreateProcessW(nullptr,
                          &cmdLine[0],
                          nullptr,
                          nullptr,
                          false,
                          NORMAL_PRIORITY_CLASS,
                          nullptr,
                          nullptr,
                          &si,
                          &pi);
}


