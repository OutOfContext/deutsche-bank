package com.acme.mytrader.price;

import com.acme.mytrader.strategy.TradingStrategy;

/**
 * Develop a basic implementation of the PriceListener interface that provides the following behaviour:
 * <p>
 * Monitors price movements on a specified single stock (e.g. "IBM") Executes a single "buy" instruction for a specified number of lots
 * (e.g. 100) as soon as the price of that stock is seen to be below a specified price (e.g. 55.0). Don’t worry what units that is in.
 */
public class PriceListenerImpl implements PriceListener {

  private final TradingStrategy tradingStrategy;

  public PriceListenerImpl(TradingStrategy tradingStrategy) {
    this.tradingStrategy = tradingStrategy;
  }

  @Override
  public void priceUpdate(String security, double price) throws IllegalArgumentException {
    if (security == null) {
      throw new IllegalArgumentException("1st parameter of type 'String' must not be null.");
    }
    tradingStrategy.executeWith(security, price);
  }
}
