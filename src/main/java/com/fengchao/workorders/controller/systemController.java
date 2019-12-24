package com.fengchao.workorders.controller;

import com.fengchao.workorders.bean.LogListBean;
import com.fengchao.workorders.service.impl.SystemServiceImpl;
import com.fengchao.workorders.model.SysLog;
import com.fengchao.workorders.util.*;
import io.swagger.annotations.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
//import java.util.Date;

@Slf4j
@Api(tags="LogAPI", description = "日志相关", produces = "application/json;charset=UTF-8")
@RestController
@RequestMapping(value = "/sys", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class systemController {
    //private static Logger log = LoggerFactory.getLogger(systemController.class);

    private SystemServiceImpl systemService;

    @Autowired
    public systemController(SystemServiceImpl systemService) {
        this.systemService = systemService;
    }

    @ApiOperation(value = "日志列表", notes="Header中必须包含Token")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("/logs")
    //@PreAuthorize("hasPermission('log','list')")
    public PageInfo<SysLog> searchLog(HttpServletResponse response,@ApiParam(value="data:pageIndex, pageSize",required=true)
                                                        @RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication,
                                                        @ApiParam(value="页码",required=false)@RequestParam(required=false) Integer pageIndex,
                                                        @ApiParam(value="每页记录数",required=false)@RequestParam(required=false) Integer pageSize,
                                                        @ApiParam(value="方法")@RequestParam(required=false) String method,
                                                        @ApiParam(value="访问链接")@RequestParam(required=false) String url,
                                                        @ApiParam(value="用户名")@RequestParam(required=false) String username,
                                                        @ApiParam(value="访问参数")@RequestParam(required=false) String param,
                                                        @ApiParam(value="创建开始时间")@RequestParam(required=false) String createTimeStart,
                                                        @ApiParam(value="创建结束时间")@RequestParam(required=false) String createTimeEnd) {

        String usernameInToken = JwtTokenUtil.getUsername(authentication);

        if (null == usernameInToken) {
            log.warn("can not find username in token");
        }
        java.util.Date dateCreateTimeStart = null;
        java.util.Date dateCreateTimeEnd = null;

        // long currentUserId = sysUserService.getUserIdInToken(authentication);

        if (null == pageIndex || 0 >= pageIndex) {
            pageIndex = 1;
        }
        if (null == pageSize || 0>= pageSize) {
            pageSize = 10;
        }

        try {
            if (null != createTimeStart && !createTimeStart.isEmpty()) {
                dateCreateTimeStart = StringUtil.String2Date(createTimeStart);
            }
            if (null != createTimeEnd && !createTimeEnd.isEmpty()) {
                dateCreateTimeEnd = StringUtil.String2Date(createTimeEnd);
            }
        } catch (Exception ex) {
            StringUtil.throw400Exp(response,"400002:can not find log");
        }

        //System.out.println("search Log: time_start: " + createTimeStart + "   time_end:  " + createTimeEnd);
        PageInfo<SysLog> pageInfo = systemService.selectLog(pageIndex, pageSize,
                "id", "DESC", method, url, param, dateCreateTimeStart, dateCreateTimeEnd, username);

        if ((pageIndex -1) * pageSize > pageInfo.getTotal()) {
            return new PageInfo<>(pageInfo.getTotal(),pageSize,pageIndex,null);
        }

        return pageInfo;
    }

    @ApiOperation(value = "删除日志", notes="需要管理员权限")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to delete record") })
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @DeleteMapping("logs/{id}")
    //@PreAuthorize("hasPermission('data','delete')")
    public void deleteById(HttpServletResponse response,
                                @RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication,
                                @ApiParam(value="id",required=true)@PathVariable("id") Long id
                                ) {

        String username = JwtTokenUtil.getUsername(authentication);

        if (null == username) {
            log.warn("can not find username in token");
        }
        if (null == id || 0 >= id ) {
            StringUtil.throw400Exp(response,"400002:can not find log");
        }

        SysLog sysLog = systemService.selectLogById(id);
        if (null == sysLog ) {
            StringUtil.throw400Exp(response,"400002:can not find log");
        }

        systemService.deleteLogById(id);


        response.setStatus(MyErrorMap.e204.getCode());

    }

    @ApiOperation(value = "删除日志组", notes="需要管理员权限")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to delete records") })
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @DeleteMapping("logs/list")
    //@PreAuthorize("hasPermission('data','delete')")
    public void deleteByIdList(HttpServletResponse response,
                                    @RequestHeader(value="Authorization",defaultValue="Bearer token") String authentication,
                                    @ApiParam(value="data:idList必须填写,其他空缺",required=true)@RequestBody LogListBean data) {

        String username = JwtTokenUtil.getUsername(authentication);

        if (null == username) {
            log.warn("can not find username in token");
        }
        if (null == data.getIdList() || 0 == data.getIdList().size()) {
            StringUtil.throw400Exp(response, "400002:can not find log");
        }

        //System.out.println("==== idList: " + data.getIdList());
        systemService.deleteLogByIdList(data.getIdList());

        response.setStatus(MyErrorMap.e204.getCode());

    }

}
