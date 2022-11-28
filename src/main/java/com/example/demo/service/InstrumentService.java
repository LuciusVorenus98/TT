package com.example.demo.service;

import com.example.demo.model.DTO.BestBuySellPrice;
import com.example.demo.model.DTO.CreateOrderForm;
import com.example.demo.model.Instrument;
import com.example.demo.model.MarketOrder;
import com.example.demo.model.OrderBook;
import com.example.demo.model.OrderType;
import com.example.demo.repository.InstrumentRepository;
import com.example.demo.repository.MarketOrderRepository;
import com.example.demo.repository.OrderBookRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InstrumentService {

    private final InstrumentRepository instrumentRepository;
    private final MarketOrderRepository marketOrderRepository;
    private final OrderBookRepository orderBookRepository;
    private List<String> responseMessageLog = new ArrayList<>();

    public List<String> getResponseMessageLog() {
        return responseMessageLog;
    }

    public void setResponseMessageLog(List<String> responseMessageLog) {
        this.responseMessageLog = responseMessageLog;
    }

    public InstrumentService(InstrumentRepository instrumentRepository, MarketOrderRepository marketOrderRepository, OrderBookRepository orderBookRepository) {
        this.instrumentRepository = instrumentRepository;
        this.marketOrderRepository = marketOrderRepository;
        this.orderBookRepository = orderBookRepository;
    }

    public List<Instrument> getInstruments() {
        return instrumentRepository.findAll();
    }

    public List<BestBuySellPrice> getBestBuySellPrices() {
        List<BestBuySellPrice> bestBuySellPrices = new ArrayList<>();
        List<Instrument> instruments = instrumentRepository.findAll();

        for(Instrument instrument: instruments){
            try {
                int bestBuyPrice = marketOrderRepository.findBestBuyPrice(instrument.getId());
                int bestSellPrice = marketOrderRepository.findBestSellPrice(instrument.getId());
                bestBuySellPrices.add(new BestBuySellPrice(bestBuyPrice, bestSellPrice));
            }
            catch (Exception e){
                bestBuySellPrices.add(new BestBuySellPrice(-1, -1));
                e.printStackTrace();
            }
        }
        return bestBuySellPrices;
    }

    public Instrument getInstrumentByShortName(String shortName) {
        return instrumentRepository.findByShortName(shortName);
    }

    public List<MarketOrder> getBuyOrdersSorted(Long id) {
        return marketOrderRepository.findBuyOrdersSorted(id);
    }

    public List<MarketOrder> getSellOrdersSorted(Long id) {
        return marketOrderRepository.findSellOrdersSorted(id);
    }

    private void executePartialTrade(CreateOrderForm userOrder, MarketOrder oppositeOrder, String activity) {
        userOrder.setQuantity(userOrder.getQuantity() - oppositeOrder.getQuantity());
        //TODO dodaj u tabelu TransactionHistory
        marketOrderRepository.deleteById(oppositeOrder.getId());
        responseMessageLog.add(activity + oppositeOrder.getQuantity() + " quantity at the price of " + oppositeOrder.getPrice());
    }

    private void executeRemainingTrade(CreateOrderForm userOrder, MarketOrder oppositeOrder, String activity) {
        int userQuantity = userOrder.getQuantity();
        oppositeOrder.setQuantity(oppositeOrder.getQuantity() - userQuantity);
        userOrder.setQuantity(0);
        marketOrderRepository.save(oppositeOrder);
        responseMessageLog.add(activity + userQuantity + " quantity at the price of " + oppositeOrder.getPrice());
    }

    private void executeTransaction(CreateOrderForm userOrder, MarketOrder oppositeOrder, OrderType orderType) {
        String activity = orderType == OrderType.BUY ? "Bought " : "Sold ";
        if (userOrder.getQuantity() >= oppositeOrder.getQuantity()) {
            executePartialTrade(userOrder, oppositeOrder, activity);
        } else {
            executeRemainingTrade(userOrder, oppositeOrder, activity);
        }
    }

    private void storeOrder(CreateOrderForm createOrderForm) {
        OrderType orderType;
        if (createOrderForm.getOrderType().equals("BUY"))
            orderType = OrderType.BUY;
        else
            orderType = OrderType.SELL;
        MarketOrder marketOrder = new MarketOrder(orderType,
                createOrderForm.getPrice(),
                createOrderForm.getQuantity(),
                LocalDateTime.now());
        marketOrderRepository.save(marketOrder);
        String shortName = createOrderForm.getShortName();
        Instrument instrument = instrumentRepository.findByShortName(shortName);
        Optional<OrderBook> orderBook = orderBookRepository.findById(instrument.getId());
        OrderBook currOrderBook = orderBook.get();
        currOrderBook.getMarketOrders().add(marketOrder);
        orderBookRepository.save(currOrderBook);
        responseMessageLog.add("Stored " + createOrderForm.getQuantity() + " quantity at the price of " + createOrderForm.getPrice());
    }

    private boolean canProcessLimitOrder(CreateOrderForm createOrderForm, List<MarketOrder> marketOrders, OrderType orderType, int i) {
        if(orderType == OrderType.BUY) {
            if (createOrderForm.getQuantity() > 0
                    && createOrderForm.getPrice() >= marketOrders.get(i).getPrice()
                    && i < marketOrders.size())
                return true;
            else
                return false;
        }
        else{
            if (createOrderForm.getQuantity() > 0
                    && createOrderForm.getPrice() <= marketOrders.get(i).getPrice()
                    && i < marketOrders.size())
                return true;
            else
                return false;
        }
    }

    private boolean canProcessMarketOrder(CreateOrderForm createOrderForm, List<MarketOrder> marketOrders, int i) {
        if (createOrderForm.getQuantity() > 0 && i < marketOrders.size())
            return true;
        else
            return false;
    }

    void processLimitOrder(CreateOrderForm createOrderForm, OrderType orderType, Long id) {
        List<MarketOrder> marketOrders = orderType == OrderType.BUY ? this.getSellOrdersSorted(id) : this.getBuyOrdersSorted(id);
        int i = 0;
        while (canProcessLimitOrder(createOrderForm, marketOrders, orderType, i)) {
            executeTransaction(createOrderForm, marketOrders.get(i), orderType);
            i++;
        }
        if (createOrderForm.getQuantity() > 0) {
            storeOrder(createOrderForm);
        }
    }

    void processMarketOrder(CreateOrderForm createOrderForm, OrderType orderType, Long id) {
        List<MarketOrder> marketOrders = orderType == OrderType.BUY ? this.getSellOrdersSorted(id) : this.getBuyOrdersSorted(id);
        int i = 0;
        while (canProcessMarketOrder(createOrderForm, marketOrders, i)) {
            executeTransaction(createOrderForm, marketOrders.get(i), orderType);
            i++;
        }
    }

    public void processOrder(CreateOrderForm createOrderForm, String shortName) throws Exception {
        Instrument instrument = getInstrumentByShortName(shortName);
        responseMessageLog.clear();
        OrderType orderType = createOrderForm.getOrderType().equals("BUY") ? OrderType.BUY : OrderType.SELL;

        if (createOrderForm.getPriceType().equals("LIMIT")) {
            processLimitOrder(createOrderForm, orderType, instrument.getId());
        } else if (createOrderForm.getPriceType().equals("MARKET")) {
            processMarketOrder(createOrderForm, orderType, instrument.getId());
        } else {
            throw new Exception("Illegal state for price type!");
        }
    }
}
