package com.fengchao.workorders.service;

import com.fengchao.workorders.bean.CustomerQueryWorkOrderBean;
import com.fengchao.workorders.bean.CustomerWorkOrderBean;
import com.fengchao.workorders.bean.WorkFlowBodyBean;
import com.fengchao.workorders.dto.ParentOrderRefundData;
import com.fengchao.workorders.dto.WorkFlowBeanList;
import com.fengchao.workorders.entity.WorkFlow;
import com.fengchao.workorders.entity.WorkOrder;
import com.fengchao.workorders.util.PageInfo;

public interface IAppSideWorkOrderService {

    WorkFlowBeanList
    queryWorkFlows(String renterId, Long workId);

    WorkFlow
    handleWorkOrder(String renterId, WorkFlowBodyBean data);

    WorkOrder
    createWorkOrder(String renterId, CustomerWorkOrderBean data);

    WorkOrder
    updateWorkOrder(String renterId, CustomerWorkOrderBean data, Long id);

    PageInfo<CustomerQueryWorkOrderBean>
    queryWorkOrder(String renterId,Integer pageIndex,Integer pageSize,String customer,String orderId);

    Integer
    getRefundValidCount(String renterId,String customer,String orderId,Long merchantId);

    ParentOrderRefundData
    getParentOrderRefund(String renterId,String orderId);

}
