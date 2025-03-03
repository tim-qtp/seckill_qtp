package com.seckill.goods.controller;

import com.seckill.goods.task.dynamic.DynamicTask;
import com.seckill.goods.task.dynamic.ElasticjobDynamicConfig;
import com.seckill.util.Result;
import com.seckill.util.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping(value = "/task")
public class TaskController {

    @Autowired
    private ElasticjobDynamicConfig elasticjobDynamicConfig;

    /**
     * 动态定时任务案例测试
     */
    @GetMapping
    public Result task(String jobName, Long time, String id) {
//        String cron = "0/" + time + " * * * * ?";
        //在当前时间往后延迟time毫秒执行
        String cron = ElasticjobDynamicConfig.date2cron(new Date(System.currentTimeMillis() + time));
        elasticjobDynamicConfig.addDynamicTask(jobName, cron, 1, new DynamicTask(), id);
        return new Result(true, StatusCode.OK, "执行成功！");
    }
}
