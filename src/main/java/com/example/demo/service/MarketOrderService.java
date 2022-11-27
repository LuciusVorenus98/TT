package com.example.demo.service;

import com.example.demo.model.MarketOrder;
import com.example.demo.model.OrderType;
import com.example.demo.repository.MarketOrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarketOrderService {

    private final MarketOrderRepository marketOrderRepository;

    public MarketOrderService(MarketOrderRepository marketOrderRepository) {
        this.marketOrderRepository = marketOrderRepository;
    }

    public List<MarketOrder> getOrders(String orderType) {
        List<MarketOrder> marketOrders;
        if (orderType != null) {
            OrderType ot = OrderType.valueOf(orderType);
            marketOrders = marketOrderRepository.findByOrderTypeOrderByPriceAsc(ot);
        } else {
            marketOrders = marketOrderRepository.findAll();
        }
        return marketOrders;
    }
}
