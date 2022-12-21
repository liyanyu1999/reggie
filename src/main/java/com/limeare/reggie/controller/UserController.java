package com.limeare.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.limeare.reggie.common.R;
import com.limeare.reggie.entity.User;
import com.limeare.reggie.service.UserService;
import com.limeare.reggie.utils.SMSUtils;
import com.limeare.reggie.utils.UserNameUtils;
import com.limeare.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取用户手机号
        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)){
            //生产验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            //发送短信
//            SMSUtils.sendMessage("外卖","",phone,code);
            //保存验证码 留待后续验证
//            session.setAttribute(phone,code);
            //存入redis  有效期5分钟
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);

            log.info("验证码："+code);

            return R.success(code);
        }

        return R.error("短信发送失败");

    }


    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){

        log.info(map.toString());
        //验证码比对
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

//        Object codeInSession = session.getAttribute(phone);

        //获取验证码
        Object codeInSession =redisTemplate.opsForValue().get(phone);

        if (codeInSession!=null && codeInSession.equals(code)){

            LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);

            //if this user is new one ,auto register
            if (user==null){
                user=new User();
                user.setName(UserNameUtils.getRandomUserName());
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            //login successful
            session.setAttribute("user",user.getId());

            redisTemplate.delete(phone);

            return R.success(user);

        }

        return R.error("登录失败");
    }

    @PostMapping("/loginout")
    public R<String> loginout(HttpServletRequest request){
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }

    @PostMapping("/getUserInfo")
    public R<User> getUserInfo(@RequestBody User user){
        String phone = user.getPhone();
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone,phone);
        User one = userService.getOne(queryWrapper);
        return R.success(one);
    }


}
