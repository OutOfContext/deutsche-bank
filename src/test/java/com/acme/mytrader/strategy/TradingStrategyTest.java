package com.acme.mytrader.strategy;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.acme.mytrader.execution.ExecutionService;
import com.github.javafaker.Faker;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class TradingStrategyTest {

  private final Faker faker = new Faker(Locale.ENGLISH);
  private Map<String, Object> configuration = new HashMap<>();
  private final List<String> stockNames = new ArrayList<>(Arrays.asList("IBM", "APC", "FB2A", "AMZ", "SAP"));
  private ExecutionService executionService;
  private TradingStrategy tradingStrategy;

  @Before
  public void setup() {
    executionService = mock(ExecutionService.class);
    configuration.put("buy_threshold", 30.0);
    configuration.put("sell_threshold", 40.0);
    configuration.put("buy_volume", 100);
    configuration.put("sell_volume", 100);
    tradingStrategy = new TradingStrategy(executionService, configuration);
  }

  @Test
  public void generateTestCasesAndVerifyExecutionServiceUsage() {
    int buyVolume = (int) configuration.get("buy_volume");
    int sellVolume = (int) configuration.get("sell_volume");
    for (int i = 0; i < 10; i++) {
      if (i < 5) {
        tradingStrategy.executeWith(stockNames.get(faker.random().nextInt(0, 4)), faker.number().randomDouble(2, 1, 25));
      } else {
        tradingStrategy.executeWith(stockNames.get(faker.random().nextInt(0, 4)), faker.number().randomDouble(2, 40, 100));
      }
    }
    verify(executionService, times(5)).buy(anyString(), anyDouble(), eq(buyVolume));
    verify(executionService, times(5)).sell(anyString(), anyDouble(), eq(sellVolume));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSecurityParameterNull() {
    tradingStrategy.executeWith(null, 0.00);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidConfiguration(){
    configuration = new HashMap<>();
    configuration.put("buy_threshold","invalidInput");
    configuration.put("sell_threshold", 40.0);
    configuration.put("buy_volume", 100);
    configuration.put("sell_volume", 100);
    tradingStrategy = new TradingStrategy(executionService,configuration);
    tradingStrategy.executeWith(stockNames.get(0),100.00);
  }

  @Test
  public void testBuyAndSellThresholdAreEqual(){
    configuration = new HashMap<>();
    configuration.put("buy_threshold",40.0);
    configuration.put("sell_threshold", 40.0);
    configuration.put("buy_volume", 100);
    configuration.put("sell_volume", 100);
    tradingStrategy = new TradingStrategy(executionService,configuration);
    for (int i = 0; i < 10; i++) {
        tradingStrategy.executeWith(stockNames.get(faker.random().nextInt(0, 4)), 40.0);
    }
    verify(executionService, times(10)).buy(anyString(), anyDouble(), eq(100));
    verify(executionService, times(10)).sell(anyString(), anyDouble(), eq(100));
  }
}
