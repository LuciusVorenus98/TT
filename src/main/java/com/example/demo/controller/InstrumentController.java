package com.example.demo.controller;

import com.example.demo.model.DTO.BestBuySellPrice;
import com.example.demo.model.DTO.CreateOrderForm;
import com.example.demo.model.Instrument;
import com.example.demo.model.MarketOrder;
import com.example.demo.service.InstrumentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(path = "instruments")
public class InstrumentController {

    private final InstrumentService instrumentService;
    private static final int LENGTH_OF_BEST_ORDERS = 10;

    public InstrumentController(InstrumentService instrumentService) {
        this.instrumentService = instrumentService;
    }

    private void loadOrderList(List<MarketOrder> orderList, int length){
        while (orderList.size() < length) {
            orderList.add(new MarketOrder());
        }
    }
    private void loadBestOrders(List<MarketOrder> buyOrders, List<MarketOrder> sellOrders, int length){
        loadOrderList(buyOrders, length);
        loadOrderList(sellOrders, length);
    }

    @GetMapping
    public String getInstruments(Model model) {
        List<Instrument> instruments = instrumentService.getInstruments();
        List<BestBuySellPrice> bestBuySellPrices = instrumentService.getBestBuySellPrices();
        model.addAttribute("instruments", instruments);
        model.addAttribute("bestBuySell", bestBuySellPrices);
        return "instruments";
    }

    @GetMapping(path = "/{shortName}")
    public String getInstrumentByShortName(@PathVariable("shortName") String shortName, Model model) {
        Instrument instrument = instrumentService.getInstrumentByShortName(shortName);
        model.addAttribute("instrument", instrument);
        List<MarketOrder> buyOrders = instrumentService.getBuyOrdersSorted(instrument.getId());
        List<MarketOrder> sellOrders = instrumentService.getSellOrdersSorted(instrument.getId());
        loadBestOrders(buyOrders, sellOrders, LENGTH_OF_BEST_ORDERS);
        buyOrders = buyOrders.subList(0, LENGTH_OF_BEST_ORDERS);
        sellOrders = sellOrders.subList(0, LENGTH_OF_BEST_ORDERS);
        model.addAttribute("buyOrders", buyOrders);
        model.addAttribute("sellOrders", sellOrders);
        return "orderBook";
    }

    @GetMapping(path = "create-order/{shortName}")
    public String createNewOrderForm(@PathVariable("shortName") String shortName, Model model) {
        model.addAttribute("createOrderForm", new CreateOrderForm());
        return "createOrder";
    }

    @PostMapping(path = "create-order/{shortName}")
    public String submitNewOrder(@PathVariable("shortName") String shortName,
                                 @ModelAttribute CreateOrderForm createOrderForm,
                                 Model model) throws Exception {
        model.addAttribute("createOrderForm", createOrderForm);
//        createOrderForm.setShortName(shortName);
        instrumentService.processOrder(createOrderForm, shortName);
        model.addAttribute("shortName", shortName);
        model.addAttribute("log", instrumentService.getResponseMessageLog());
        return "message";
    }
}
