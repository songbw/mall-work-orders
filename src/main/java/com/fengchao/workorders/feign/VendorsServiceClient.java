package com.fengchao.workorders.feign;

import com.fengchao.workorders.bean.OperaResponse;
import com.fengchao.workorders.feign.hystric.VendorsServiceClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "vendors", fallbackFactory = VendorsServiceClientFallbackFactory.class)
public interface VendorsServiceClient {


    @RequestMapping(value = "/renter/api/companies", method = RequestMethod.GET)
    OperaResponse<List<Long>> queryRenterMerchantList(@RequestParam("renterId") String renterId);

    @RequestMapping(value = "/renter/api/renterId", method = RequestMethod.GET)
    OperaResponse<String> queryRenterId(@RequestParam("appId") String appId );

    @RequestMapping(value = "/renter/api/appIdList", method = RequestMethod.GET)
    OperaResponse<List<String>> queryAppIdList(@RequestHeader("renterId") String renterId);

    @RequestMapping(value = "/renter/api/companiesByAppId/{appId}", method = RequestMethod.GET)
    OperaResponse<List<Long>> queryAppIdMerchantList(@PathVariable("appId") String appId);

    @RequestMapping(value = "/renter/api/appId/byMerchant", method = RequestMethod.GET)
    OperaResponse<List<String>> queryAppIdsByMerchantId(@RequestParam("merchantId") Long merchantId );

}
