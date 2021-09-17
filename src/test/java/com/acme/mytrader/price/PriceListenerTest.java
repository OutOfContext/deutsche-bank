package com.acme.mytrader.price;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.acme.mytrader.strategy.TradingStrategy;
import com.github.javafaker.Faker;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;

public class PriceListenerTest {

  private final Faker faker = new Faker(Locale.ENGLISH);
  private final List<String> stockNames = new ArrayList<>(Arrays.asList("IBM", "APC", "FB2A", "AMZ", "SAP"));
  private TradingStrategy tradingStrategy;
  private PriceListener priceListener;
  private PriceSource priceSource;

  @Before
  public void setup() {
    tradingStrategy = mock(TradingStrategy.class);
    priceListener = new PriceListenerImpl(tradingStrategy);
    priceSource = mock(PriceSource.class);
    doAnswer(invocation -> {
      priceListener.priceUpdate(stockNames.get(faker.random().nextInt(0, 4)),faker.number().randomDouble(2, 1, 100));
      return null;
    }).when(priceSource).notifyListeners();
  }

  @Test
  public void generateTestPriceEventsAndVerifyStrategyExecution() {
    for (int i = 0; i < 10; i++) {
      priceSource.notifyListeners();
    }
    verify(tradingStrategy, times(10)).executeWith(anyString(), anyDouble());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSecurityParameterNull() {
    priceListener.priceUpdate(null, 0.0);
  }

  @Test
  public void testPriceIsNegative() {
    priceListener.priceUpdate(stockNames.get(0), -12.0);
    verify(tradingStrategy, times(1)).executeWith(stockNames.get(0), -12.0);
  }

}
