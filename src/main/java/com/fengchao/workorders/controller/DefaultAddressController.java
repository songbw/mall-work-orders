package com.fengchao.workorders.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fengchao.workorders.bean.AddressBean;
import com.fengchao.workorders.model.DefaultAddress;
import com.fengchao.workorders.service.impl.DefaultAddressServiceImpl;
import com.fengchao.workorders.util.MyErrorMap;
import com.fengchao.workorders.util.StringUtil;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Slf4j
@Api(tags="DefaultAddressAPI", description = "缺省地址管理相关", produces = "application/json;charset=UTF-8")
@RestController
@RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class DefaultAddressController {

    private DefaultAddressServiceImpl defaultAddressService;

    @Autowired
    public DefaultAddressController(DefaultAddressServiceImpl defaultAddressService){
        this.defaultAddressService = defaultAddressService;
    }

    @ApiOperation(value = "获取地址信息", notes = "地址信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("addresses/{id}")
    public AddressBean getAddress(HttpServletResponse response,
                                  @ApiParam(value="id",required=true)@PathVariable("id") Long id) {

        if (null == id || 0 == id) {
            StringUtil.throw400Exp(response, "400002:ID is wrong");
            return  null;
        }

        DefaultAddress record;
        try{
            record = defaultAddressService.selectById(id);
        }catch (Exception e){
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
            return  null;
        }

        if (null == record) {
            StringUtil.throw400Exp(response, "400003:地址记录不存在");
            return  null;
        }
        AddressBean bean = new AddressBean();
        BeanUtils.copyProperties(record, bean);
        if (1 == record.getAsDefault()){
            bean.setIsDefault(true);
        }else {
            bean.setIsDefault(false);
        }

        response.setStatus(200);
        return bean;
    }

    @ApiOperation(value = "获取Default地址信息", notes = "Default地址信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("addresses/default")
    public AddressBean getDefaultAddress(HttpServletResponse response) {

        DefaultAddress record;
        try{
            record = defaultAddressService.selectDefault();
        }catch (Exception e){
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
            return  null;
        }

        AddressBean bean = new AddressBean();
        BeanUtils.copyProperties(record, bean);
        if (1 == record.getAsDefault()){
            bean.setIsDefault(true);
        }else {
            bean.setIsDefault(false);
        }

        response.setStatus(200);
        return bean;
    }

    @ApiOperation(value = "获取地址信息列表", notes = "地址信息列表")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("addresses/list")
    public List<AddressBean> getAddressList(HttpServletResponse response) {

        List<DefaultAddress> list;
        try{
            list = defaultAddressService.selectAll();
        }catch (Exception e){
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
            return  null;
        }

        List<AddressBean> result = new ArrayList<>();

        if (null != list && 0 < list.size()) {
            for (DefaultAddress a : list) {
                AddressBean bean = new AddressBean();
                BeanUtils.copyProperties(a, bean);
                if (1 == a.getAsDefault()) {
                    bean.setIsDefault(true);
                } else {
                    bean.setIsDefault(false);
                }
                result.add(bean);
            }
        }
        response.setStatus(200);
        return result;
    }

    @ApiOperation(value = "新建地址信息", notes = "新建地址信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to create record") })
    @ResponseStatus(code = HttpStatus.OK)
    @PostMapping("addresses")
    public String createAddress(HttpServletResponse response,
                                  @RequestBody AddressBean data) {

        log.info("createAddress param {}", JSON.toJSONString(data));
        String address = data.getContent();
        Boolean asDefault = data.getIsDefault();

        if (null == address) {
            StringUtil.throw400Exp(response, "400002:content is missing");
            return  null;
        }

        if (null != asDefault && asDefault){
            DefaultAddress defaultRecord=null;
            try {
                defaultRecord = defaultAddressService.selectDefault();
            }catch (Exception e){
                log.info("default address record do not exists");
            }
            if (null != defaultRecord){
                StringUtil.throw400Exp(response, "400004:默认地址已经存在");
                return  null;
            }
        }

        DefaultAddress record = new DefaultAddress();
        record.setContent(address);
        if (null == asDefault){
            record.setAsDefault(0);
        } else {
            record.setAsDefault((asDefault) ? 1 : 0);
        }
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        Long newId;
        try{
            newId = defaultAddressService.insert(record);
        }catch (Exception e){
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
            return  null;
        }


        response.setStatus(201);
        Map< String , Object > jsonMap = new HashMap<>();
        jsonMap.put("id",newId);
        jsonMap.put("message","success");
        log.info("createAddress id={}", newId.toString());
        return JSONObject.toJSONString(jsonMap, SerializerFeature.WriteMapNullValue);
    }

    @ApiOperation(value = "更新地址信息", notes = "更新地址信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to update record") })
    @ResponseStatus(code = HttpStatus.OK)
    @PutMapping("addresses/{id}")
    public String updateAddress(HttpServletResponse response,
                                @ApiParam(value="id",required=true)@PathVariable("id") Long id,
                                @RequestBody AddressBean data) {

        log.info("updateAddress param {}", JSON.toJSONString(data));
        if (null == id || 0 == id) {
            StringUtil.throw400Exp(response, "400002:ID is wrong");
            return  null;
        }
        String address = data.getContent();
        Boolean asDefault = data.getIsDefault();

        DefaultAddress record;
        try {
            record = defaultAddressService.selectById(id);
        }catch (Exception e){
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
            return  null;
        }
        if (null == record){
            StringUtil.throw400Exp(response, "400003:记录不存在");
            return  null;
        }

        if (null != address) {
            record.setContent(address);
        }
        if (null != asDefault) {
            if (asDefault){
                DefaultAddress defaultRecord=null;
                try {
                    defaultRecord = defaultAddressService.selectDefault();
                }catch (Exception e){
                    log.info("failed to find default address record");
                }
                if (null != defaultRecord && !defaultRecord.getId().equals(record.getId())){
                    StringUtil.throw400Exp(response, "400004:默认地址已经存在");
                    return  null;
                }
            }

            record.setAsDefault((asDefault) ? 1 : 0);
        }

        record.setUpdateTime(new Date());
        try{
            defaultAddressService.update(record);
        }catch (Exception e){
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
            return  null;
        }


        response.setStatus(201);
        response.setContentType("application/json;charset=UTF-8");
        Map< String , Object > jsonMap = new HashMap<>();
        jsonMap.put("id",record.getId());
        jsonMap.put("message","success");
        log.info("updateAddress success");
        return JSONObject.toJSONString(jsonMap, SerializerFeature.WriteMapNullValue);
    }

    @ApiOperation(value = "删除地址信息", notes = "删除地址信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to delete record") })
    @ResponseStatus(code = HttpStatus.OK)
    @DeleteMapping("addresses/{id}")
    public String deleteAddress(HttpServletResponse response,
                                  @ApiParam(value="id",required=true)@PathVariable("id") long id) {

        if (0 == id) {
            StringUtil.throw400Exp(response, "400002:ID is wrong");
        }

        DefaultAddress record = null;
        try{
            record = defaultAddressService.selectById(id);
        }catch (Exception e){
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
        }

        if (null == record) {
            StringUtil.throw400Exp(response, "400003:地址记录不存在");
        }

        try{
            defaultAddressService.deleteById(id);
        }catch (Exception e){
            StringUtil.throw400Exp(response, "400006:"+e.getMessage());
        }

        response.setStatus(MyErrorMap.e204.getCode());
        response.setContentType("application/json;charset=UTF-8");
        Map< String , Object > jsonMap = new HashMap<>();
        jsonMap.put("message","success");
        log.info("delete address success");
        return JSONObject.toJSONString(jsonMap, SerializerFeature.WriteMapNullValue);
    }

}
