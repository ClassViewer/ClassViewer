# ClassViewer

[![Gitter](https://badges.gitter.im/ClassViewer/ClassViewer.svg)](https://gitter.im/ClassViewer/ClassViewer?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge) [![Build Status](https://travis-ci.com/ClassViewer/ClassViewer.svg?branch=master)](https://travis-ci.com/ClassViewer/ClassViewer)

Website: [ClassViewer Homepage](https://viewer.glavo.org/)

ClassViewer v3 is a lightweight Java class file viewer only depends on JDK and JavaFX, it only has a single jar file less than 250k in size, and it can work on JRE 8.

OracleJDK no longer bundles JavaFX releases after OracleJDK 10, you can get the OpenJDK distribution bundled with OpenJFX 13 from [here](https://bell-sw.com/pages/java-13.0.1/).

ClassViewer v4 is under development, 
[icon](https://icons8.com/icons/set/picasa) by [Icons8](https://icons8.com).

## Features

* Understands class files described by [JVMS13](https://docs.oracle.com/javase/specs/jvms/se13/html/index.html)
* Displays parsed class file as tree and hex text
* The corresponding hex text is highlighted when you select a tree node

## Build

Building ClassViewer v3 requires Java 11 or later (but Java 8 is sufficient at runtime). 

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

* Donation via 爱发电: [Glavo - 爱发电](https://afdian.net/@Glavo)

* Donate via Alipay:

    ![Alipay](https://s2.ax1x.com/2020/02/04/1B9yFK.png)

* Donate via Wechat:

    ![Wechat](https://s2.ax1x.com/2020/02/04/1B9ro6.png)