package com.fengchao.workorders.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface WorkOrderXMapper {

    /**
     * 获取商户的退货人数
     *
     * @param merchantId
     * @return
     */
    Integer selectRefundUserCountByMerchantId(@Param("merchantId") Long merchantId);
}
