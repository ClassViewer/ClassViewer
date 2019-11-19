#include <windows.h>
#include <pathcch.h>
#include <strsafe.h>

#define MAX_LONG_PATH_SIZE 32768

static WCHAR par[] = L"-m org.glavo.viewer/org.glavo.viewer.gui.Viewer ";

int WINAPI wWinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, PWSTR pCmdLine, int nCmdShow) {
    WCHAR *buffer = calloc(MAX_LONG_PATH_SIZE, sizeof(WCHAR));
    if (GetModuleFileNameW(NULL, buffer, MAX_LONG_PATH_SIZE)) {
        // javaw.exe
        PathCchRemoveFileSpec(buffer, MAX_LONG_PATH_SIZE);
        PathCchAppendEx(buffer, MAX_LONG_PATH_SIZE, L"javaw.exe", PATHCCH_ALLOW_LONG_PATHS);

        // Parameters
        size_t pl = wcslen(par) + wcslen(pCmdLine) + 1;
        LPWSTR pbuffer = calloc(pl, sizeof(WCHAR));
        StringCchCatW(pbuffer, pl, par);
        StringCchCatW(pbuffer, pl, pCmdLine);

        ShellExecuteW(NULL, L"open", buffer, pbuffer, NULL, SW_SHOWDEFAULT);
        free(buffer);
        free(pbuffer);
        return 0;
    }
    free(buffer);
    return EXIT_FAILURE;
}
