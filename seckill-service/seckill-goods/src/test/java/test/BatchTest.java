package test;

import com.seckill.GoodsApplication;
import com.seckill.goods.pojo.Sku;
import com.seckill.goods.service.SkuService;
import com.seckill.util.IdWorker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GoodsApplication.class)
public class BatchTest {

    @Autowired
    private SkuService skuService;
    @Autowired
    private IdWorker idWorker;

    @Test
    public void test() {
        int count = 1000 * 10;
        //======================================

        List<Sku> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Sku sku = getSku();
            list.add(sku);
        }
        long now = System.currentTimeMillis();
        skuService.batch(list);
        System.out.println("第一次测试：" + (System.currentTimeMillis() - now));

        //======================================

        list.clear();
        for (int i = 0; i < count; i++) {
            Sku sku = getSku();
            list.add(sku);
        }
        now = System.currentTimeMillis();
        skuService.batch(list);
        System.out.println("第二次测试：" + (System.currentTimeMillis() - now));

        //======================================

        list.clear();
        for (int i = 0; i < count; i++) {
            Sku sku = getSku();
            list.add(sku);
        }
        now = System.currentTimeMillis();
        skuService.batch(list);
        System.out.println("第三次测试：" + (System.currentTimeMillis() - now));
    }


    private Sku getSku() {
        Sku sku = new Sku();

        sku.setId(idWorker.nextId() + "");
        sku.setName("液晶电视机高清网络wifi智能平板彩电");
        sku.setPrice(1000);
        sku.setSeckillPrice(900);
        sku.setNum(100);
        sku.setAlertNum(200);
        sku.setImage("https://img12.360buyimg.com/n7/jfs/t1/88153/11/12539/121483/5e4b4d7dE3523eb05/55f70133f03daa7c.jpg");
        sku.setImages("https://img12.360buyimg.com/n7/jfs/t1/88153/11/12539/121483/5e4b4d7dE3523eb05/55f70133f03daa7c.jpg");
        sku.setCreateTime(new Date());
        sku.setUpdateTime(new Date());
        sku.setSeckillBegin(new Date());
        sku.setSeckillEnd(new Date());
        sku.setSpuId("123");
        sku.setCategory1Id(74);
        sku.setCategory2Id(75);
        sku.setCategory3Id(76);
        sku.setCategory1Name("家用电器");
        sku.setCategory2Name("大家电");
        sku.setCategory3Name("平板电视");
        sku.setBrandId(7817);
        sku.setBrandName("海尔");
        sku.setSaleNum(300);
        sku.setCommentNum(745);
        sku.setStatus("2");
        sku.setIslock(2);
        sku.setSeckillNum(1001);
        sku.setAudit(1);
        sku.setCount(1);
        sku.setIsdel(1);

        return sku;
    }
}
