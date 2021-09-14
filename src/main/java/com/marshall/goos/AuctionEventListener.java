package com.marshall.goos;

public interface AuctionEventListener {
    void auctionClosed();
    void currentPrice(int price, int increment);
}
