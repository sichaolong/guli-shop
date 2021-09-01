package henu.soft.xiaosi.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import henu.soft.common.utils.PageUtils;
import henu.soft.xiaosi.member.entity.MemberReceiveAddressEntity;

import java.util.List;
import java.util.Map;

/**
 * 会员收货地址
 *
 * @author xiaosi
 * @email 2589165806@qq.com
 * @date 2021-07-22 23:33:19
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddressEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查询用户收货地址列表
     * @param userId
     * @return
     */
    List<MemberReceiveAddressEntity> getAddressByUserId(Long userId);

}

