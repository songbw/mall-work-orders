package com.fengchao.workorders.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * @author songbw
 * @date 2020/9/28 21:51
 */
@Setter
@Getter
public class WorkOrderQueryBean {
    int pageIndex;
    int pageSize;
    String sort;
    String order;
    String iAppId;
    String title;
    String receiverId;
    String receiverName;
    String receiverPhone;
    String orderId;
    Integer typeId;
    Long merchantId;
    Integer status;
    Date createTimeStart;
    Date createTimeEnd;
    Date refundTimeBegin;
    Date refundTimeEnd ;
    List<Integer> merchantIds ;
    private Integer merchantHeader;
    private String renterHeader;
    private String renterId ;
}
