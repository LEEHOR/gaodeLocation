package com.weilun.uniplugin_beLocation.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Map;
import java.util.TreeMap;

public class EncryptUtil {

    /**
     * 排序param字段，组装成k1=v1&k2=v2...的格式
     *
     * @param param
     * @param charset
     * @param skipblank
     */
    public static String getSignStr(Map<String, String> param, String charset, boolean skipblank) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        TreeMap<String, String> p;
        if (param instanceof TreeMap) {
            p = (TreeMap<String, String>) param;
        } else {
            p = new TreeMap<String, String>();
            p.putAll(param);
        }
        for (Map.Entry<String, String> entry : p.entrySet()) {
            if (skipblank) {
                if (isEmpty(entry.getValue())) {
                    continue;
                }
            }
            sb.append(entry.getKey()).append("=").append(charset == null ? entry.getValue() : URLEncoder.encode(entry.getValue(), charset))
                    .append("&");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * 判断字符串是否为空
     *
     * @param s
     * @return
     */
    private static boolean isEmpty(String s) {
        if (s == null || "".equals(s.trim())) {
            return true;
        }
        return false;
    }

    public static String md5(byte[] b) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(b);
            byte[] hash = md.digest();
            StringBuffer outStrBuf = new StringBuffer(32);
            for (int i = 0; i < hash.length; i++) {
                int v = hash[i] & 0xFF;
                if (v < 16) {
                    outStrBuf.append('0');
                }
                outStrBuf.append(Integer.toString(v, 16).toLowerCase());
            }
            return outStrBuf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new String(b);
        }
    }

    /**
     * rsa加密签名
     *
     * @param content
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String rsa(String content, String privateKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey priKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(privateKey.getBytes())));

        java.security.Signature signature = java.security.Signature.getInstance("SHA1WithRSA");
        signature.initSign(priKey);
        signature.update(content.getBytes("UTF-8"));
        return new String(Base64.encode(signature.sign()));
    }

}
