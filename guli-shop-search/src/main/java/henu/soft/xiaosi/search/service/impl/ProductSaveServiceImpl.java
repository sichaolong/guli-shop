package henu.soft.xiaosi.search.service.impl;


import com.alibaba.fastjson.JSON;
import henu.soft.common.to.es.SkuEsModel;
import henu.soft.xiaosi.search.constant.EsConstant;
import henu.soft.xiaosi.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {

    @Autowired

    RestHighLevelClient restHighLevelClient;

    /**
     * 商品上级，检索信息存入ES
     * @param skuEsModels
     * @return
     */
    @Override
    public boolean saveProductAsIndices(List<SkuEsModel> skuEsModels) throws IOException {

        // 保存到es

        // 1. 给es建立索引 product 建立好映射关系，在可视化界面创建

        // 2. 保存数据 参数 BulkRequest bulkRequest, RequestOptions options

        BulkRequest bulkRequest = new BulkRequest();

        for (SkuEsModel skuEsModel : skuEsModels) {
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(skuEsModel.getSkuId().toString());

            // 保存的内容转为json
            String s = JSON.toJSONString(skuEsModel);
            indexRequest.source(s, XContentType.JSON);

            bulkRequest.add(indexRequest);
        }
        BulkResponse response = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);

        boolean existError = response.hasFailures();

        List<String> Ids = Arrays.stream(response.getItems()).map(item -> {
            return item.getId();
        }).collect(Collectors.toList());


        // TODO 批量保存是否存在错误

        log.info("商品上架成功: {}",Ids);

        return existError;

    }
}
