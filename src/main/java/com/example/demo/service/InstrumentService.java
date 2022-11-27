package com.example.demo.service;

import com.example.demo.model.DTO.BestBuySellPrice;
import com.example.demo.model.DTO.CreateOrderForm;
import com.example.demo.model.Instrument;
import com.example.demo.model.MarketOrder;
import com.example.demo.model.OrderType;
import com.example.demo.repository.InstrumentRepository;
import com.example.demo.repository.MarketOrderRepository;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.error.Mark;

import java.time.LocalDateTime;
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

    public void processOrder(CreateOrderForm createOrderForm) throws Exception {
        if(createOrderForm.getPriceType().equals("LIMIT")){
            if(createOrderForm.getOrderType().equals("BUY")){
                List<MarketOrder> marketOrders = this.getSellOrdersSorted();
                int i = 0;
                while(createOrderForm.getPrice() >= marketOrders.get(i).getPrice() && i<marketOrders.size()){
                    int matchedPrice = marketOrders.get(i).getPrice();
                    int buyerQuantity = createOrderForm.getQuantity();
                    int sellerQuantity = marketOrders.get(i).getQuantity();
                    if(buyerQuantity >= sellerQuantity){
                        createOrderForm.setQuantity(buyerQuantity-sellerQuantity);
                        //TODO dodaj u tabelu TransactionHistory
                        marketOrderRepository.deleteById(marketOrders.get(i).getId());
                    }
                    else{
                        marketOrders.get(i).setQuantity((sellerQuantity - buyerQuantity));
                        createOrderForm.setQuantity(0);
                        marketOrderRepository.save(marketOrders.get(i));
                        break;
                    }
                    i++;
                }
                if(createOrderForm.getQuantity() > 0){
                    marketOrderRepository.save(new MarketOrder(OrderType.BUY,
                            createOrderForm.getPrice(),
                            createOrderForm.getQuantity(),
                            LocalDateTime.now()));
                }
            }
            else if (createOrderForm.getOrderType().equals("SELL")) {
                List<MarketOrder> marketOrders = this.getBuyOrdersSorted();
                int i = 0;
                while(createOrderForm.getPrice() <= marketOrders.get(i).getPrice() && i<marketOrders.size()){
                    int matchedPrice = marketOrders.get(i).getPrice();
                    int sellerQuantity = createOrderForm.getQuantity();
                    int buyerQuantity = marketOrders.get(i).getQuantity();
                    if(sellerQuantity >= buyerQuantity){
                        createOrderForm.setQuantity(sellerQuantity-buyerQuantity);
                        //TODO dodaj u tabelu TransactionHistory
                        marketOrderRepository.deleteById(marketOrders.get(i).getId());
                    }
                    else{
                        marketOrders.get(i).setQuantity((buyerQuantity - sellerQuantity));
                        createOrderForm.setQuantity(0);
                        marketOrderRepository.save(marketOrders.get(i));
                        break;
                    }
                    i++;
                }
                if(createOrderForm.getQuantity() > 0){
                    marketOrderRepository.save(new MarketOrder(OrderType.SELL,
                            createOrderForm.getPrice(),
                            createOrderForm.getQuantity(),
                            LocalDateTime.now()));
                }
            }
        }
        else if (createOrderForm.getPriceType().equals("MARKET")) {
            if(createOrderForm.getOrderType().equals("BUY")){
                List<MarketOrder> marketOrders = this.getSellOrdersSorted();
                int i = 0;
                while(i<marketOrders.size()){
                    int matchedPrice = marketOrders.get(i).getPrice();
                    int buyerQuantity = createOrderForm.getQuantity();
                    int sellerQuantity = marketOrders.get(i).getQuantity();
                    if(buyerQuantity >= sellerQuantity){
                        createOrderForm.setQuantity(buyerQuantity-sellerQuantity);
                        //TODO dodaj u tabelu TransactionHistory
                        marketOrderRepository.deleteById(marketOrders.get(i).getId());
                    }
                    else{
                        marketOrders.get(i).setQuantity((sellerQuantity - buyerQuantity));
                        createOrderForm.setQuantity(0);
                        marketOrderRepository.save(marketOrders.get(i));
                        break;
                    }
                    i++;
                }
            }
            else if (createOrderForm.getOrderType().equals("SELL")) {
                List<MarketOrder> marketOrders = this.getBuyOrdersSorted();
                int i = 0;
                while(i<marketOrders.size()){
                    int matchedPrice = marketOrders.get(i).getPrice();
                    int sellerQuantity = createOrderForm.getQuantity();
                    int buyerQuantity = marketOrders.get(i).getQuantity();
                    if(sellerQuantity >= buyerQuantity){
                        createOrderForm.setQuantity(sellerQuantity-buyerQuantity);
                        //TODO dodaj u tabelu TransactionHistory
                        marketOrderRepository.deleteById(marketOrders.get(i).getId());
                    }
                    else{
                        marketOrders.get(i).setQuantity((buyerQuantity - sellerQuantity));
                        createOrderForm.setQuantity(0);
                        marketOrderRepository.save(marketOrders.get(i));
                        break;
                    }
                    i++;
                }
            }
        }
        else {
            throw new Exception("Illegal state for price type!");
        }
    }
}
