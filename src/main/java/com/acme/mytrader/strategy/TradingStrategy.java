package com.acme.mytrader.strategy;

import static java.lang.String.format;

import com.acme.mytrader.execution.ExecutionService;
import java.util.Map;
import java.util.Optional;

/**
 * <pre>
 * User Story: As a trader I want to be able to monitor stock prices such
 * that when they breach a trigger level orders can be executed automatically
 * </pre>
 */
public class TradingStrategy {

  private static final String ERROR_MSG = "Configuration for property '%s' invalid. Value is supposed to be '%s'.";
  private final ExecutionService executionService;
  private final Map<String, Object> configuration;

  public TradingStrategy(ExecutionService executionService, Map<String, Object> configuration) {
    this.executionService = executionService;
    this.configuration = configuration;
  }

  public void executeWith(String security, Double price) throws IllegalArgumentException{
    if (security == null) {
      throw new IllegalArgumentException("1st parameter of type 'String' must not be null.");
    }
    double buyThreshold = getDoubleConfigurationInfo("buy_threshold");
    double sellThreshold = getDoubleConfigurationInfo("sell_threshold");
    int buyVolume = getIntegerConfigurationInfo("buy_volume");
    int sellVolume = getIntegerConfigurationInfo("sell_volume");

    buyForThreshold(buyThreshold, buyVolume, security, price);
    sellForThreshold(sellThreshold, sellVolume, security, price);
  }

  private Integer getIntegerConfigurationInfo(String key) {
    return Optional.ofNullable(configuration.get(key))
        .filter(Integer.class::isInstance)
        .map(Integer.class::cast)
        .orElseThrow(() -> new IllegalArgumentException(format(ERROR_MSG, key, "Integer")));
  }

  private Double getDoubleConfigurationInfo(String key) {
    return Optional.ofNullable(configuration.get(key))
        .filter(Double.class::isInstance)
        .map(Double.class::cast)
        .orElseThrow(() -> new IllegalArgumentException(format(ERROR_MSG, key, "Double")));
  }

  private void buyForThreshold(double buyThreshold, int buyVolume, String security, Double price) {
    if (price.compareTo(buyThreshold) == 0 || price.compareTo(buyThreshold) < 0) {
      executionService.buy(security, price, buyVolume);
    }
  }

  private void sellForThreshold(double sellThreshold, int sellVolume, String security, Double price) {
    if (price.compareTo(sellThreshold) == 0 || price.compareTo(sellThreshold) > 0) {
      executionService.sell(security, price, sellVolume);
    }
  }

}
