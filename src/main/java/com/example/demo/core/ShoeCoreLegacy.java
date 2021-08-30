package com.example.demo.core;

import com.example.demo.dto.in.ShoeFilter.Color;
import com.example.demo.dto.in.ShoeFilter;
import com.example.demo.dto.in.ShoeStockMovement;
import com.example.demo.dto.out.Shoe;
import com.example.demo.dto.out.ShoeQuantity;
import com.example.demo.dto.out.Shoes;
import com.example.demo.dto.out.StockState;
import com.example.demo.entity.ShoeStock;
import com.example.demo.entity.Stock;
import com.example.demo.mapper.ShoeMapper;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Implementation(version = 1)
public class ShoeCoreLegacy extends AbstractShoeCore {

  @Override
  public Shoes search(final ShoeFilter filter) {
    return Shoes.builder()
            .shoes(List.of(Shoe.builder()
                    .name("Legacy shoe")
                    .color(Color.BLUE)
                    .size(BigInteger.ONE)
                    .build()))
            .build();
  }

  @Override
  public void update(ShoeStockMovement shoeStockMovement) {

    ShoeStock shoeStock = ShoeMapper.INSTANCE.ssMvtTosStock(shoeStockMovement);
    Stock.getInstance().addShoesToStock(shoeStock);

  }

  @Override
  public StockState getStock() {
    StockState stockState = ShoeMapper.INSTANCE.stockToStockState(Stock.getInstance());
    Optional<BigInteger> shoesTotalOpt = stockState.getShoeQuantities().stream().map(ShoeQuantity::getQuantity).reduce(BigInteger::add);
    if(shoesTotalOpt.isPresent()) {
      BigInteger shoesTotal = shoesTotalOpt.get();
      if(shoesTotal.equals(BigInteger.ZERO)) {
        stockState.setState(StockState.State.EMPTY);
      } else if(shoesTotal.equals(Stock.MAX_SIZE)) {
        stockState.setState(StockState.State.FULL);
      } else {
        stockState.setState(StockState.State.SOME);
      }
    } else {
      stockState.setState(StockState.State.EMPTY);
    }
    return stockState;
  }

}
