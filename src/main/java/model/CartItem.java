package model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

@Getter @Setter
public class CartItem {
  private Integer productId;
  private String productName;
  private String sizeName;       // optional
  private String toppingsCsv;    // optional "1,3,5"
  private int quantity;
  private BigDecimal unitPrice;        // giá gốc
  private BigDecimal sizeAdj;          // +size
  private BigDecimal toppingsCost;     // +topping

  public BigDecimal getLineTotal() {
    BigDecimal base = unitPrice != null ? unitPrice : BigDecimal.ZERO;
    BigDecimal adj  = sizeAdj   != null ? sizeAdj   : BigDecimal.ZERO;
    BigDecimal top  = toppingsCost != null ? toppingsCost : BigDecimal.ZERO;
    return base.add(adj).add(top).multiply(BigDecimal.valueOf(quantity));
  }

  @Override public boolean equals(Object o){
    if(this==o) return true;
    if(!(o instanceof CartItem)) return false;
    CartItem that=(CartItem)o;
    return Objects.equals(productId,that.productId) &&
           Objects.equals(sizeName,that.sizeName) &&
           Objects.equals(toppingsCsv,that.toppingsCsv);
  }
  @Override public int hashCode(){ return Objects.hash(productId,sizeName,toppingsCsv); }
}
