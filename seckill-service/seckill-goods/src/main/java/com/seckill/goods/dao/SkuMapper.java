package com.seckill.goods.dao;

import com.seckill.goods.pojo.Sku;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.web.bind.annotation.PathVariable;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author http://www.itheima.com
 */
public interface SkuMapper extends Mapper<Sku> {

    /**
     * 锁定
     */
    @Update("update sku set lock=2 where id=#{id} and lock=1")
    void lock(String id);

    /**
     * 解锁
     */
    @Update("update sku set lock=1 where id=#{id} and lock=2")
    void unlock(String id);

    /**
     * 根据活动ID查询商品
     */
    @Select("SELECT s.id,s.images,s.`name`,s.price,s.seckill_price seckillPrice,s.num,s.seckill_num seckillNum,s.alert_num alertNum,s.sale_num saleNum,s.image,s.count FROM tb_sku_act sa LEFT JOIN tb_sku s ON sa.sku_id=s.id WHERE sa.activity_id=#{id} ORDER BY sa.create_time DESC")
    List<Sku> findSkuByActivityId(String id);

    /**
     * 修改
     */
    @Update("update tb_sku set islock=1 ,status=1")
    void modifySku();
}
