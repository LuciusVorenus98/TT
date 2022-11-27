package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class MarketOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private OrderType orderType;
    private int price;
    private int quantity;
    private LocalDateTime time;
//    @ManyToOne
//    @JoinColumn(name = "order_book_id")
//    private OrderBook orderBook;
//
//    public void setOrderBook(OrderBook orderBook) {
//        this.orderBook = orderBook;
//    }

    public MarketOrder(OrderType orderType, int price, int quantity, LocalDateTime time) {
        this.orderType = orderType;
        this.price = price;
        this.quantity = quantity;
        this.time = time;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderType=" + orderType +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        MarketOrder that = (MarketOrder) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
