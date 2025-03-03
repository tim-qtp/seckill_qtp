package com.seckill.goods.dao;

import com.seckill.goods.pojo.Sku;
import org.apache.ibatis.annotations.Insert;
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

    @Insert("<script> " +
            "INSERT INTO `tb_sku` " +
            "(`id`, `name`, `price`, `seckill_price`, `num`, `alert_num`, `image`, `images`," +
            " `create_time`, `update_time`, `spu_id`, `category1_id`, `category2_id`, `category3_id`, " +
            "`category1_name`, `category2_name`, `category3_name`, `brand_id`, `brand_name`, `sale_num`, `comment_num`, " +
            "`seckill_end`, `seckill_begin`, `status`, `islock`, `seckill_num`, `audit`, `count`, `isdel`) " +
            " VALUES " +
            "<foreach  collection='list' item='item' separator=','>" +
            "(#{item.id},#{item.name},#{item.price},#{item.seckillPrice},#{item.num},#{item.alertNum},#{item.image},#{item.images}" +
            ",#{item.createTime},#{item.updateTime},#{item.spuId},#{item.category1Id},#{item.category2Id},#{item.category3Id}" +
            ",#{item.category1Name},#{item.category2Name},#{item.category3Name},#{item.brandId},#{item.brandName},#{item.saleNum},#{item.commentNum}" +
            ",#{item.seckillEnd},#{item.seckillBegin},#{item.status},#{item.islock},#{item.seckillNum},#{item.audit},#{item.count},#{item.isdel})" +
            "</foreach>" +
            "</script>")
    void batch(List<Sku> list);

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
