package com.cs.service;

import com.cs.domain.vo.MallUserAddressVO;
import com.cs.pojo.MallUserAddress;

import java.util.List;

public interface MallManageUserAddressService {
    MallUserAddress selectDefaultAddressByPrimaryKey(Long userId);

    boolean saveAddress(MallUserAddress mallUserAddress);

    List<MallUserAddressVO> getMyAddresslist(Long userId);

    com.cs.pojo.MallUserAddress getDetailByPriamryKey(Long addressId);
}
