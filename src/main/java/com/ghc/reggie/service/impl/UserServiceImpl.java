package com.ghc.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghc.reggie.bean.User;
import com.ghc.reggie.mapper.UserMapper;
import com.ghc.reggie.service.UserService;
import com.ghc.reggie.tencentSMSUtils.MsmConstantUtils;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * @author 郭昊晨
 * @version 1.0
 * 2022/7/31 - 10:08
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{
    @Override
    public boolean send(String phone,String code) {
        try {
            //这里是实例化一个Credential，也就是认证对象，参数是密钥对；你要使用肯定要进行认证
            Credential credential = new Credential(MsmConstantUtils.SECRET_ID, MsmConstantUtils.SECRET_KEY);

            //HttpProfile这是http的配置文件操作，比如设置请求类型(post,get)或者设置超时时间了、还有指定域名了
            //最简单的就是实例化该对象即可，它的构造方法已经帮我们设置了一些默认的值
            HttpProfile httpProfile = new HttpProfile();
            //这个setEndpoint可以省略的
            httpProfile.setEndpoint(MsmConstantUtils.END_POINT);

            //实例化一个客户端配置对象,这个配置可以进行签名（使用私钥进行加密的过程），对方可以利用公钥进行解密
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            //实例化要请求产品(以sms为例)的client对象
            SmsClient smsClient = new SmsClient(credential, "ap-nanjing", clientProfile);

            //实例化request封装请求信息
            SendSmsRequest request = new SendSmsRequest();
            String[] phoneNumber = {phone};
            request.setPhoneNumberSet(phoneNumber);     //设置手机号
            request.setSmsSdkAppId(MsmConstantUtils.APP_ID);

            //因为这个参数是签名内容  即公众号名 为定值，而通过yml文件传过滤会乱码，所以索性直接传定值发现可以了
            request.setSignName("外卖编程公众号");
            request.setTemplateId(MsmConstantUtils.TEMPLATE_ID);


            //生成随机验证码，我的模板内容的参数只有一个
            String[] templateParamSet = {code};
            request.setTemplateParamSet(templateParamSet);

            //发送短信
            SendSmsResponse response = smsClient.SendSms(request);
//            log.info(SendSmsResponse.toJsonString(response));
            return true;
        } catch (Exception e) {
            return false;
        }


    }
}
