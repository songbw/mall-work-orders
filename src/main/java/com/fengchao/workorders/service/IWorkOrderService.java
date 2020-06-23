package com.fengchao.workorders.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fengchao.workorders.bean.AggPayNotifyBean;
import com.fengchao.workorders.bean.AoYiRefundResponseBean;
import com.fengchao.workorders.bean.GuanAiTongNotifyBean;
import com.fengchao.workorders.entity.WorkOrder;
import com.fengchao.workorders.util.PageInfo;

import java.util.Date;
import java.util.List;

/**
 * @author Clark
 * */
public interface IWorkOrderService extends IService<WorkOrder> {

    WorkOrder selectByRefundNo(String refundNo) throws Exception;

    PageInfo<WorkOrder> selectPage(int page, int rows, String iAppId,
                                   String title, String receiverId, String receiverName, String receiverPhone,
                                   String orderId, Integer typeId, Long merchantId,Integer status,
                                   String createTimeStart, String createTimeEnd,String refundTimeBegin, String refundTimeEnd) throws Exception;

    List<WorkOrder> selectByOrderIdList(List<String> orderIdList) throws Exception;


    List<WorkOrder> selectByParentOrderId(Integer parentOrderId) throws Exception;

    List<WorkOrder> selectByTimeRange(String createTimeStart, String createTimeEnd) throws Exception;

    PageInfo<WorkOrder> selectAbnormalRefundList(int pageIndex, int pageSize,
                                             String iAppId,
                                             String orderId, Long merchantId,
                                             String createTimeStart, String createTimeEnd
    ) throws Exception;
    /**
     * 获取商户的退货人数
     *
     * @param merchantId 商户ID
     * @return count
     */
    Integer queryRefundUserCount(Long merchantId);

    int countReturn(String createTimeStart, String createTimeEnd) throws Exception;

    WorkOrder getValidNumOfOrder(String openId, String sbuOrderId);

    JSONObject getOrderInfo(String openId, String sbuOrderId, Long merchantId) throws Exception;

    String handleNotify(GuanAiTongNotifyBean backBean) throws Exception;

    String handleAggPaysNotify(AggPayNotifyBean bean) throws Exception;

    /**
     * 查询退款成功的子订单
     *
     * @param merchantId
     * @param startTime
     * @param endTime
     * @return
     * @throws Exception
     */
    List<WorkOrder> querySuccessRefundOrderDetailIdList(String iAppId,Long merchantId, String startTime, String endTime) throws Exception;

    String sendRefund2GuangAiTong(Long workOrderId, Integer handleFare, Float refund) throws Exception;

    /**
     * 向怡亚通发送退货物流信息
     * @param workOrder 工单信息
     * @param comments 注释
     * */
    void sendExpressInfo(WorkOrder workOrder, String comments);

    /**
     * 向怡亚通发送退款申请
     * @param reason 退货原因说明
     * @param subStatus 怡亚通订单状态
     * @param thirdOrderSn 怡亚通订单id
     * @param skuId 商品id
     * @return  AoYiRefundResponseBean 怡亚通返回信息
     * */
    AoYiRefundResponseBean
    getYiYaTongRefundNo(String reason,Integer subStatus,String thirdOrderSn,String skuId);

    }
