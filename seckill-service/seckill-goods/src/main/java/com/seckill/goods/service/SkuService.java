package com.seckill.goods.service;

import com.github.pagehelper.PageInfo;
import com.seckill.goods.pojo.Sku;

import java.util.List;

/**
 * @author http://www.itheima.com
 */
public interface SkuService {



    /**
     * Sku多条件分页查询
     */
    PageInfo<Sku> findPage(Sku sku, int page, int size);

    /**
     * Sku分页查询
     */
    PageInfo<Sku> findPage(int page, int size);

    /**
     * Sku多条件搜索方法
     */
    List<Sku> findList(Sku sku);

    /**
     * 删除Sku
     */
    void delete(String id);

    /**
     * 修改Sku数据
     */
    void update(Sku sku);

    /**
     * 新增Sku
     */
    void add(Sku sku);

    /**
     * 根据ID查询Sku
     */
    Sku findById(String id);

    /**
     * 查询所有Sku
     */
    List<Sku> findAll();

    /**
     * 锁定商品
     */
    void lock(String id);

    /**
     * 解锁商品
     */
    void unlock(String id);

    /**
     * 根据活动ID查询商品列表
     */
    PageInfo<Sku> findSkuByActivityId(String id, Integer page, Integer size);


    /**
     * 数据归零
     */
    void zero(String id);

    /**
     * 所有秒杀商品更新成普通商品
     */
    void modifySku();

    List<Sku> findTop(String id);
}
