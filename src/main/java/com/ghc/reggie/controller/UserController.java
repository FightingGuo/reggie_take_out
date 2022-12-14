package com.ghc.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ghc.reggie.bean.User;
import com.ghc.reggie.service.UserService;
import com.ghc.reggie.tencentSMSUtils.RamdomUtils;
import com.ghc.reggie.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author 郭昊晨
 * @version 1.0
 * 2022/7/31 - 19:05
 */
@RestController
@ResponseBody
@RequestMapping("/user")
@Slf4j
//加了注释
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Resource
    private CacheManager cacheManager;


    /**
     * 发送短信
     * @param user
     * @param Session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> send(@RequestBody User user, HttpSession Session) {
        //获取手机号
        String phone=user.getPhone();

        //生成随机的六位验证码
        if (StringUtils.isNotEmpty(phone)){
            String code = RamdomUtils.getSixBitRandom().toString();
            log.info("验证码 code{}",code);

            //调用腾讯云的API实现发送短信
            boolean result = userService.send(phone,code);

            if (result){
                //将生成的验证码保存导Session中
//                Session.setAttribute(phone,code);

                //优化
                //将生成的验证码存入redis中，并设置过期时间1min
                redisTemplate.opsForValue().set(phone,code,1, TimeUnit.MINUTES);

                return R.success("短信发送成功");
            }
            }
        return R.error("短信发送失败");
    }

    /**
     * 登录
     * @param map
     * @param Session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession Session) {

        //获取手机号
        String phone = map.get("phone").toString();

        //获取验证码
        String code = map.get("code").toString();

        //从session中获取后端存的验证码
//        String smScode = Session.getAttribute(phone).toString();

        //从redis中取出验证码
        String smsCode = (String) redisTemplate.opsForValue().get(phone);

        //进行验证
        if (smsCode!=null && smsCode.equals(code)){
            //判断是否为新用户,如果是新用户就自动完成注册
            LambdaQueryWrapper<User> userLqw=new LambdaQueryWrapper<>();
            userLqw.eq(User::getPhone,phone);
            User user = userService.getOne(userLqw);

            if (user==null){
                user=new User();

                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            //把登录成功的user放入session
            Session.setAttribute("user",user.getId());
            //登录成功把验证码清除
            redisTemplate.delete(phone);
            return R.success(user);
        }


//        log.info("map {}",map);
        return R.error("登录失败");
    }

    @PostMapping("/loginout")
    public R<String> logout(HttpSession session){
        session.removeAttribute("user");

        return R.success("用户退出");
    }
}
