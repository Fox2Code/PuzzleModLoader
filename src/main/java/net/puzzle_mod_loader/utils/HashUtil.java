package net.puzzle_mod_loader.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {
    private final static char[] HEXARRAY = "0123456789abcdef".toCharArray();
    private static final MessageDigest MD5;
    private static final MessageDigest SHA1;

    static {
        try {
            MD5 = MessageDigest.getInstance("MD5");
            SHA1 = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new Error(e);
        }
    }

    public static String encodeHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEXARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEXARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] _md5(byte[] var0) {
        synchronized (MD5) {
            MD5.reset();
            MD5.update(var0);
            return MD5.digest();
        }
    }

    public static byte[] _sha1(byte[] var0) {
        synchronized (SHA1) {
            SHA1.reset();
            SHA1.update(var0);
            return SHA1.digest();
        }
    }

    public static String md5(byte[] var0) {
        return encodeHexString(_md5(var0));
    }

    public static String sha1(byte[] var0) {
        return encodeHexString(_sha1(var0));
    }

    public static String md5sha1(byte[] var0) {
        return md5(var0)+sha1(var0);
    }
}
