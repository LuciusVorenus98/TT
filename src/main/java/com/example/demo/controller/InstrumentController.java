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

    public InstrumentController(InstrumentService instrumentService) {
        this.instrumentService = instrumentService;
    }

    @GetMapping
    public String getInstruments(Model model) {
        List<Instrument> instruments = instrumentService.getInstruments();
        BestBuySellPrice bestBuySellPrice = instrumentService.getBestBuySellPrice();
        model.addAttribute("instruments", instruments);
        model.addAttribute("bestBuySell", bestBuySellPrice);
        return "instruments";
    }

    @GetMapping(path = "/{shortName}")
    public String getInstrumentByShortName(@PathVariable("shortName") String shortName, Model model) {
        Instrument instrument = instrumentService.getInstrumentByShortName(shortName);
        model.addAttribute("instrument", instrument);
        List<MarketOrder> buyOrders = instrumentService.getBuyOrdersSorted();
        List<MarketOrder> sellOrders = instrumentService.getSellOrdersSorted();
        int numOfBestBuyOrders = 10;
        while (buyOrders.size() < numOfBestBuyOrders) {
            buyOrders.add(new MarketOrder());
        }
        while (sellOrders.size() < numOfBestBuyOrders) {
            sellOrders.add(new MarketOrder());
        }
        buyOrders = buyOrders.subList(0, numOfBestBuyOrders);
        sellOrders = sellOrders.subList(0, numOfBestBuyOrders);
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
        createOrderForm.setShortName(shortName);
        instrumentService.processOrder(createOrderForm);
        return "instruments";
    }
}
