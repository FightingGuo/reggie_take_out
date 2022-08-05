package com.ghc.reggie.dto;

import com.ghc.reggie.bean.OrderDetail;
import com.ghc.reggie.bean.Orders;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
