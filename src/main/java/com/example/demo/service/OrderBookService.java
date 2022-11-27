package com.example.demo.service;

import com.example.demo.model.OrderBook;
import com.example.demo.repository.OrderBookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderBookService {

    private final OrderBookRepository orderBookRepository;

    public OrderBookService(OrderBookRepository orderBookRepository) {
        this.orderBookRepository = orderBookRepository;
    }

    public List<OrderBook> getOrderBooks() {
        return orderBookRepository.findAll();
    }
}
