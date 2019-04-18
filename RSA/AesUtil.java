package com.secmask.util.tool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author wd
 * @Program DBMaskerServer
 * @create 2019-01-14 20:20
 */
public class AesUtil {

    private static  String passwordkey = "password";

    /**
     * 获取秘钥
     */
    private static String getKey() throws Exception{
        InputStream input = new FileInputStream(PathUtil.getProjectPath()+ "/password.json");
        byte[] bytes = new byte[input.available()];
        input.read(bytes);
        JSONObject jsonObject = JSON.parseObject(new String(bytes));
        return SecurityUtil.parseMD5(jsonObject.getString(passwordkey),"16");
    }

    /**
     * 加密
     */
    public static String aesEncode( String content )  {
        try {
            SecretKey key=new SecretKeySpec(getKey().getBytes(), "AES");
            Cipher cipher=Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte [] byteEncode =content.getBytes("utf-8");
            byte [] byteAes =cipher.doFinal(byteEncode );
            return new BASE64Encoder().encode(byteAes).replaceAll("[\\s*\t\n\r]", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //如果有错就返加nulll
        return null;
    }
    /**
     * 解密
     */
    public static String aesDecode( String content ){
        try {
            SecretKey key=new SecretKeySpec(getKey().getBytes(), "AES");
            Cipher cipher=Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte [] byteContent= new BASE64Decoder().decodeBuffer(content);
            byte [] byteDecode=cipher.doFinal(byteContent);
            return new String(byteDecode,"utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //如果有错就返加nulll
        return null;
    }

    public static void main(String[] args) {
        //String s = "U2FsdGVkX1/O/z5xS9E9XrVzMvVpfGFtzLDQpG7SGmXLlN2kGJf5N5i1mtUu9eTptHBaMJfvLFi9o3jAcnoFLw==";
        //System.out.println(aesDecode(s));
        String s = aesEncode("123");
        System.out.println(s);
        System.out.println(aesDecode(s));
    }

}
