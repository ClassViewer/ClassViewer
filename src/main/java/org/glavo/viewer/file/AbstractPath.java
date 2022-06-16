package org.glavo.viewer.file;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
public sealed abstract class AbstractPath implements Comparable<AbstractPath> permits FilePath, RootPath {
    public abstract RootPath getRoot();
}
