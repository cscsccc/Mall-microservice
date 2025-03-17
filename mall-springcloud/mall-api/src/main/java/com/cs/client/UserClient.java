package com.cs.client;

import com.cs.pojo.AdminUserToken;
import com.cs.pojo.MallUserAddress;
import com.cs.pojo.MallUserToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "user-service")
public interface UserClient {

    @GetMapping("/api/selectDefault")
    MallUserAddress selectDefaultAddressByPrimaryKey(@RequestParam("userId") Long userId);

    @GetMapping("/api/getAddressById")
    MallUserAddress selectAddressById(@RequestParam("addressId") Long addressId);

    @GetMapping("/api/user/getToken")
    MallUserToken getMallUserToken(@RequestParam("token") String token);

    @GetMapping("/adminUser/getAdminUserToken")
    AdminUserToken getAdminUserToken(@RequestParam("token") String token);
}
