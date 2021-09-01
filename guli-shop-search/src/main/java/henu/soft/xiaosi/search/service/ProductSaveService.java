package henu.soft.xiaosi.search.service;

import henu.soft.common.to.es.SkuEsModel;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


public interface ProductSaveService {
    boolean saveProductAsIndices(List<SkuEsModel> skuEsModels) throws IOException;

}
