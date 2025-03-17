package com.cs.dto;

import lombok.Data;

import java.util.List;

@Data
public class GetListParam {
    private List<Long> cartItemIds;

    private Long userId;
}
