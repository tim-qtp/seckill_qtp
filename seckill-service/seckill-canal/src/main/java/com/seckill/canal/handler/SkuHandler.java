package com.seckill.canal.handler;

import com.seckill.goods.pojo.Sku;
import com.seckill.page.feign.SkuPageFeign;
import com.seckill.search.feign.SkuInfoFeign;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.javatool.canal.client.annotation.CanalTable;
import top.javatool.canal.client.handler.EntryHandler;

@Component
@CanalTable(value = "tb_sku")
public class SkuHandler implements EntryHandler<Sku> {

    @Autowired
    private SkuInfoFeign skuInfoFeign;

    @Autowired
    private SkuPageFeign skuPageFeign;

    /**
     * 增加数据监听
     */
    @SneakyThrows
    @Override
    public void insert(Sku sku) {
        // status=2 表示为秒杀商品，进行相关新增操作
        if (sku.getStatus().equals("2")) {
            //同步索引
            skuInfoFeign.modifySku(1, sku);
            //同步静态页
            skuPageFeign.html(sku.getId());
            System.out.println("===========insert:"+sku);
        }

    }

    /**
     * 修改数据监听
     */
    @SneakyThrows
    @Override
    public void update(Sku before, Sku after) {
        if (after.getStatus().equals("2")) {
            //同步索引库
            skuInfoFeign.modifySku(2, after);
            //同步静态页
            skuPageFeign.html(after.getId());
            //开始状态为2，修改后为1的话，说明改为了普通商品，删除索引
        } else if (before.getStatus().equals("2") && after.getStatus().equals("1")) {
            //删除索引库
            skuInfoFeign.modifySku(3, after);
            //删除静态页
            skuPageFeign.delHtml(after.getId());
            System.out.println("===========update-before:"+before);
            System.out.println("===========update-after:"+after);
        }

        //逻辑删除，判断状态isdel
        if (after.getIsdel() == 2) {
            //删除索引库
            skuInfoFeign.modifySku(3, after);
            //删除静态页
            skuPageFeign.delHtml(after.getId());
        }
    }

    /**
     * 删除数据监听 商品只做逻辑删除，没有物理删除，所以不用处理删除监听
     */
    @Override
    public void delete(Sku sku) {
    }
}