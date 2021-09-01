package henu.soft.xiaosi.ware.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.TypeReference;
import henu.soft.common.utils.R;
import henu.soft.xiaosi.ware.feign.MemberFeignService;
import henu.soft.xiaosi.ware.vo.FareVo;
import henu.soft.common.to.MemberAddressTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import henu.soft.common.utils.PageUtils;
import henu.soft.common.utils.Query;

import henu.soft.xiaosi.ware.dao.WareInfoDao;
import henu.soft.xiaosi.ware.entity.WareInfoEntity;
import henu.soft.xiaosi.ware.service.WareInfoService;



@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {



    @Autowired
    MemberFeignService memberFeignService;


//    重写模糊查询
    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        QueryWrapper<WareInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");

        if (!StringUtils.isEmpty(key)){
            wrapper.eq("id",key)
                    .or().like("name",key)
                    .or().like("address",key)
                    .or().like("areacode",key);
        }
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }


    /**
     * 根据地址查询运费
     * @param addrId
     * @return
     */
    @Override
    public FareVo getFare(Long addrId) {

        FareVo fareVo = new FareVo();
        R info = memberFeignService.addressInfo(addrId);
        if (info.getCode() == 0) {
            MemberAddressTo address = info.getData(new TypeReference<MemberAddressTo>() {
            });
            fareVo.setAddress(address);
            String phone = address.getPhone();
            //取电话号的最后两位作为邮费
            String fare = phone.substring(phone.length() - 2, phone.length());
            fareVo.setFare(new BigDecimal(fare));
        }
        return fareVo;
    }

}
