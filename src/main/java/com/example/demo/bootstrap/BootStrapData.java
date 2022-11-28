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

    private void createInstrumentNames(List<String> instrumentFullNames, List<String> instrumentShortNames) {
        instrumentFullNames.add("Nike");
        instrumentShortNames.add("NKE");
        instrumentFullNames.add("Apple");
        instrumentShortNames.add("AAPL");
        instrumentFullNames.add("Microsoft");
        instrumentShortNames.add("MSFT");
        instrumentFullNames.add("Gold");
        instrumentShortNames.add("GOLD");
        instrumentFullNames.add("Netflix");
        instrumentShortNames.add("NFLX");
    }

    @Override
    public void run(String... args) throws Exception {
        //initializing database
        List<String> instrumentFullNames = new ArrayList<>();
        List<String> instrumentShortNames = new ArrayList<>();
        createInstrumentNames(instrumentFullNames, instrumentShortNames);
        int numOfOrdersPerInstrument = 50;
        for (int j = 0; j < instrumentFullNames.size(); j++){
            List<MarketOrder> orders = new LinkedList<>();
            for (int i = 0; i < numOfOrdersPerInstrument; i++) {
                double r = ThreadLocalRandom.current().nextGaussian() * 10 + 500;
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
            OrderBook orderBook = new OrderBook(orders);
            Instrument instrument = new Instrument(instrumentFullNames.get(j), instrumentShortNames.get(j), orderBook);
            orderBookRepository.save(orderBook);
            instrumentRepository.save(instrument);
        }
    }
}
