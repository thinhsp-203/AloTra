package model;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Objects;

@Getter @Setter
public class CartItem {
  private Integer productId;
  private String productName;
  private String thumbnail; 
  private String sizeName;
  private String sugarLevel; // Độ ngọt: Ít, Bình thường, Nhiều
  private String iceLevel; // Mức đá: Ít, Bình thường, Nhiều
  private String toppingsCsv;
  private int quantity;
  private BigDecimal unitPrice;
  private BigDecimal sizeAdj;
  private BigDecimal toppingsCost;

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
	    // So sánh cả productId, size, sugar, ice và toppings
	    return Objects.equals(productId,that.productId) &&
	           Objects.equals(sizeName,that.sizeName) &&
	           Objects.equals(sugarLevel,that.sugarLevel) &&
	           Objects.equals(iceLevel,that.iceLevel) &&
	           Objects.equals(toppingsCsv,that.toppingsCsv);
	  }
	  @Override public int hashCode(){ 
	    return Objects.hash(productId,sizeName,sugarLevel,iceLevel,toppingsCsv); 
	  }
	}