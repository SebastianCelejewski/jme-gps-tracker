package com.jcraft.jzlib;

public class WrapperType {
    
    public static final WrapperType NONE = new WrapperType(1);
    public static final WrapperType ZLIB = new WrapperType(2);
    public static final WrapperType GZIP = new WrapperType(3);
    public static final WrapperType ANY = new WrapperType(4);

    public static final WrapperType W_NONE = WrapperType.NONE;
    public static final WrapperType W_ZLIB = WrapperType.ZLIB;
    public static final WrapperType W_GZIP = WrapperType.GZIP;
    public static final WrapperType W_ANY = WrapperType.ANY;
    
    private WrapperType(int value) {
        this.value = value;
    }
    
    private int value;
}