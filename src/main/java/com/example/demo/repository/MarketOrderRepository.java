package com.example.demo.repository;

import com.example.demo.model.Instrument;
import com.example.demo.model.MarketOrder;
import com.example.demo.model.OrderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MarketOrderRepository extends JpaRepository<MarketOrder, Long> {
    List<MarketOrder> findByOrderType(OrderType ot);
    List<MarketOrder> findAllByOrderByPriceAsc();
    List<MarketOrder> findByOrderTypeOrderByPriceAsc(OrderType ot);
    @Query(value = "SELECT MAX(price) FROM MarketOrder mo WHERE mo.orderType = BUY")
    int findBestBuyPrice();
    @Query(value = "SELECT MIN(price) FROM MarketOrder mo WHERE mo.orderType = SELL")
    int findBestSellPrice();
    @Query(value = "SELECT * FROM MARKET_ORDER mo WHERE mo.ORDER_TYPE = 'BUY'" +
            "ORDER BY mo.PRICE DESC, mo.TIME ASC", nativeQuery = true)
    List<MarketOrder> findBuyOrdersSorted();
    @Query(value = "SELECT * FROM MARKET_ORDER mo WHERE mo.ORDER_TYPE = 'SELL'" +
            "ORDER BY mo.PRICE ASC, mo.TIME ASC", nativeQuery = true)
    List<MarketOrder> findSellOrdersSorted();
}
