package org.glavo.viewer.file.types.classfile.attribute;

public final class AttributeFactory {

    private AttributeFactory() {
    }

    /**
     * Create concrete XxxAttribute by name.
     *
     * @param name type of attribute
     * @return new AttributeInfo
     */
    public static AttributeInfo create(String name) {
        //  predefined class file attributes:
        switch (name) {
            case "ConstantValue":
                return new ConstantValueAttribute();
            case "Code":
                return new CodeAttribute();
            case "StackMapTable":
                return new StackMapTableAttribute(); // todo
            case "Exceptions":
                return new ExceptionsAttribute();
            case "InnerClasses":
                return new InnerClassesAttribute();
            case "EnclosingMethod":
                return new EnclosingMethodAttribute();
            case "Synthetic":
                return new SyntheticAttribute();
            case "Signature":
                return new SignatureAttribute();
            case "SourceFile":
                return new SourceFileAttribute();
            case "SourceDebugExtension":
                return new SourceDebugExtensionAttribute(); // todo
            case "LineNumberTable":
                return new LineNumberTableAttribute();
            case "LocalVariableTable":
                return new LocalVariableTableAttribute();
            case "LocalVariableTypeTable":
                return new LocalVariableTypeTableAttribute();
            case "Deprecated":
                return new DeprecatedAttribute();
            case "RuntimeVisibleAnnotations":
            case "RuntimeInvisibleAnnotations":
                return new RuntimeVisibleAnnotationsAttribute();
            case "RuntimeVisibleParameterAnnotations":
            case "RuntimeInvisibleParameterAnnotations":
                return new RuntimeVisibleParameterAnnotationsAttribute();
            case "RuntimeVisibleTypeAnnotations":
            case "RuntimeInvisibleTypeAnnotations":
                return new RuntimeVisibleTypeAnnotationsAttribute();
            case "AnnotationDefault":
                return new AnnotationDefaultAttribute();
            case "BootstrapMethods":
                return new BootstrapMethodsAttribute();
            case "MethodParameters":
                return new MethodParametersAttribute(); // todo
            case "Module":
                return new ModuleAttribute();
            case "ModulePackages":
                return new ModulePackagesAttribute();
            case "ModuleMainClass":
                return new ModuleMainClassAttribute();
            case "NestHost":
                return new NestHostAttribute();
            case "NestMembers":
                return new NestMembersAttribute();
            case "Record":
                return new RecordAttribute();
            case "PermittedSubclasses":
                return new PermittedSubclassesAttribute();
        }

        //throw new ParseException(name);
        return new UndefinedAttribute();
    }

}
