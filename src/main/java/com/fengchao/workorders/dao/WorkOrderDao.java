package com.fengchao.workorders.dao;

import com.fengchao.workorders.model.WorkOrder;
import com.fengchao.workorders.util.PageInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface WorkOrderDao {
    int deleteByPrimaryKey(Long id);

    int insert(WorkOrder record);

    int insertSelective(WorkOrder record);

    WorkOrder selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(WorkOrder record);

    int updateByPrimaryKey(WorkOrder record);

    List<WorkOrder> selectByOrderId(String orderId) throws Exception;

    List<WorkOrder> selectByOrderIdList(List<String> orderIdList) throws Exception;

    PageInfo<WorkOrder> selectRange(int pageIndex, int pageSize,String sort, String order,
                                    String iAppId,
                                    String title,
                                    String receiverId,
                                    String receiverPhone,
                                    String receiverName,
                                    String orderId,
                                    Long merchantId,
                                    Integer typeId,
                                    Integer status,
                                    Date createTimeStart,
                                    Date createTimeEnd,Date refundTimeBegin, Date refundTimeEnd) throws Exception;

    int countType(Integer typeId,
                  Date createTimeStart,
                  Date createTimeEnd);

    WorkOrder selectByRefundNo(String refundNo);

    /**
     * 获取商户的退货人数
     *
     * @param merchantId
     * @return
     */
    Integer selectRefundUserCountByMerchantId(@Param("merchantId") Long merchantId) throws Exception;

    /**
     * 查询退款成功的记录
     *
     * @param merchantId
     * @param startTime
     * @param endTime
     * @return
     */
    List<WorkOrder> selectRefundSuccessOrderDetailIdList(String iAppId,Long merchantId, Date startTime, Date endTime);

    PageInfo<WorkOrder> selectAbnormalRefund(int pageIndex, int pageSize,String sort, String order,
                                             String iAppId,
                                             String orderId, Long merchantId,
                                             Date createTimeStart, Date createTimeEnd
    ) throws Exception;
}
