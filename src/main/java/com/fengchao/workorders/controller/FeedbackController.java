package com.fengchao.workorders.controller;

import com.fengchao.workorders.bean.*;
import com.fengchao.workorders.model.WorkOrder;
import com.fengchao.workorders.service.impl.FeedbackServiceImpl;
import com.fengchao.workorders.model.Feedback;
import com.fengchao.workorders.service.impl.WorkOrderServiceImpl;
import com.fengchao.workorders.util.*;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
//import java.lang.NullPointerException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags="FeedbackAPI", description = "用户权限相关", produces = "application/json;charset=UTF-8")
@RestController
@RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class FeedbackController {
    private static Logger logger = LoggerFactory.getLogger(FeedbackController.class);

    private FeedbackServiceImpl feedbackService;
    private WorkOrderServiceImpl workOrderService;

    @ApiModel(value="反馈信息ID")
    private class FeedbackResponseData implements Serializable {
        @ApiModelProperty(value="ID", example="111",required=true)
        public Long id;
    }

    @Autowired
    public FeedbackController(WorkOrderServiceImpl workOrderService,
                              FeedbackServiceImpl feedbackService
                             ) {
        this.feedbackService = feedbackService;
        this.workOrderService = workOrderService;
    }

    @ApiOperation(value = "增加反馈信息", notes="Header中必须包含Token")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to create Feedback") })
    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("feedback")
    //@PreAuthorize("hasFeedback('Feedback','insert')")
    public FeedbackResponseData insert(HttpServletResponse response,
                            @RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication,
                            @RequestBody FeedbackBodyBean data ) {

        FeedbackResponseData result = new FeedbackResponseData();
        String customer = data.getCustomer();
        String feedbackText = data.getFeedbackText();
        Long workOrderId = data.getWorkOrderId();
        String title = data.getTitle();
        String feedbackTimeStr = data.getFeedbackTime();

        if (null == customer || customer.isEmpty()
                || null == feedbackText || feedbackText.isEmpty()
                || null == workOrderId || 0 == workOrderId) {
                StringUtil.throw400Exp(response,"400002: 反馈内容,反馈人, 工单不能为空");
                return result;
        }

        WorkOrder workOrder = workOrderService.selectById(workOrderId);
        if (null == workOrder) {
            StringUtil.throw400Exp(response,"400002: 工单号不存在");
            return result;
        }

        Feedback feedback = new Feedback();
        try {
            java.util.Date feedbackTime = StringUtil.String2Date(feedbackTimeStr);
            feedback.setFeedbackTime(feedbackTime);
        } catch (ParseException e) {
            StringUtil.throw400Exp(response,"400002:反馈时间格式必须为:yyyy-MM-dd HH:mm:ss");
            return result;
        }
        String username = JwtTokenUtil.getUsername(authentication);

        if (null != username) {
            feedback.setUpdatedBy(username);
            feedback.setUpdatedBy(username);
        }
        feedback.setWorkOrderId(workOrderId);

        feedback.setCustomer(customer);
        feedback.setFeedbackText(feedbackText);

        if (null != title && !title.isEmpty()) {
            feedback.setTitle(title);
        }
        feedback.setCreateTime(new Date());
        feedback.setUpdateTime(new Date());

        Long id = feedbackService.insert(feedback);
        if (0 == id) {
            StringUtil.throw400Exp(response,"400002: Failed to add Feedback");
        }

        result.id = id;
        response.setStatus(MyErrorMap.e201.getCode());
        return result;
    }

    @ApiOperation(value = "删除反馈", notes="Header中必须包含Token")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to delete Feedback") })
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @DeleteMapping("feedback/{id}")
    //@PreAuthorize("hasFeedback('Feedback','delete')")
    public void deleteById(HttpServletResponse response,
                            @RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication,
                            @ApiParam(value="id",required=true)@PathVariable("id") Long id ) {

        String username = JwtTokenUtil.getUsername(authentication);

        if (null == username) {
            logger.warn("can not find username in token");
        }

        if (null == id || 0 >= id ) {
            StringUtil.throw400Exp(response,"400002:can not find Feedback");
            return;
        }

        Feedback feedback = feedbackService.selectById(id);
        if ( null == feedback ) {
            StringUtil.throw400Exp(response,"400002:反馈不存在");
        } else {
            feedbackService.deleteById(id);
            response.setStatus(MyErrorMap.e204.getCode());
        }

    }

    @ApiOperation(value = "更新反馈信息", notes="Header中必须包含Token")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to update Feedback") })
    @ResponseStatus(code = HttpStatus.CREATED)
    @PutMapping("/feedback/{id}")
    //@PreAuthorize("hasFeedback('Feedback','update')")
    public FeedbackResponseData update(HttpServletResponse response,@RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication,
                            @ApiParam(value="id",required=true)@PathVariable("id") Long id,
                            @RequestBody FeedbackBodyBean data ) {

        FeedbackResponseData result = new FeedbackResponseData();
        Feedback feedback;

        String customer = data.getCustomer();
        String feedbackText = data.getFeedbackText();
        Long workOrderId = data.getWorkOrderId();
        String title = data.getTitle();
        String feedbackTimeStr = data.getFeedbackTime();

        if (null == id) {
            StringUtil.throw400Exp(response,"400002:can not find Feedback");
            return result;
        }

        feedback = feedbackService.selectById(id);
        if ( null == feedback ) {
            StringUtil.throw400Exp(response,"400002:反馈不存在");
            return result;
        }

        if (null != workOrderId && 0 != workOrderId) {
            WorkOrder workOrder = workOrderService.selectById(workOrderId);
            if (null == workOrder) {
                StringUtil.throw400Exp(response, "400002: 工单号不存在");
            } else {
                feedback.setWorkOrderId(workOrderId);
            }
        }

        if (null != feedbackTimeStr && !feedbackTimeStr.isEmpty()) {
            try {
                Date feedbackTime = StringUtil.String2Date(feedbackTimeStr);
                feedback.setFeedbackTime(feedbackTime);
            } catch (ParseException e) {
                StringUtil.throw400Exp(response, "400002:反馈时间格式必须为:yyyy-MM-dd HH:mm:ss");
                return result;
            }
        }
        if (null != title && !title.isEmpty()) {
            feedback.setTitle(title);
        }
        if (null != customer && !customer.isEmpty()) {
            feedback.setCustomer(customer);
        }
        if (null != feedbackText && !feedbackText.isEmpty()){
            feedback.setFeedbackText(feedbackText);
        }

        feedback.setUpdateTime(new Date());
        String username = JwtTokenUtil.getUsername(authentication);
        feedback.setUpdatedBy(username);

        feedbackService.update(feedback);
        response.setStatus(MyErrorMap.e201.getCode());
        result.id = id;
        return result;
    }

    @ApiOperation(value = "查询反馈信息列表")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("feedback/pages")
    //@PreAuthorize("hasFeedback('user','list')")
    public PageInfo<FeedbackBean> pages(HttpServletResponse response,
                                      @RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication,
                                      @ApiParam(value="获取页的序号",required=true)@RequestParam Integer pageIndex,
                                      @ApiParam(value="每页返回的最大数",required=true)@RequestParam Integer pageSize,
                                      @ApiParam(value="工单号")@RequestParam(required=false) Long workOrderId,
                                      @ApiParam(value="反馈者")@RequestParam(required=false) String customer,
                                      @ApiParam(value="标题")@RequestParam(required=false) String title,
                                      @ApiParam(value="反馈内容")@RequestParam(required=false) String feedbackText,
                                      @ApiParam(value="反馈提交开始时间")@RequestParam(required=false) String createTimeStart,
                                      @ApiParam(value="反馈提交结束时间")@RequestParam(required=false) String createTimeEnd
                                      ) {


        String username = JwtTokenUtil.getUsername(authentication);

        if (null == username) {
            logger.warn("can not find username in token");
        }

        if (null == pageIndex || 0 >= pageIndex
                || null == pageSize || 0>= pageSize) {
            StringUtil.throw400Exp(response,"400002:pageIndex or pageSize is wrong");
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
            StringUtil.throw400Exp(response,"400002:date format is wrong");
        }

        PageInfo<Feedback> pageInfo = feedbackService.selectPage(pageIndex, pageSize,
                "id", "ASC", workOrderId,customer, title,feedbackText, dateCreateTimeStart, dateCreateTimeEnd);

        List<FeedbackBean> beans = new ArrayList<>();
        PageInfo<FeedbackBean> result = new PageInfo<>(pageInfo.getTotal(),pageIndex, pageSize, beans);
        result.setPageIndex(pageIndex);
        result.setPageSize(pageSize);
        result.setTotal(pageInfo.getTotal());

        if ((pageIndex -1) * pageSize <= pageInfo.getTotal()) {
            for (Feedback f : pageInfo.getRows()) {
                FeedbackBean b = new FeedbackBean();

                BeanUtils.copyProperties(f, b);
                beans.add(b);
            }
        }
        result.setRows(beans);

        response.setStatus(MyErrorMap.e200.getCode());
        return result;
    }

}
