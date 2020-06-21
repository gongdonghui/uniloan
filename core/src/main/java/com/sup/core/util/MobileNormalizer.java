package com.sup.core.util;

/**
 * gongshuai
 * <p>
 * 2020/5/31
 */
public class MobileNormalizer {

    public static String normalize(String mobile) {
        String ret = mobile.trim();
        ret = ret.replaceAll("\\s*", "");
        ret =   ret.replaceAll("-*","");
        if (ret.startsWith("+84")) {
            ret = ret.replace("+84", "84");
        }
        if (ret.startsWith("0")) {
            ret = "84" + ret.substring(1, ret.length());
        }
        return  ret;
    }
    public  static void main(String[]  args) {
        String mobile ="+8416 386-58969";
        String m =  normalize(mobile);
        System.out.println(m);
    }
}
