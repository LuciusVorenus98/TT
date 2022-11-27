package com.example.demo.controller;

import com.example.demo.model.OrderBook;
import com.example.demo.service.OrderBookService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "order-books")
public class OrderBookController {
    private final OrderBookService orderBookService;

    public OrderBookController(OrderBookService orderBookService) {
        this.orderBookService = orderBookService;
    }

    @GetMapping
    public List<OrderBook> getOrderBooks(){
        return orderBookService.getOrderBooks();
    }
}
