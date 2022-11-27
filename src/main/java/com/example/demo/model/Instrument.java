package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Instrument {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String fullName;
    @Column(unique = true)
    private String shortName;
    @OneToOne
    private OrderBook orderBook;

    public Instrument(String fullName, String shortName, OrderBook orderBook) {
        this.fullName = fullName;
        this.shortName = shortName;
        this.orderBook = orderBook;
    }
}
