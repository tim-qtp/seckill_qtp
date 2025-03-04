package com.seckill.user.controller;

import com.github.pagehelper.PageInfo;
import com.seckill.user.pojo.User;
import com.seckill.user.service.UserService;
import com.seckill.util.JwtTokenUtil;
import com.seckill.util.Result;
import com.seckill.util.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author http://www.itheima.com
 */
@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping(value = "/login")
    public Result login(@RequestBody User param) throws Exception {
        //1.根据用户名查询用户信息
        User user = userService.findById(param.getUsername());
        //2.用户不存在
        if (user == null) {
            return new Result(false, StatusCode.ERROR, "账号不存在！");
        }
        //3.密码错误
        if (!user.getPassword().equals(DigestUtils.md5DigestAsHex(param.getPassword().getBytes()))) {
            return new Result(false, StatusCode.ERROR, "密码错误！");
        }
        //4.登录成功
        Map<String, Object> payload = new HashMap<>();
        payload.put("username", user.getUsername());
        payload.put("name", user.getName());
        payload.put("phone", user.getPhone());
        String token = JwtTokenUtil.generateTokenUser(UUID.randomUUID().toString(), payload, 86400000L);

        return new Result(true, StatusCode.OK, "登录成功！", token);
    }

    /**
     * user分页条件搜索实现
     */
    @RequestMapping(value = "/search/{page}/{size}")
    public Result<PageInfo> findPage(@RequestBody(required = false) User user, @PathVariable int page, @PathVariable int size) throws InterruptedException {
        //调用ParaService实现分页条件查询Para
        PageInfo<User> pageInfo = userService.findPage(user, page, size);
        return new Result<>(true, StatusCode.OK, "查询成功", pageInfo);
    }


    public static void main(String[] args) {
        System.out.println(DigestUtils.md5DigestAsHex("itcast".getBytes()));
    }

}
