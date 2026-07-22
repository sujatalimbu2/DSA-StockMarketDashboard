package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StockQuote {

    private String symbol;
    private double price;
    private LocalDateTime timestamp;

    public StockQuote(String symbol, double price) {
        this.symbol = symbol;
        this.price = price;
        this.timestamp = LocalDateTime.now();
    }

    public String getSymbol() {
        return symbol;
    }

    public double getPrice() {
        return price;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getFormattedTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        return timestamp.format(formatter);
    }
}