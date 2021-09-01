package henu.soft.xiaosi.ware.feign;

import henu.soft.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("guli-shop-member")
public interface MemberFeignService {


    /**
     * 根据选中的地址查运费
     * @param id
     * @return
     */
    @RequestMapping("member/memberreceiveaddress/addressinfo/{id}")
    R addressInfo(@PathVariable("id") Long id);
}
