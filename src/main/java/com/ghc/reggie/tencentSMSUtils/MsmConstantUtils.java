package com.ghc.reggie.tencentSMSUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author 郭昊晨
 * @version 1.0
 * 2022/7/31 - 22:49
 */
//实现了InitializingBean接口，当spring进行初始化bean时，会执行afterPropertiesSet方法
@Component
@PropertySource("classpath:application.yml")
@ConfigurationProperties(prefix = "tencent.msm")
public class MsmConstantUtils implements InitializingBean {

    @Value("${id}")
    private String secretID ;

    @Value("${secret}")
    private String secretKey ;

    @Value("${endPoint}")
    private String endPoint;

    @Value("${appId}")
    private String appId;

//    @Value("${signName}")
//    private String signName;

    @Value("${templateId}")
    private String templateId;
    //六个相关的参数
    public static String SECRET_ID;
    public static String SECRET_KEY;
    public static String END_POINT;
    public static String APP_ID;
//    public static String SIGN_NAME;
    public static String TEMPLATE_ID;

    @Override
    public void afterPropertiesSet() throws Exception {
        SECRET_ID = secretID;
        SECRET_KEY = secretKey;
        END_POINT = endPoint;
        APP_ID = appId;
//        SIGN_NAME = signName;
        TEMPLATE_ID = templateId;
    }
}

