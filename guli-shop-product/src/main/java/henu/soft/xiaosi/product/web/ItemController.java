package henu.soft.xiaosi.product.web;


import henu.soft.xiaosi.product.service.SkuInfoService;
import henu.soft.xiaosi.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ItemController {

    @Autowired
    private SkuInfoService skuInfoService;

    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) {
        System.out.println("===========");

        SkuItemVo skuItemVo = skuInfoService.item(skuId);
        System.out.println(skuItemVo);
        model.addAttribute("item", skuItemVo);
        return "item";
    }
}
