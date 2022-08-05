package com.ghc.reggie.tencentSMSUtils;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * @author 郭昊晨
 * @version 1.0
 * 2022/7/31 - 22:51
 */
public class RamdomUtils {

    private static final Random random = new Random();
    //我定义的验证码位数是6位
    private static final DecimalFormat sixdf = new DecimalFormat("000000");

    public static String getSixBitRandom() {
        return sixdf.format(random.nextInt(1000000));
    }
}


