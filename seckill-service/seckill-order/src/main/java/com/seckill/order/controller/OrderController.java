package com.seckill.order.controller;

import com.github.pagehelper.PageInfo;
import com.seckill.order.pojo.Order;
import com.seckill.order.pojo.OrderVo;
import com.seckill.order.service.OrderService;
import com.seckill.util.IdWorker;
import com.seckill.util.JwtTokenUtil;
import com.seckill.util.Result;
import com.seckill.util.StatusCode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

/**
 * @author http://www.itheima.com
 */
@RestController
@RequestMapping("/order")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private IdWorker idWorker;

    /**
     * Order分页条件搜索实现
     */
    @PostMapping(value = "/search/{page}/{size}")
    public Result<PageInfo> findPage(@RequestBody(required = false) OrderVo orderVo, @PathVariable int page, @PathVariable int size) {
        //调用OrderService实现分页条件查询Order
        Order order = new Order();
        BeanUtils.copyProperties(orderVo, order);
        PageInfo<Order> pageInfo = orderService.findPage(order, page, size);
        return new Result<>(true, StatusCode.OK, "查询成功", pageInfo);
    }


    /**
     * 用户订单
     */
    @GetMapping(value = "/user/{page}/{size}")
    public Result<PageInfo> userOrders(@PathVariable int page,
                                       @PathVariable int size,
                                       @RequestParam(value = "type", defaultValue = "0") Integer type,
                                       @RequestHeader("Authorization") String authorization) {
        Map<String, Object> userMap = JwtTokenUtil.parseToken(authorization);
        //调用OrderService实现分页条件查询Order
        Order order = new Order();
        order.setUsername(userMap.get("username").toString());
        switch (type) {
            case 1:
                order.setPayStatus("0");
                break;
            case 3:
                order.setPayStatus("1");
                break;
        }
        PageInfo<Order> pageInfo = orderService.findPage(order, page, size);
        return new Result<>(true, StatusCode.OK, "查询成功", pageInfo);
    }


    /**
     * 订单详情
     */
    @GetMapping(value = "/info/{id}")
    public Result<Order> one(@PathVariable(value = "id") String id) {
        Order order = orderService.findById(id);
        return new Result<>(true, StatusCode.OK, "查询成功", order);
    }

    /**
     * 订单详情
     */
    @GetMapping(value = "/pay/{id}")
    public Result pay(@PathVariable(value = "id") String id) {
        orderService.pay(id);
        return new Result(true, StatusCode.OK, "支付完成");
    }
}
