package com.example.demo.service;

import com.example.demo.model.DTO.BestBuySellPrice;
import com.example.demo.model.Instrument;
import com.example.demo.model.MarketOrder;
import com.example.demo.model.OrderType;
import com.example.demo.repository.InstrumentRepository;
import com.example.demo.repository.MarketOrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstrumentService {

    private final InstrumentRepository instrumentRepository;
    private final MarketOrderRepository marketOrderRepository;

    public InstrumentService(InstrumentRepository instrumentRepository, MarketOrderRepository marketOrderRepository) {
        this.instrumentRepository = instrumentRepository;
        this.marketOrderRepository = marketOrderRepository;
    }

    public List<Instrument> getInstruments() {
        return instrumentRepository.findAll();
    }

    public BestBuySellPrice getBestBuySellPrice() {
        int bestBuyPrice = marketOrderRepository.findBestBuyPrice();
        int bestSellPrice = marketOrderRepository.findBestSellPrice();
        return new BestBuySellPrice(bestBuyPrice, bestSellPrice);
    }

    public Instrument getInstrumentByShortName(String shortName) {
        return instrumentRepository.findByShortName(shortName);
    }

    public List<MarketOrder> getBuyOrdersSorted() {
        return marketOrderRepository.findBuyOrdersSorted();
    }

    public List<MarketOrder> getSellOrdersSorted() {
        return marketOrderRepository.findSellOrdersSorted();
    }
}
