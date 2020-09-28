package com.fengchao.workorders.rpc;

import com.alibaba.fastjson.JSON;
import com.fengchao.workorders.bean.OperaResponse;
import com.fengchao.workorders.feign.VendorsServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author tom
 * @Date 19-7-27 上午10:34
 */
@Service
@Slf4j
public class VendorsRpcService {

    private VendorsServiceClient vendorsServiceClient;

    @Autowired
    public VendorsRpcService(VendorsServiceClient vendorsServiceClient) {
        this.vendorsServiceClient = vendorsServiceClient;
    }



    public List<Long> queryRenterMerhantList(String renterId){
        List<Long> renterCompanyList = new ArrayList<>();

        OperaResponse<List<Long>> response = vendorsServiceClient.queryRenterMerchantList(renterId) ;

        log.debug("vendor 服务 queryRenterMerhantList 入参renterId： {},  返回值：{}",renterId, JSON.toJSONString(response));
        if (response.getCode() == 200) {
            renterCompanyList = response.getData() ;
        } else {
            log.warn("查询所有的商户信息 调用vendors rpc服务 错误!");
        }
        return renterCompanyList;

    }

    public List<String> queryAppIdList(String renterId){
        List<String> renterCompanyList = new ArrayList<>();

        OperaResponse<List<String>> response = vendorsServiceClient.queryAppIdList(renterId) ;

        log.debug("vendor 服务 queryAppIdList 入参renterId： {},  返回值：{}",renterId, JSON.toJSONString(response));
        if (response.getCode() == 200) {
            renterCompanyList = response.getData() ;
        } else {
            log.warn("查询所有的商户信息 调用vendors rpc服务 错误!");
        }
        return renterCompanyList;

    }

    public String queryRenterId(String appId){
        String renterId = "" ;
        OperaResponse<String> response = vendorsServiceClient.queryRenterId(appId) ;

        log.debug("vendor 服务 queryRenterId 入参appId ： {},  返回值：{}",appId, JSON.toJSONString(response));
        if (response.getCode() == 200) {
            renterId = response.getData() ;
        } else {
            log.warn("查询所有的商户信息 调用vendors rpc服务 错误!");
        }
        return renterId;

    }

    public List<Long> queryMerhantListByAppId(String appId){
        List<Long> renterCompanyList = new ArrayList<>();

        OperaResponse<List<Long>> response = vendorsServiceClient.queryAppIdMerchantList(appId) ;

        log.debug("vendor 服务 queryMerhantListByAppId 入参appId： {},  返回值：{}",appId, JSON.toJSONString(response));
        if (response.getCode() == 200) {
            renterCompanyList = response.getData() ;
        } else {
            log.warn("查询所有的商户信息 调用vendors rpc服务 错误!");
        }
        return renterCompanyList;

    }
}
