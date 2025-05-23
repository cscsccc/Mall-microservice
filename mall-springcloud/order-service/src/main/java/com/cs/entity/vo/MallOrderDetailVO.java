package com.cs.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 订单详情页页面VO
 */
@Data
public class MallOrderDetailVO implements Serializable {

    private String orderNo;

    private Integer totalPrice;

    private Byte payStatus;

    private Byte payType;

    private String payTypeString;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date payTime;

    private Byte orderStatus;

    private String orderStatusString;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private List<MallOrderItemVO> mallOrderItemVOS;
}
