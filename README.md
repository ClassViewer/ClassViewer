# ClassViewer v3

ClassViewer is a lightweight Java class file viewer that you can use to view the structure of Java class files.

## Features

* Understands class files described by [JVMS23](https://docs.oracle.com/javase/specs/jvms/se23/html/index.html)
* Displays class file as tree and hex text.
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
