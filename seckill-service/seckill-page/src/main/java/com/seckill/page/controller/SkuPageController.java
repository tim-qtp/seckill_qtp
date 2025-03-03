package com.seckill.page.controller;

import com.alibaba.fastjson.JSON;
import com.seckill.goods.feign.SkuFeign;
import com.seckill.goods.pojo.Sku;
import com.seckill.page.service.SkuPageService;
import com.seckill.util.Result;
import com.seckill.util.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/page")
public class SkuPageController {


    @Value("${htmlPath}")
    private String htmlPath;

    @Autowired
    private SkuPageService skuPageService;

    @Autowired
    private SkuFeign skuFeign;

    /**
     * 测试商品详情静态页
     */
    @PostMapping(value = "/html")
    public Result html() throws Exception {
        String id = "1234";
        //数据模型
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("templateName", "test.ftl"); //模板名字
        dataMap.put("name", id + ".html");  //生成静态页的文件名字
        dataMap.put("path", htmlPath);      //生成的静态页路径

        dataMap.put("title", "笔记本电脑"); //数据
        dataMap.put("arr", new String[]{"1", "2", "3"}); //集合

        //生成静态页
        skuPageService.writePage(dataMap);
        return new Result(true, StatusCode.OK, "生成成功！");
    }

    /**
     * 删除商品详情静态页
     */
    @DeleteMapping(value = "/html/{id}")
    public Result delHtml(@PathVariable(value = "id") String id) throws Exception {
        //删除静态页
        skuPageService.delItemPage(id, htmlPath);
        return new Result(true, StatusCode.OK, "删除成功！");
    }

    /**
     * Sku静态页生成
     */
    @GetMapping(value = "/html/{id}")
    public Result html(@PathVariable(value = "id") String id) throws Exception {
        Result<Sku> skuResult = skuFeign.findById(id);
        //拿到sku后，对sku进行解析，页面用到什么数据，这里就放什么数据
        //数据模型
        Map<String, Object> dataMap = new HashMap<String, Object>();
         dataMap.put("templateName", "sku.ftl");//模板名字
//        dataMap.put("templateName", "sku_cdn.ftl");//模板名字,静态资源从cdn获取
        dataMap.put("name", id + ".html");  //id.html
        dataMap.put("path", htmlPath);      //生成的静态页路径

        dataMap.put("sku", skuResult.getData());
        dataMap.put("images", skuResult.getData().getImages().split(","));

        //获取spec规格
        String spec = skuResult.getData().getSpec();
        Map<String, String> specMap = JSON.parseObject(spec, Map.class);
        String speclist = "";
        for (Map.Entry<String, String> entry : specMap.entrySet()) {
            speclist += "/" + entry.getValue();
        }
        dataMap.put("spec", speclist);

        //生成静态页
        skuPageService.writePage(dataMap);
        return new Result(true, StatusCode.OK, "生成静态页成功！");
    }
}