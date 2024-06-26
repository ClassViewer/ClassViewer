# ClassViewer

[![Gitter](https://badges.gitter.im/ClassViewer/ClassViewer.svg)](https://gitter.im/ClassViewer/ClassViewer?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge) [![Build Status](https://travis-ci.com/ClassViewer/ClassViewer.svg?branch=master)](https://travis-ci.com/ClassViewer/ClassViewer)

Website: [ClassViewer Homepage](https://viewer.glavo.org/)

ClassViewer v3 is a lightweight Java class file viewer only depends on JDK and JavaFX, it only has a single jar file less than 250k in size, and it can work on JRE 8.

OracleJDK no longer bundles JavaFX releases after OracleJDK 10, you can get the OpenJDK distribution bundled with OpenJFX 22 from [here](https://bell-sw.com/pages/downloads/?version=java-22&package=jdk-full).

ClassViewer v4 is under development, 
[icon](https://icons8.com/icons/set/picasa) by [Icons8](https://icons8.com).

## Features

* Understands class files described by [JVMS16](https://docs.oracle.com/javase/specs/jvms/se16/html/index.html)
* Displays parsed class file as tree and hex text
* The corresponding hex text is highlighted when you select a tree node

## Build

Building ClassViewer v4 requires Java 22 (but Java 21 is sufficient at runtime). 

```shell
./gradlew jar
```

## Run
```shell
./gradlew run
```

## Screenshots

![Screenshot](https://s2.ax1x.com/2020/02/04/1BC5jJ.png)
![Screenshot](https://s2.ax1x.com/2020/02/04/1BCTBR.png)
![Screenshot](https://s2.ax1x.com/2020/02/04/1BCou9.png)
![Screenshot](https://s2.ax1x.com/2020/02/04/1BCh3F.png)

## Support ClassViewer

ClassViewer is free and open source software, if you want to support its developers, you can make a donation:

* Donate via 爱发电: [Glavo - 爱发电](https://afdian.net/@Glavo)

## Thanks

Thanks to [PLCT Lab](https://plctlab.github.io/) for supporting me.

<img src="https://resources.jetbrains.com/storage/products/company/brand/logos/IntelliJ_IDEA.svg" alt="IntelliJ IDEA logo.">


This project is developed using JetBrains IDEA.
Thanks to JetBrains for providing me with a free license, which is a strong support for me.
