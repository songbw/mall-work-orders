package com.fengchao.workorders.controller;

import com.fengchao.workorders.bean.*;
import com.fengchao.workorders.model.*;
import com.fengchao.workorders.service.impl.OrderTypeServiceImpl;
import com.fengchao.workorders.util.*;
import io.swagger.annotations.*;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.http.HttpStatus;
//import java.text.ParseException;
//import java.util.ArrayList;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
//import java.util.HashMap;
import java.util.List;

@Slf4j
@Api(tags="OrderTypeAPI", description = "工单类型相关", produces = "application/json;charset=UTF-8")
@RestController
@RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)

public class OrderTypeController {

    //private static Logger log = LoggerFactory.getLogger(OrderTypeController.class);

    private OrderTypeServiceImpl orderTypeService;


    @Autowired
    public OrderTypeController(
                          OrderTypeServiceImpl orderTypeService){

        this.orderTypeService = orderTypeService;
    }

    @ApiModel(value="工单类型列表")
    private class OrderTypeListResponseData implements Serializable {

        public List<OrderTypeBean> list;
    }

    @ApiModel(value="Id信息")
    private class IdBean implements Serializable {
        @ApiModelProperty(value="ID", example="12345678901",required=true)
        public Long id;

    }

    @ApiOperation(value = "获取工单类型列表", notes="Header中必须包含Token")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("order_types/list")
    public OrderTypeListResponseData getOrderTypeList(HttpServletResponse response,
                                     @RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication
                                     ) {
        OrderTypeListResponseData result = new OrderTypeListResponseData();
        List<OrderTypeBean> list = new ArrayList<>();
        List<OrderType> orderTypes = orderTypeService.selectAll();

        String username = JwtTokenUtil.getUsername(authentication);

        if (null == username) {
            log.warn("can not find username in token");
        }
        result.list = list;
        if (null != orderTypes && orderTypes.size() > 0) {
            for (OrderType o: orderTypes) {
                OrderTypeBean bean = new OrderTypeBean();
                BeanUtils.copyProperties(o, bean);
                list.add(bean);
            }
        }

        response.setStatus(MyErrorMap.e200.getCode());
        return result;
    }

    @ApiOperation(value = "增加工单类型", notes="Header中必须包含Token")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to create OrderType") })
    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("order_types")
    //@PreAuthorize("hasPermission('OrderType','insert')")
    public IdBean insertOrderType(HttpServletResponse response,
                            @RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication,
                            @RequestBody OrderTypeBodyBean data ) {

        IdBean result = new IdBean();

        String orderTypeName = data.getName();
        String workflowText = data.getWorkflowText();
        String workflowUrl = data.getWorkflowUrl();

        if (null == orderTypeName || orderTypeName.isEmpty()
            || null == workflowText || 0 == workflowText.length()) {
            StringUtil.throw400Exp(response,"400002: 工单类型名, 工作流程文字说明不能为空");
        }

        OrderType orderType = orderTypeService.selectByName(orderTypeName);
        if (null != orderType) {
            StringUtil.throw400Exp(response,"400002: 工单类型名已经存在");
        } else {
            orderType = new OrderType();
        }

        if (null != workflowUrl && !workflowUrl.isEmpty()) {
            orderType.setWorkflowUrl(workflowUrl);
        }
        orderType.setName(orderTypeName);
        orderType.setWorkflowText(workflowText);
        orderType.setCreateTime(new Date());
        orderType.setUpdateTime(new Date());

        String username = JwtTokenUtil.getUsername(authentication);
        if (null != username && !username.isEmpty()) {
            orderType.setCreatedBy(username);
            orderType.setUpdatedBy(username);
        }

        Long id = orderTypeService.insert(orderType);
        if (0 == id) {
            StringUtil.throw400Exp(response,"400003: Failed to create work_order type");
        }

        result.id = id;
        response.setStatus(MyErrorMap.e201.getCode());
        return result;
    }

    @ApiOperation(value = "获取指定工单类型信息", notes="Header中必须包含Token")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find OrderType") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("order_types/{id}")
    //@PreAuthorize("hasPermission('OrderType','list')")
    public OrderTypeBean selectOrderTypeById(HttpServletResponse response,
                                      @RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication,
                                      @ApiParam(value="id",required=true)@PathVariable("id") Long id ) {

        String username = JwtTokenUtil.getUsername(authentication);

        if (null == username) {
            log.warn("can not find username in token");
        }

        OrderTypeBean result = new OrderTypeBean();
        if (null == id) {
            StringUtil.throw400Exp(response,"400002: ID为空");
            return result;
        }

        OrderType orderType = orderTypeService.selectById(id);
        if (null == orderType) {
            StringUtil.throw400Exp(response,"400003: Failed to find order_type");
            return result;
        }


        BeanUtils.copyProperties(orderType, result);

        return result;
    }

    @ApiOperation(value = "更新指定工单类型", notes="Header中必须包含Token")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to update OrderType") })
    @ResponseStatus(code = HttpStatus.CREATED)
    @PutMapping("order_types/{id}")
    //@PreAuthorize("hasPermission('OrderType','insert')")
    public IdBean updateOrderTypeById(HttpServletResponse response,
                            @RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication,
                            @ApiParam(value="id",required=true)@PathVariable("id") Long id,
                            @RequestBody OrderTypeBodyBean data ) {

        IdBean result = new IdBean();
        String name = data.getName();
        String workflowText = data.getWorkflowText();
        String workflowUrl = data.getWorkflowUrl();

        if (null == id || 0 == id) {
            StringUtil.throw400Exp(response,"400002: id is wrong");
            return result;
        }

        OrderType orderType = orderTypeService.selectById(id);
        if (null == orderType) {
            StringUtil.throw400Exp(response,"400003: 工单类型不存在");
            return result;
        }

        if (null != name && !orderType.getName().equals(name)) {
            OrderType o = orderTypeService.selectByName(name);
            if (null != o) {
                StringUtil.throw400Exp(response, "400002: 工单类型名已经存在");
            }
        }

        if (null != name && !name.isEmpty()) {
            orderType.setName(name);
        }
        if (null != workflowText && !workflowText.isEmpty()) {
            orderType.setWorkflowText(workflowText);
        }
        if (null != workflowUrl && !workflowUrl.isEmpty()) {
            orderType.setWorkflowUrl(workflowUrl);
        }
        orderType.setUpdateTime(new Date());

        String username = JwtTokenUtil.getUsername(authentication);
        if (null != username && !username.isEmpty()) {
            orderType.setUpdatedBy(username);
        }

        orderTypeService.update(orderType);

        result.id = id;
        response.setStatus(MyErrorMap.e201.getCode());
        return result;
    }

    @ApiOperation(value = "删除指定工单类型", notes="Header中必须包含Token")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to delete OrderType") })
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @DeleteMapping("order_types/{id}")
    //@PreAuthorize("hasPermission('OrderType','delete')")
    public void deleteOrderTypeById(HttpServletResponse response,
                                 @RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication,
                                 @ApiParam(value="id",required=true)@PathVariable("id") Long id
                                 ) {

        String username = JwtTokenUtil.getUsername(authentication);

        if (null == username) {
            log.warn("can not find username in token");
        }

        if (null == id || 0 == id) {
            StringUtil.throw400Exp(response,"400002: Id is wrong");
            return;
        }

        OrderType orderType = orderTypeService.selectById(id);
        if (null == orderType) {
            StringUtil.throw400Exp(response,"400003: 工单类型不存在");
        }

        orderTypeService.deleteById(id);

        response.setStatus(MyErrorMap.e204.getCode());

    }


    @ApiOperation(value = "查询工单类型", notes="Header中必须包含Token")
    @GetMapping("order_types/pages")
    //@PreAuthorize("hasPermission('orderType','list')")
    public PageInfo<OrderTypeBean> getPages(HttpServletResponse response,
                                            @RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication,
                                            @ApiParam(value="获取页的序号",required=false)@RequestParam(required=false) Integer pageIndex,
                                            @ApiParam(value="每页返回的最大数",required=false)@RequestParam(required=false) Integer pageSize,
                                            @ApiParam(value="名称")@RequestParam(required=false) String name,
                                            @ApiParam(value="反馈提交开始时间")@RequestParam(required=false) String createTimeStart,
                                            @ApiParam(value="反馈提交结束时间")@RequestParam(required=false) String createTimeEnd

    ) {

        String username = JwtTokenUtil.getUsername(authentication);

        if (null == username) {
            log.warn("can not find username in token");
        }

        if (null == pageIndex || 0 >= pageIndex) {
            pageIndex = 1;
        }
        if (null == pageSize || 0>= pageSize) {
            pageSize = 10;
        }

        java.util.Date dateCreateTimeStart = null;
        java.util.Date dateCreateTimeEnd = null;

        try {
            if (null != createTimeStart && !createTimeStart.isEmpty()) {
                dateCreateTimeStart = StringUtil.String2Date(createTimeStart);
            }
            if (null != createTimeEnd && !createTimeEnd.isEmpty()) {
                dateCreateTimeEnd = StringUtil.String2Date(createTimeEnd);
            }
        } catch (ParseException ex) {
            StringUtil.throw400Exp(response,"400002:date format error");
        }

        PageInfo<OrderType> pageInfo = orderTypeService.selectPage(pageIndex, pageSize,
                "id", "ASC", name, dateCreateTimeStart, dateCreateTimeEnd);

        List<OrderTypeBean> beans = new ArrayList<>();
        PageInfo<OrderTypeBean> result = new PageInfo<>(pageInfo.getTotal(),pageIndex, pageSize, beans);
        result.setPageIndex(pageIndex);
        result.setPageSize(pageSize);
        result.setTotal(pageInfo.getTotal());

        if ((pageIndex -1) * pageSize <= pageInfo.getTotal()) {
            for (OrderType f : pageInfo.getRows()) {
                OrderTypeBean b = new OrderTypeBean();

                BeanUtils.copyProperties(f, b);
                beans.add(b);
            }
        }
        result.setRows(beans);

        response.setStatus(MyErrorMap.e200.getCode());
        return result;
    }

}
