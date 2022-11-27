package com.example.demo.controller;

import com.example.demo.model.MarketOrder;
import com.example.demo.service.MarketOrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "orders")
public class MarketOrderController {
    private final MarketOrderService marketOrderService;

    public MarketOrderController(MarketOrderService marketOrderService) {
        this.marketOrderService = marketOrderService;
    }

    @GetMapping
    public List<MarketOrder> getOrders(@RequestParam(required = false) String orderType) {
        return marketOrderService.getOrders(orderType);
    }
}
