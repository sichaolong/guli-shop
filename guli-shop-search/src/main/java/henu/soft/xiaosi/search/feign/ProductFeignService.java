package henu.soft.xiaosi.search.feign;


import henu.soft.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("guli-shop-product")
public interface ProductFeignService {



    @RequestMapping("product/attr/info/{attrId}")
    R info(@PathVariable("attrId") Long attrId);

}
