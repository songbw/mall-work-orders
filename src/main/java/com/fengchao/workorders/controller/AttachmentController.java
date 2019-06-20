package com.fengchao.workorders.controller;

import com.fengchao.workorders.bean.*;
import com.fengchao.workorders.model.*;
//import com.fengchao.workorders.service.TokenAuthenticationService;
import com.fengchao.workorders.service.impl.*;
import com.fengchao.workorders.util.*;
import com.fengchao.workorders.util.PageInfo;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.BeanUtils;

import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Api(tags="AttachmentAPI", description = "附件管理相关", produces = "application/json;charset=UTF-8")
@RestController
@RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class AttachmentController {

    private static Logger logger = LoggerFactory.getLogger(AttachmentController.class);

    private AttachmentServiceImpl attachmentService;
    private WorkOrderServiceImpl workOrderService;

    @ApiModel(value = "附件信息ID")
    private class IdData implements Serializable {
        @ApiModelProperty(value = "ID", example = "1", required = true)
        public Long id;

    }

    @ApiModel(value = "附件信息List")
    private class ListData implements Serializable {

        public List<AttachmentBean> list;

    }

    @Autowired
    public AttachmentController(WorkOrderServiceImpl workOrderService,
                                AttachmentServiceImpl attachmentService
                             ) {
        this.attachmentService = attachmentService;
        this.workOrderService = workOrderService;
    }

    @ApiOperation(value = "获取附件信息列表", notes = "获取附件信息列表")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("attachments/list")
    public ListData getList(HttpServletResponse response,
                            @RequestHeader(value = "Authorization", defaultValue = "Bearer token") String authentication
                            ) {
        String username = JwtTokenUtil.getUsername(authentication);

        if (null == username) {
            logger.warn("can not find username in token");
        }

        List<Attachment> attachments = attachmentService.selectAll();
        List<AttachmentBean> list = new ArrayList<>();
        for (Attachment a : attachments) {
            AttachmentBean b = new AttachmentBean();
            BeanUtils.copyProperties(a, b);
            list.add(b);
        }

        ListData result = new ListData();
        result.list = list;
        response.setStatus(MyErrorMap.e200.getCode());
        return result;

    }

    @ApiOperation(value = "获取附件信息", notes = "附件信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("attachments/{id}")
    public AttachmentBean getProfile(HttpServletResponse response,
                                 @RequestHeader(value = "Authorization", defaultValue = "Bearer token") String authentication,
                                 @ApiParam(value="id",required=true)@PathVariable("id") Long id) {

        AttachmentBean bean = new AttachmentBean();
        //String username = JwtTokenUtil.getUsername(authentication);
        if (null == id || 0 == id) {
            StringUtil.throw400Exp(response, "400002:ID is wrong");
            return  bean;
        }
        String username = JwtTokenUtil.getUsername(authentication);

        if (null == username) {
            logger.warn("can not find username in token");
        }
        Attachment attachment = attachmentService.selectById(id);
        if (null == attachment) {
            StringUtil.throw400Exp(response, "400003:Failed to find attachment record");
            return bean;
        }

        BeanUtils.copyProperties(attachment, bean);

        response.setStatus(MyErrorMap.e200.getCode());
        return bean;

    }

    @ApiOperation(value = "条件查询附件信息", notes = "查询附件信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("attachments")
    public PageInfo<AttachmentBean> queryAttachments(HttpServletResponse response,
                                                     @RequestHeader(value = "Authorization", defaultValue = "Bearer token") String authentication,
                                                     @ApiParam(value="页码",required=true)@RequestParam Integer pageIndex,
                                                     @ApiParam(value="每页记录数",required=true)@RequestParam Integer pageSize,
                                                     @ApiParam(value="name")@RequestParam(required=false) String name,
                                                     @ApiParam(value="submitter")@RequestParam(required=false) String submitter,
                                                     @ApiParam(value="创建开始时间")@RequestParam(required=false) String createTimeStart,
                                                     @ApiParam(value="创建结束时间")@RequestParam(required=false) String createTimeEnd,
                                                     @ApiParam(value="workOrderId")@RequestParam(required=false) Long workOrderId) {

        java.util.Date dateCreateTimeStart = null;
        java.util.Date dateCreateTimeEnd = null;

        if (null == pageIndex || 0 >= pageIndex
                || null == pageSize || 0>= pageSize) {
            StringUtil.throw400Exp(response,"400002:pageIndex or pageSize is wrong");
        }

        try {
            if (null != createTimeStart && !createTimeStart.isEmpty()) {
                dateCreateTimeStart = StringUtil.String2Date(createTimeStart);
            }
            if (null != createTimeEnd && !createTimeEnd.isEmpty()) {
                dateCreateTimeEnd = StringUtil.String2Date(createTimeEnd);
            }
        } catch (ParseException ex) {
            StringUtil.throw400Exp(response,"400002:createTime format is wrong");
        }

        String username = JwtTokenUtil.getUsername(authentication);

        if (null == username) {
            logger.warn("can not find username in token");
        }

        PageInfo<Attachment> pages = attachmentService.selectAttachments(pageIndex,pageSize,
                "id", "DESC",workOrderId,name,submitter, dateCreateTimeStart, dateCreateTimeEnd);

        List<AttachmentBean> list = new ArrayList<>();

        if ((pageIndex -1) * pageSize <= pages.getTotal()) {
            for (Attachment a : pages.getRows()) {
                AttachmentBean b = new AttachmentBean();
                BeanUtils.copyProperties(a, b);
                list.add(b);
            }
        }
        PageInfo<AttachmentBean> result = new PageInfo<>(pages.getTotal(), pageSize,pageIndex, list);

        response.setStatus(MyErrorMap.e200.getCode());
        return result;

    }

    @ApiOperation(value = "创建附件信息", notes = "创建附件信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to creat record") })
    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("attachments")
    public IdData createProfile(HttpServletResponse response,
                                            @RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication,
                                            @RequestBody AttachmentProfileBean data) throws RuntimeException {

        System.out.println("create attachment enter");
        IdData result = new IdData();
        String username = JwtTokenUtil.getUsername(authentication);
        String path = data.getPath();
        String submitter = data.getSubmitter();
        String name = data.getName();
        Long workOrderId = data.getWorkOrderId();

        if (null == path || path.isEmpty() ||
            null ==name || name.isEmpty() ||
            null == workOrderId || 0 == workOrderId
            ) {
            StringUtil.throw400Exp(response, "400002:名称,路径,工单号,不能为空");
        }

        if (attachmentService.isExistName(name)) {
            StringUtil.throw400Exp(response, "400003:附件名已存在");
        }

        WorkOrder workOrder = workOrderService.selectById(workOrderId);
        if (null == workOrder) {
            StringUtil.throw400Exp(response, "400002:工单号不存在");
        }

        Attachment attachment = new Attachment();
        attachment.setName(name);
        attachment.setPath(path);
        attachment.setWorkOrderId(workOrderId);
        if (null != submitter && !submitter.isEmpty()) {
            attachment.setSubmitter(submitter);
        }

        attachment.setCreateTime(new Date());
        attachment.setUpdateTime(new Date());
        if (null != username && !username.isEmpty()) {
            attachment.setCreatedBy(username);
            attachment.setUpdatedBy(username);
        }

        result.id = attachmentService.insertRecord(attachment);

        response.setStatus(MyErrorMap.e201.getCode());
        logger.info("create Attachment : " + name);
        return result;
    }


    @ApiOperation(value = "更新附件信息", notes = "更新附件信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to update record") })
    @ResponseStatus(code = HttpStatus.CREATED)
    @PutMapping("attachments/{id}")
    public IdData updateProfile(HttpServletResponse response,
                                @RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication,
                                @ApiParam(value="id",required=true)@PathVariable("id") Long id,
                                @RequestBody AttachmentProfileBean data) throws RuntimeException {

        System.out.println("update attachment");
        IdData result = new IdData();
        String username = JwtTokenUtil.getUsername(authentication);
        String path = data.getPath();
        String submitter = data.getSubmitter();
        String name = data.getName();
        Long workOrderId = data.getWorkOrderId();

        if (null == id || 0 == id) {
            StringUtil.throw400Exp(response, "400002:ID is wrong");
            System.out.println("update attachment err1");
        }

        Attachment attachment = attachmentService.selectById(id);
        if (null == attachment) {
            StringUtil.throw400Exp(response, "400002:附件不存在");
            System.out.println("update attachment err2");
            return result;
        }

        if (null != name && attachmentService.isExistNameExcludeId(name, id)) {
            StringUtil.throw400Exp(response, "400001:附件名已存在");
            System.out.println("update attachment er3");
        }

        if (null != workOrderId) {
            WorkOrder workOrder = workOrderService.selectById(workOrderId);
            if (null == workOrder) {
                StringUtil.throw400Exp(response, "400002:工单号不存在");
            }
            attachment.setWorkOrderId(workOrderId);
        }

        if (null != name && !name.isEmpty()) {
            attachment.setName(name);
        }

        if (null != path && !path.isEmpty()) {
            attachment.setPath(path);
        }
        if (null != submitter && !submitter.isEmpty()) {
            attachment.setSubmitter(submitter);
        }

        attachment.setUpdateTime(new Date());

        if (null != username) {
            attachment.setUpdatedBy(username);
        }

        attachmentService.updateSelectById(attachment);

        result.id = id;
        response.setStatus(MyErrorMap.e201.getCode());
        logger.info("update Attachment profile");
        return result;
    }


    @ApiOperation(value = "删除附件信息", notes = "删除附件信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to delete Attachment's profile") })
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @DeleteMapping("attachments/{id}")
    public void deleteAttachment(HttpServletResponse response,
                                          @ApiParam(value="id",required=true)@PathVariable("id") Long id,
                                          @RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication
                                          ) throws RuntimeException {

        logger.info("delete attachment");

        String username = JwtTokenUtil.getUsername(authentication);

        if (null == username) {
            logger.warn("can not find username in token");
        }
        if (null == id || 0 == id) {
            StringUtil.throw400Exp(response, "400002: ID is wrong");
        }

        Attachment attachment = attachmentService.selectById(id);
        if (null == attachment) {
            StringUtil.throw400Exp(response, "400003: failed to find record");
        }

        attachmentService.deleteById(id);
        response.setStatus(MyErrorMap.e204.getCode());

        logger.info("delete Attachment profile");

    }

}
