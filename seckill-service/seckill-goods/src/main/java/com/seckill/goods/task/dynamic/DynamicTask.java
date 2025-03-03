package com.seckill.goods.task.dynamic;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.seckill.goods.dao.SkuActMapper;
import com.seckill.goods.dao.SkuMapper;
import com.seckill.goods.pojo.Sku;
import com.seckill.goods.pojo.SkuAct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DynamicTask implements SimpleJob {

    private SkuActMapper skuActMapper = GetSpringBean.get(SkuActMapper.class);

    private SkuMapper skuMapper = GetSpringBean.get(SkuMapper.class);
    ;

    /**
     * 实现对应的业务
     */
    @Override
    public void execute(ShardingContext shardingContext) {
        // String id = shardingContext.getJobParameter();
        // SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        // String jobName = shardingContext.getJobName();
        // System.out.println(jobName + "时间：" + simpleDateFormat.format(new Date()) + ":::" + id);

        //活动ID(也就是那个额外的参数)
        String id = shardingContext.getJobParameter();
        System.out.println("动态添加定时任务:" + id);

        try {
            modify(id);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 1.根据活动ID查询活动ID下拥有的秒杀商品集合
     * 2.修改参与活动的秒杀商品状态，将状态改成非秒杀商品->
     * MySQL->binlog->Canal->获取增量数据->Canal微服务订阅增量数据->调用【静态页微服务、搜索微服务】
     */
    public void modify(String id) {
        //1.根据活动ID查询活动ID下拥有的秒杀商品集合
        SkuAct skuAct = new SkuAct();
        skuAct.setActivityId(id);
        List<SkuAct> skuActs = skuActMapper.select(skuAct);

        //获取活动对应的所有秒杀商品的ID
        List<String> ids = new ArrayList<>();
        for (SkuAct act : skuActs) {
            ids.add(act.getSkuId());
        }

        //2.修改参与活动的秒杀商品状态
        Sku sku = new Sku();
        //活动到期，秒杀商品的状态修改1，为普通商品
        sku.setStatus("1");

        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        //必须是秒杀商品才有 必要进行修改
        criteria.andEqualTo("status", "2");
        //修改条件是  活动所关联的秒杀商品id
        criteria.andIn("id", ids);

        skuMapper.updateByExampleSelective(sku, example);
    }
}