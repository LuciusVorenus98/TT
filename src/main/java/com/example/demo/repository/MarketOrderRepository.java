package com.example.demo.repository;

import com.example.demo.model.MarketOrder;
import com.example.demo.model.OrderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MarketOrderRepository extends JpaRepository<MarketOrder, Long> {
    List<MarketOrder> findByOrderType(OrderType ot);

    List<MarketOrder> findAllByOrderByPriceAsc();

    List<MarketOrder> findByOrderTypeOrderByPriceAsc(OrderType ot);

    @Query(value = "SELECT MAX(price) FROM MARKET_ORDER mo WHERE mo.ORDER_TYPE = 'BUY' AND mo.order_book_id = ?1", nativeQuery = true)
    int findBestBuyPrice(Long id);

    @Query(value = "SELECT MIN(price) FROM MARKET_ORDER mo WHERE mo.ORDER_TYPE = 'SELL' AND mo.order_book_id = ?1", nativeQuery = true)
    int findBestSellPrice(Long id);

    @Query(value = "SELECT * FROM MARKET_ORDER mo WHERE mo.ORDER_TYPE = 'BUY' AND mo.order_book_id = ?1 " +
            "ORDER BY mo.PRICE DESC, mo.TIME ASC", nativeQuery = true)
    List<MarketOrder> findBuyOrdersSorted(Long id);

    @Query(value = "SELECT * FROM MARKET_ORDER mo WHERE mo.ORDER_TYPE = 'SELL' AND mo.order_book_id = ?1 " +
            "ORDER BY mo.PRICE ASC, mo.TIME ASC", nativeQuery = true)
    List<MarketOrder> findSellOrdersSorted(Long id);
}
