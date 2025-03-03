package com.seckill.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.seckill.goods.feign.SkuFeign;
import com.seckill.goods.pojo.Sku;
import com.seckill.search.pojo.SkuInfo;
import com.seckill.search.service.SkuInfoService;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SkuInfoServiceImpl implements SkuInfoService {

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    private String index_name = "goodsindex";

    /**
     * 秒杀搜索列表
     */
    //TODO：不懂，后续学完ES需要看
    @Override
    public Page<SkuInfo> search(Map<String, String> searchMap) {
        //时间  starttime
        String starttime = searchMap.get("starttime");
        //页码数
        int pageNum = pageNum2Int(searchMap);
        int pageSize = pageSize2Int(searchMap);

        SearchRequest searchRequest = new SearchRequest("goodsindex");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //范围查询
        RangeQueryBuilder query = QueryBuilders.rangeQuery("bgtime");
        //指定下限
        query.lte(starttime);
        sourceBuilder.query(query);

        //设置分页
        sourceBuilder.from(pageNum);
        sourceBuilder.size(pageSize);

        searchRequest.source(sourceBuilder);


        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            SearchHits searchHits = searchResponse.getHits();

            //获取记录数
            long total = searchHits.getTotalHits().value;

            List<SkuInfo> skuInfoList = new ArrayList<>();
            SearchHit[] hits = searchHits.getHits();
            for (SearchHit hit : hits) {
                String sourceAsString = hit.getSourceAsString();
                //转为java
                SkuInfo skuInfo = JSON.parseObject(sourceAsString, SkuInfo.class);
                skuInfoList.add(skuInfo);
            }

            Pageable pageable = PageRequest.of(pageNum, pageSize).first();
            return new PageImpl<>(skuInfoList, pageable, total);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前页->pageNum
     */
    private Integer pageNum2Int(Map<String, String> searchMap) {
        try {
            return Integer.parseInt(searchMap.get("pageNum"));
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    /**
     * 获取当前页->pageSize
     */
    private Integer pageSize2Int(Map<String, String> searchMap) {
        try {
            return Integer.parseInt(searchMap.get("pageSize"));
        } catch (NumberFormatException e) {
            return 50;
        }
    }

    /**
     * 增量操作
     * ->添加索引   type=1
     * ->修改索引   type=2
     * ->删除索引   type=3
     */
    @Override
    public void modify(Integer type, SkuInfo skuInfo) {
        try {
            if (type == 1 || type == 2) {
                // 将开始时间转换成字符串类型
                skuInfoConverter(skuInfo);

                //将对象转为json
                String data = JSON.toJSONString(skuInfo);
                IndexRequest request = new IndexRequest(index_name).id(skuInfo.getId()).source(data, XContentType.JSON);

                //增加-修改
                restHighLevelClient.index(request, RequestOptions.DEFAULT);

            } else {
                //删除
                DeleteRequest deleteRequest = new DeleteRequest(index_name, skuInfo.getId());
                restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量导入
     */
    @Override
    public void addAll() {
        //1.查询总记录数
        Integer total = skuFeign.count();

        //分页数据
        int page = 1, size = 100;
        //2.根据总记录数计算总页数
        int totalpages = total % size == 0 ? total / size : (total / size) + 1;
//        System.out.println(page+","+totalpages);
        //3.循环总页数，查询每页的数据
        for (int i = 0; i < totalpages; i++) {
            List<Sku> skus = skuFeign.list(page, size);
            page++;

            // 4.将数据转换成SkuInfo
            List<SkuInfo> skuInfos = JSON.parseArray(JSON.toJSONString(skus), SkuInfo.class);

            // 5. bulk批量导入
            BulkRequest bulkRequest = new BulkRequest();

            // 5.1 循环, 创建IndexRequest添加数据
            for (SkuInfo skuInfo : skuInfos) {
                // 将开始时间转换成字符串类型
                skuInfoConverter(skuInfo);

                //将skuInfo对象转换为json字符串，可能是{"id":123,"name":"商品名称"}
                String data = JSON.toJSONString(skuInfo);
//                System.out.println(data);
                // 创建一条索引操作请求，即数据将被存储到 Elasticsearch 的哪个索引中
                IndexRequest indexRequest = new IndexRequest(index_name);
                // 为这条记录设置文档 ID。在 Elasticsearch 中，每条记录（文档）可以有一个唯一的 ID，这里使用了 skuInfo 对象的 id 字段作为文档 ID。
                // 将 JSON 字符串作为文档的内容，并指定内容类型为 JSON。XContentType.JSON 表示内容是 JSON 格式。
                indexRequest.id(skuInfo.getId()).source(data, XContentType.JSON);

                //添加批量保存
                bulkRequest.add(indexRequest);
            }

            try {
                //5.2 执行批量操作
                restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //分页查询-页码数
    private volatile AtomicInteger page;

    /**
     * 批量导入
     */
    // @Override
    public void addAll2() {
        //1.查询总记录数
        Integer total = skuFeign.count();

        //分页查询-页码数
        page = new AtomicInteger(1);
        //分页查询-页面大小
        int size = 500;
        //2.根据总记录数计算总页数
        int totalpages = total % size == 0 ? total / size : (total / size) + 1;

        //计数闭锁
        CountDownLatch countDownLatch = new CountDownLatch(totalpages);
        //创建线程池
        ExecutorService pool = Executors.newFixedThreadPool(8);

        //3.循环总页数，查询每页的数据
        for (int i = 0; i < totalpages; i++) {
            pool.execute(() -> {
                List<Sku> skus = skuFeign.list(page.getAndIncrement(), size);

                // 4.将数据转换成SkuInfo
                List<SkuInfo> skuInfos = JSON.parseArray(JSON.toJSONString(skus), SkuInfo.class);

                //bulk批量导入
                BulkRequest bulkRequest = new BulkRequest();

                //2.1 循环, 创建IndexRequest添加数据
                for (SkuInfo skuInfo : skuInfos) {
                    // 将开始时间转换成字符串类型
                    skuInfoConverter(skuInfo);

                    //将skuInfo对象转换为json字符串
                    String data = JSON.toJSONString(skuInfo);
                    IndexRequest indexRequest = new IndexRequest(index_name);
                    indexRequest.id(skuInfo.getId()).source(data, XContentType.JSON);//设置索引库的唯一标识符为商品ID
                    // indexRequest.source(data, XContentType.JSON);//不设置索引库的唯一标识符

                    //添加批量保存
                    bulkRequest.add(indexRequest);
                }

                try {
                    restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            });
        }

        try {
            // 主线程等待所有的子线程countDown完成
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 将开始时间转换成字符串类型
    private void skuInfoConverter(SkuInfo skuInfo) {
        if (skuInfo.getSeckillBegin() != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHH");
            skuInfo.setBgtime(simpleDateFormat.format(skuInfo.getSeckillBegin()));
        }
    }
}
