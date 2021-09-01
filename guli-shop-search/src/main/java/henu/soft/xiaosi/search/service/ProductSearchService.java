package henu.soft.xiaosi.search.service;

import henu.soft.xiaosi.search.vo.SearchParamVo;
import henu.soft.xiaosi.search.vo.SearchResultVo;

import java.util.List;

public interface ProductSearchService {


    SearchResultVo searchProductByParam(SearchParamVo searchParamVo);

}
