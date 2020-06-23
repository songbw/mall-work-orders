package com.fengchao.workorders.controller;

import com.alibaba.fastjson.JSON;
import com.fengchao.workorders.bean.AddressBean;
import com.fengchao.workorders.constants.MyErrorEnum;
import com.fengchao.workorders.entity.DefaultAddress;
import com.fengchao.workorders.exception.MyException;
import com.fengchao.workorders.service.db.impl.DefaultAddressServiceImpl;
import com.fengchao.workorders.util.MyErrorMap;
import com.fengchao.workorders.util.ResultObject;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 退货默认地址管理
 * @author Clark
 * */
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
    public AddressBean getAddress(@ApiParam(value="id",required=true)@PathVariable("id") Long id) {
        DefaultAddress record = defaultAddressService.selectById(id);
        return AddressBean.convert(record);
    }

    @ApiOperation(value = "获取默认地址信息", notes = "默认地址信息如果有多个，以第一个为准")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("addresses/default")
    public AddressBean getDefaultAddress() {
        DefaultAddress record = defaultAddressService.selectDefault();
        return AddressBean.convert(record);
    }

    @ApiOperation(value = "获取地址信息列表", notes = "地址信息列表")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to find record") })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("addresses/list")
    public List<AddressBean> getAddressList() {
        List<DefaultAddress> list = defaultAddressService.selectAll();
        List<AddressBean> result = new ArrayList<>();
        list.forEach(address->result.add(AddressBean.convert(address)));
        return result;
    }

    @ApiOperation(value = "新建地址信息", notes = "新建地址信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to create record") })
    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("addresses")
    public ResultObject<DefaultAddress>
    createAddress(HttpServletResponse response,
                  @RequestBody AddressBean data) {

        log.info("新建地址信息 入参 {}", JSON.toJSONString(data));
        DefaultAddress record = DefaultAddress.convert(data);
        DefaultAddress newRecord = defaultAddressService.createRecord(record);
        response.setStatus(201);
        return new ResultObject<>(newRecord);
    }

    @ApiOperation(value = "更新地址信息", notes = "更新地址信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to update record") })
    @ResponseStatus(code = HttpStatus.OK)
    @PutMapping("addresses/{id}")
    public ResultObject<DefaultAddress> updateAddress(HttpServletResponse response,
                                @ApiParam(value="id",required=true)@PathVariable("id") Long id,
                                @RequestBody AddressBean data) {

        log.info("更新地址信息 入参 {}", JSON.toJSONString(data));

        DefaultAddress address = DefaultAddress.updateConvert(data);
        DefaultAddress updatedRecord = defaultAddressService.updateRecordById(address,id);

        response.setStatus(201);
        return new ResultObject<>(updatedRecord);
    }

    @ApiOperation(value = "删除地址信息", notes = "删除地址信息")
    @ApiResponses({ @ApiResponse(code = 400, message = "failed to delete record") })
    @ResponseStatus(code = HttpStatus.OK)
    @DeleteMapping("addresses/{id}")
    public ResultObject<String> deleteAddress(HttpServletResponse response,
                                  @ApiParam(value="id",required=true)@PathVariable("id") long id) {

        DefaultAddress record = defaultAddressService.selectById(id);
        log.info("删除记录 {}",JSON.toJSONString(record));
        try{
            defaultAddressService.removeById(id);
        }catch (Exception e){
            throw new MyException(MyErrorEnum.MYSQL_ERROR);
        }

        response.setStatus(MyErrorMap.e204.getCode());
        log.info("删除记录 成功");
        return new ResultObject<>();
    }

}
