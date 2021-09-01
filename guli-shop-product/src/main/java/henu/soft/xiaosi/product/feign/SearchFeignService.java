package henu.soft.xiaosi.product.feign;

import henu.soft.common.to.es.SkuEsModel;
import henu.soft.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("guli-shop-search")
public interface SearchFeignService {


    @PostMapping("/search/save/product")
    public R saveProductAsIndices(@RequestBody List<SkuEsModel> skuEsModels);

}
