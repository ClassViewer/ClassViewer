//
// Created by Glavo on 2019.12.24.
//

#ifndef LAUNCHER_LAUNCHER_H
#define LAUNCHER_LAUNCHER_H

#define APPLICATION_MAIN_MODULE L"org.glavo.viewer"
#define APPLICATION_JRE_PATH L".\\jre"

#ifdef APP_SHOW_CONSOLE
    #define JAVA_BIN L"java.exe"
#else
    #define JAVA_BIN L"javaw.exe"
#endif

#endif //LAUNCHER_LAUNCHER_H
