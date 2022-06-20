package org.glavo.viewer.file;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
        @JsonSubTypes.Type(value = FilePath.class)
})
public sealed abstract class AbstractPath implements Comparable<AbstractPath> permits FilePath, RootPath {
    public abstract RootPath getRoot();
}
