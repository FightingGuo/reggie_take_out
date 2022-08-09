package com.ghc.reggie.dto;

import com.ghc.reggie.bean.OrderDetail;
import com.ghc.reggie.bean.Orders;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import java.util.List;

@Data
@ApiModel("订单dto")
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
