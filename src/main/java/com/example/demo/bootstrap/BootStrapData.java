package com.example.demo.bootstrap;

import com.example.demo.model.Instrument;
import com.example.demo.model.MarketOrder;
import com.example.demo.model.OrderBook;
import com.example.demo.model.OrderType;
import com.example.demo.repository.InstrumentRepository;
import com.example.demo.repository.OrderBookRepository;
import com.example.demo.repository.MarketOrderRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class BootStrapData implements CommandLineRunner {

    private final InstrumentRepository instrumentRepository;
    private final OrderBookRepository orderBookRepository;
    private final MarketOrderRepository marketOrderRepository;

    public BootStrapData(InstrumentRepository instrumentRepository, OrderBookRepository orderBookRepository, MarketOrderRepository marketOrderRepository) {
        this.instrumentRepository = instrumentRepository;
        this.orderBookRepository = orderBookRepository;
        this.marketOrderRepository = marketOrderRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Random rand = new Random();
        List<MarketOrder> orders = new LinkedList<>();
        for (int i = 0; i < 50; i++) {
            double r = rand.nextGaussian() * 10 + 500;
            int price = (int) r;
            int quantity = ThreadLocalRandom.current().nextInt(10, 101);
            MarketOrder order;
            if (price < 500) {
                order = new MarketOrder(OrderType.BUY, price, quantity, LocalDateTime.now());
                orders.add(order);
                marketOrderRepository.save(order);
            } else {
                order = new MarketOrder(OrderType.SELL, price, quantity, LocalDateTime.now());
                orders.add(order);
                marketOrderRepository.save(order);
            }
        }
        OrderBook nikeOB = new OrderBook(orders);
        Instrument nike = new Instrument("Nike", "NYSE", nikeOB);
        orderBookRepository.save(nikeOB);
        instrumentRepository.save(nike);

        System.out.println("Started in a bootstrap");
        System.out.println("Number of instruments: " + instrumentRepository.count());
        System.out.println("Number of order books: " + orderBookRepository.count());
        System.out.println("Number of market orders: " + marketOrderRepository.count());
    }
}
