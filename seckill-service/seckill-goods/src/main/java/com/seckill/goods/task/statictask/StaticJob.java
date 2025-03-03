package com.seckill.goods.task.statictask;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.elasticjob.lite.annotation.ElasticSimpleJob;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

//@Component
@ElasticSimpleJob(
        cron = "1/5 * * * * ?",   //任务执行周期,任务会从第 1 秒开始，每隔 5 秒执行一次。例如：00:00:01, 00:00:06, 00:00:11, ...
        jobName = "updatetask",   //和定时任务命名空间保持一致
        shardingTotalCount = 1    //分片
)
public class StaticJob implements SimpleJob {

    /**
     * 业务处理方法
     */
    @Override
    public void execute(ShardingContext shardingContext) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        System.out.println("时间：" + simpleDateFormat.format(new Date()));
    }
}