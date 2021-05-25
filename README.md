# ClassViewer v3

[![Gitter](https://badges.gitter.im/ClassViewer/ClassViewer.svg)](https://gitter.im/ClassViewer/ClassViewer?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge) [![Build Status](https://travis-ci.com/ClassViewer/ClassViewer.svg?branch=master)](https://travis-ci.com/ClassViewer/ClassViewer)

Website: [ClassViewer Homepage](https://viewer.glavo.org/)

ClassViewer v3 is a lightweight Java class file viewer only depends on JDK and JavaFX, it only has a single jar file less than 250k in size, and it can work on JRE 8.

OracleJDK no longer bundles JavaFX releases after OracleJDK 10, you can get the OpenJDK distribution bundled with OpenJFX 13 from [here](https://bell-sw.com/pages/java-13.0.1/).

ClassViewer v4 is under development, so stay tuned.

## Features

* Understands class files described by [JVMS13](https://docs.oracle.com/javase/specs/jvms/se13/html/index.html)
* Displays parsed class file as tree and hex text
* The corresponding hex text is highlighted when you select a tree node

## Build

### Run(Java 11+)

```shell
./gradlew run
```

### Jlink(Java 11+)

```shell
./gradlew jlink
```

### JPakcage(Java 15+)

```shell
./gradlew jpackage
```

## Screenshots

![Screenshot](https://glavo.oss-cn-beijing.aliyuncs.com/image/Annotation%202020-01-12%20183836.png)
![Screenshot](https://glavo.oss-cn-beijing.aliyuncs.com/image/Annotation%202020-01-12%20184117.png)
![Screenshot](https://glavo.oss-cn-beijing.aliyuncs.com/image/Annotation%202020-01-12%20184233.png)
![Screenshot](https://glavo.oss-cn-beijing.aliyuncs.com/image/Annotation%202020-01-12%20184259.png)