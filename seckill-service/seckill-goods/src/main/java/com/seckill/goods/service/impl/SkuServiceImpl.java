package com.seckill.goods.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.seckill.goods.dao.ActivityMapper;
import com.seckill.goods.dao.SkuActMapper;
import com.seckill.goods.dao.SkuMapper;
import com.seckill.goods.pojo.Activity;
import com.seckill.goods.pojo.Sku;
import com.seckill.goods.pojo.SkuAct;
import com.seckill.goods.service.SkuService;
import com.seckill.util.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author http://www.itheima.com
 */
@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private SkuActMapper skuActMapper;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private RedisTemplate redisTemplate;




    /**
     * Sku条件+分页查询
     *
     * @param sku  查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<Sku> findPage(Sku sku, int page, int size) {
        //分页
        PageHelper.startPage(page, size);
        //搜索条件构建
        Example example = createExampleSp(sku);
        //执行搜索
        return new PageInfo<Sku>(skuMapper.selectByExample(example));
    }

    /**
     * Sku分页查询
     */
    @Override
    public PageInfo<Sku> findPage(int page, int size) {
        //静态分页
        PageHelper.startPage(page, size);
        //分页查询
        return new PageInfo<Sku>(skuMapper.selectAll());
    }

    /**
     * Sku条件查询
     */
    @Override
    public List<Sku> findList(Sku sku) {
        //构建查询条件
        Example example = createExample(sku);
        //根据构建的条件查询数据
        return skuMapper.selectByExample(example);
    }


    /**
     * Sku构建查询对象
     */
    public Example createExample(Sku sku) {
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        if (sku != null) {
            // 商品id
            if (!StringUtils.isEmpty(sku.getId())) {
                criteria.andEqualTo("id", sku.getId());
            }
            // SKU名称
            if (!StringUtils.isEmpty(sku.getName())) {
                criteria.andLike("name", "%" + sku.getName() + "%");
            }
            // 价格（分）
            if (!StringUtils.isEmpty(sku.getPrice())) {
                criteria.andEqualTo("price", sku.getPrice());
            }
            // 单位，分
            if (!StringUtils.isEmpty(sku.getSeckillPrice())) {
                criteria.andEqualTo("seckillPrice", sku.getSeckillPrice());
            }
            // 库存数量
            if (!StringUtils.isEmpty(sku.getNum())) {
                criteria.andEqualTo("num", sku.getNum());
            }
            // 库存预警数量
            if (!StringUtils.isEmpty(sku.getAlertNum())) {
                criteria.andEqualTo("alertNum", sku.getAlertNum());
            }
            // 商品图片
            if (!StringUtils.isEmpty(sku.getImage())) {
                criteria.andEqualTo("image", sku.getImage());
            }
            // 商品图片列表
            if (!StringUtils.isEmpty(sku.getImages())) {
                criteria.andEqualTo("images", sku.getImages());
            }
            // 创建时间
            if (!StringUtils.isEmpty(sku.getCreateTime())) {
                criteria.andEqualTo("createTime", sku.getCreateTime());
            }
            // 更新时间
            if (!StringUtils.isEmpty(sku.getUpdateTime())) {
                criteria.andEqualTo("updateTime", sku.getUpdateTime());
            }
            // SPUID
            if (!StringUtils.isEmpty(sku.getSpuId())) {
                criteria.andEqualTo("spuId", sku.getSpuId());
            }
            // 类目ID
            if (!StringUtils.isEmpty(sku.getCategory1Id())) {
                criteria.andEqualTo("category1Id", sku.getCategory1Id());
            }
            // 
            if (!StringUtils.isEmpty(sku.getCategory2Id())) {
                criteria.andEqualTo("category2Id", sku.getCategory2Id());
            }
            // 
            if (!StringUtils.isEmpty(sku.getCategory3Id())) {
                criteria.andEqualTo("category3Id", sku.getCategory3Id());
            }
            // 
            if (!StringUtils.isEmpty(sku.getCategory1Name())) {
                criteria.andEqualTo("category1Name", sku.getCategory1Name());
            }
            // 
            if (!StringUtils.isEmpty(sku.getCategory2Name())) {
                criteria.andEqualTo("category2Name", sku.getCategory2Name());
            }
            // 类目名称
            if (!StringUtils.isEmpty(sku.getCategory3Name())) {
                criteria.andEqualTo("category3Name", sku.getCategory3Name());
            }
            // 
            if (!StringUtils.isEmpty(sku.getBrandId())) {
                criteria.andEqualTo("brandId", sku.getBrandId());
            }
            // 品牌名称
            if (!StringUtils.isEmpty(sku.getBrandName())) {
                criteria.andEqualTo("brandName", sku.getBrandName());
            }
            // 规格
            if (!StringUtils.isEmpty(sku.getSpec())) {
                criteria.andEqualTo("spec", sku.getSpec());
            }
            // 销量
            if (!StringUtils.isEmpty(sku.getSaleNum())) {
                criteria.andEqualTo("saleNum", sku.getSaleNum());
            }
            // 评论数
            if (!StringUtils.isEmpty(sku.getCommentNum())) {
                criteria.andEqualTo("commentNum", sku.getCommentNum());
            }
            // 商品状态 1-正常，2-下架，3-删除
            if (!StringUtils.isEmpty(sku.getStatus())) {
                criteria.andEqualTo("status", sku.getStatus());
            }

            // 秒杀开始时间
            if (!StringUtils.isEmpty(sku.getSeckillBegin())) {
                criteria.andGreaterThanOrEqualTo("seckillBegin", sku.getSeckillBegin());
            }
            // 秒杀结束时间
            if (!StringUtils.isEmpty(sku.getSeckillEnd())) {
                criteria.andLessThanOrEqualTo("seckillEnd", sku.getSeckillEnd());
            }
        }
        return example;
    }


    /**
     * Sku构建查询对象
     */
    public Example createExampleSp(Sku sku) {
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        if (sku != null) {
            // 商品id
            if (!StringUtils.isEmpty(sku.getId())) {
                criteria.andEqualTo("id", sku.getId());
            }
            // SKU名称
            if (!StringUtils.isEmpty(sku.getName())) {
                criteria.andLike("name", "%" + sku.getName() + "%");
            }
            // 价格（分）
            if (!StringUtils.isEmpty(sku.getPrice())) {
                criteria.andEqualTo("price", sku.getPrice());
            }
            // 单位，分
            if (!StringUtils.isEmpty(sku.getSeckillPrice())) {
                criteria.andEqualTo("seckillPrice", sku.getSeckillPrice());
            }
            // 库存数量
            if (!StringUtils.isEmpty(sku.getNum())) {
                criteria.andEqualTo("num", sku.getNum());
            }
            // 库存预警数量
            if (!StringUtils.isEmpty(sku.getAlertNum())) {
                criteria.andEqualTo("alertNum", sku.getAlertNum());
            }
            // 商品图片
            if (!StringUtils.isEmpty(sku.getImage())) {
                criteria.andEqualTo("image", sku.getImage());
            }
            // 商品图片列表
            if (!StringUtils.isEmpty(sku.getImages())) {
                criteria.andEqualTo("images", sku.getImages());
            }
            // 创建时间
            if (!StringUtils.isEmpty(sku.getCreateTime())) {
                criteria.andEqualTo("createTime", sku.getCreateTime());
            }
            // 更新时间
            if (!StringUtils.isEmpty(sku.getUpdateTime())) {
                criteria.andEqualTo("updateTime", sku.getUpdateTime());
            }
            // SPUID
            if (!StringUtils.isEmpty(sku.getSpuId())) {
                criteria.andEqualTo("spuId", sku.getSpuId());
            }
            // 类目ID
            if (!StringUtils.isEmpty(sku.getCategory1Id())) {
                criteria.andEqualTo("brandId", sku.getCategory1Id());
            }
            //
            if (!StringUtils.isEmpty(sku.getCategory2Id())) {
                criteria.andEqualTo("category2Id", sku.getCategory2Id());
            }
            //
            if (!StringUtils.isEmpty(sku.getCategory3Id())) {
                criteria.andEqualTo("category3Id", sku.getCategory3Id());
            }
            //
            if (!StringUtils.isEmpty(sku.getCategory1Name())) {
                criteria.andEqualTo("category1Name", sku.getCategory1Name());
            }
            //
            if (!StringUtils.isEmpty(sku.getCategory2Name())) {
                criteria.andEqualTo("category2Name", sku.getCategory2Name());
            }
            // 类目名称
            if (!StringUtils.isEmpty(sku.getCategory3Name())) {
                criteria.andEqualTo("category3Name", sku.getCategory3Name());
            }
            //
            if (!StringUtils.isEmpty(sku.getBrandId())) {
                criteria.andEqualTo("category1Id", sku.getBrandId());
            }
            // 品牌名称
            if (!StringUtils.isEmpty(sku.getBrandName())) {
                criteria.andEqualTo("brandName", sku.getBrandName());
            }
            // 规格
            if (!StringUtils.isEmpty(sku.getSpec())) {
                criteria.andEqualTo("spec", sku.getSpec());
            }
            // 销量
            if (!StringUtils.isEmpty(sku.getSaleNum())) {
                criteria.andEqualTo("saleNum", sku.getSaleNum());
            }
            // 评论数
            if (!StringUtils.isEmpty(sku.getCommentNum())) {
                criteria.andEqualTo("commentNum", sku.getCommentNum());
            }
            // 商品状态 1-正常，2-下架，3-删除
            if (!StringUtils.isEmpty(sku.getStatus())) {
                criteria.andEqualTo("status", sku.getStatus());
            }

            // 秒杀开始时间
            if (!StringUtils.isEmpty(sku.getSeckillBegin())) {
                criteria.andGreaterThanOrEqualTo("seckillBegin", sku.getSeckillBegin());
            }
            // 秒杀结束时间
            if (!StringUtils.isEmpty(sku.getSeckillEnd())) {
                criteria.andLessThanOrEqualTo("seckillEnd", sku.getSeckillEnd());
            }
        }
        return example;
    }


    /**
     * 删除
     */
    @Override
    public void delete(String id) {
        //查询商品信息
        Sku currentsku = skuMapper.selectByPrimaryKey(id);

        if (currentsku != null) {
            //查询活动信息
            Activity activity = new Activity();
            activity.setBegintime(currentsku.getSeckillBegin());
            Activity currentActivity = activityMapper.selectOne(activity);
            //skuMapper.deleteByPrimaryKey(id);
            Sku sku = new Sku();
            sku.setId(id);
            sku.setStatus("1"); //非秒杀商品
            sku.setIslock(1);   //未锁定
            int count = skuMapper.updateByPrimaryKeySelective(sku);
            if (count > 0) {
                //移除关联
                SkuAct skuAct = new SkuAct();
                skuAct.setActivityId(currentActivity.getId());
                skuAct.setSkuId(currentsku.getId());
                skuActMapper.delete(skuAct);
            }
        }
    }

    /**
     * 修改Sku
     */
    @Override
    public void update(Sku sku) {
        skuMapper.updateByPrimaryKeySelective(sku);
    }

    /**
     * 增加Sku
     */
    @Override
    public void add(Sku sku) {
        skuMapper.insertSelective(sku);
    }

    /**
     * 根据ID查询Sku
     */
    @Override
    public Sku findById(String id) {
        return skuMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询Sku全部数据
     */
    @Override
    public List<Sku> findAll() {
        return skuMapper.selectAll();
    }

    /**
     * 锁定商品
     */
    @Override
    public void lock(String id) {
        skuMapper.lock(id);
    }

    /**
     * 解锁商品
     */
    @Override
    public void unlock(String id) {
        skuMapper.unlock(id);
    }


    /**
     * 根据活动ID查询
     */
    @Override
    public PageInfo<Sku> findSkuByActivityId(String id, Integer page, Integer size) {
        //分页
        PageHelper.startPage(page, size);

        //查询
        List<Sku> skuList = skuMapper.findSkuByActivityId(id);
        return new PageInfo<>(skuList);
    }


    /**
     * 提取所有ID
     */
    public List<String> getIds(List<Sku> skuList) {
        List<String> ids = new ArrayList<String>();

        for (Sku sku : skuList) {
            ids.add(sku.getId());
        }
        return ids;
    }

    /**
     * 归零设置
     */
    @Override
    public void zero(String id) {
        Sku sku = new Sku();
        sku.setId(id);
        sku.setSeckillNum(0);
        skuMapper.updateByPrimaryKeySelective(sku);
    }

    /**
     * 商品更新成非秒杀商品，未锁定商品
     */
    @Override
    public void modifySku() {
        //修改状态
        skuMapper.modifySku();
    }

    @Override
    public List<Sku> findTop(String id) {
        //查询对应的商品信息
        List<String> ids = skuActMapper.findSkuById(id);
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", ids);
        return skuMapper.selectByExample(example);
    }
}
