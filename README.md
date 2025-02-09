# ClassViewer

ClassViewer is a lightweight Java class file viewer that you can use to view the structure of Java class files.

## Features

* Understands class files described by [JVMS23](https://docs.oracle.com/javase/specs/jvms/se23/html/index.html)
* Displays class file as tree and hex text.
* The corresponding hex text is highlighted when you select a tree node

## Screenshots

![Screenshot](https://s2.ax1x.com/2020/02/04/1BC5jJ.png)
![Screenshot](https://s2.ax1x.com/2020/02/04/1BCTBR.png)
![Screenshot](https://s2.ax1x.com/2020/02/04/1BCou9.png)
![Screenshot](https://s2.ax1x.com/2020/02/04/1BCh3F.png)

## Build

Building ClassViewer v3 requires JDK 11 (or later) with JavaFX bundled. 

```shell
./gradlew jar
```

## Run
```shell
./gradlew run
```
