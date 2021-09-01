package henu.soft.xiaosi.search.web;

import henu.soft.xiaosi.search.service.ProductSearchService;
import henu.soft.xiaosi.search.vo.SearchParamVo;
import henu.soft.xiaosi.search.vo.SearchResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class SearchController {

    @Autowired
    ProductSearchService productSearchService;

    @GetMapping("/list.html")
    public String listPage(SearchParamVo searchParamVo, Model model){

        // 处理检索属性
      SearchResultVo searchResultVo =  productSearchService.searchProductByParam(searchParamVo);
      model.addAttribute("result",searchResultVo);

        return "list";
    }
}
