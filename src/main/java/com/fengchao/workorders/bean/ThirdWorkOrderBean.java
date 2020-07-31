package com.fengchao.workorders.bean;

import com.fengchao.workorders.model.WorkFlow;
import com.fengchao.workorders.model.WorkOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author songbw
 * @date 2020/7/31 10:47
 */

@Setter
@Getter
public class ThirdWorkOrderBean extends WorkOrder {
    private List<WorkFlow> workFlows ;
}
