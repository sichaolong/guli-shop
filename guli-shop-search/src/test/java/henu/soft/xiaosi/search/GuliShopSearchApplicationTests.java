package henu.soft.xiaosi.search;


import lombok.Data;
import net.minidev.json.JSONUtil;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.util.Map;

@SpringBootTest
class GuliShopSearchApplicationTests {


    @Autowired
    RestHighLevelClient client;


    @Test
    void contextLoads() {
        System.out.println(client); //org.elasticsearch.client.RestHighLevelClient@30bbe83
    }


    @Test
    void test1() throws IOException {
        Product product = new Product();
        product.setSpuName("华为");
        product.setId(10L);
        IndexRequest request = new IndexRequest("product").id("20")
                .source("spuName","华为","id",20L);
        try {

            // 1.
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            System.out.println(request.toString());
            IndexResponse response2 = client.index(request, RequestOptions.DEFAULT);
        } catch (ElasticsearchException e) {
            if (e.status() == RestStatus.CONFLICT) {
            }
        }
    }
    @Data
    class Product{

        private String SpuName;
        private Long id;

    }

    @Test
    void test2() throws IOException {
        Product product = new Product();
        product.setSpuName("苹果");
        product.setId(20L);

        IndexRequest indexRequest = new IndexRequest("product");

        Object o = JSONUtil.convertToStrict(product, Product.class);
        IndexRequest request = indexRequest.source(o,XContentType.JSON);

        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println(response);


    }


    @Test
    void test3() throws IOException {

        // 1. 查询api
        SearchRequest search = new SearchRequest();

        // 2. 指定索引
        search.indices("bank");

        // 3. 查询条件构建
        SearchSourceBuilder ssb = new SearchSourceBuilder();
        // 3.1查询
        ssb.query(QueryBuilders.matchAllQuery());

        // 3.2聚合

        // 按照年龄值分布聚合
        TermsAggregationBuilder aggAgg = AggregationBuilders.terms("agaAgg").field("aga").size(10);
        ssb.aggregation(aggAgg);

        // 按照薪资聚合求均值
        TermsAggregationBuilder balanceAgg = AggregationBuilders.terms("balanceAgg").field("balance").size(10);
        ssb.aggregation(balanceAgg);

        // 4. 组合查询条件
        search.source(ssb);

        // 5. 执行查询
        SearchResponse response = client.search(search, RequestOptions.DEFAULT);

        // 6. 解析结果（Hits 和 聚合的值)

        // 6.1 获取所有查到的数据
        // 拿到外边的hits
        SearchHits hits = response.getHits();
        // 拿到里面的数据hits
        SearchHit[] dataHits = hits.getHits();

        for (SearchHit dataHit : dataHits) {

            String id = dataHit.getId();
            String index = dataHit.getIndex();
            float score = dataHit.getScore();
            String type = dataHit.getType();
            long seqNo = dataHit.getSeqNo();
            Map<String, Object> sourceAsMap = dataHit.getSourceAsMap();

            System.out.println(id +"==" + index +"==" + score +"==" + type +"==" + seqNo);
            System.out.println(sourceAsMap);



        }

        System.out.println(response);

    }
}
