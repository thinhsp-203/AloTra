package service;

import model.CartItem;

import java.util.List;

/**
 * Service xử lý logic giỏ hàng
 */
public interface CartService {
    
    /**
     * Thêm sản phẩm vào giỏ hàng
     * @param cart Giỏ hàng hiện tại
     * @param productId ID sản phẩm
     * @param quantity Số lượng
     * @param sizeName Tên size (có thể null)
     * @param sugarLevel Độ ngọt: Ít, Bình thường, Nhiều (có thể null)
     * @param iceLevel Mức đá: Ít, Bình thường, Nhiều (có thể null)
     * @param toppingParam Chuỗi topping "id:qty,id:qty" (có thể null)
     * @return CartItem mới được thêm hoặc đã cập nhật
     */
    CartItem addToCart(List<CartItem> cart, int productId, int quantity, 
                      String sizeName, String sugarLevel, String iceLevel, String toppingParam);
    
    /**
     * Cập nhật số lượng item trong giỏ
     */
    void updateQuantity(List<CartItem> cart, int productId, String sizeName, 
                       String sugarLevel, String iceLevel, String toppingsCsv, int newQuantity);
    
    /**
     * Xóa item khỏi giỏ
     */
    void removeItem(List<CartItem> cart, int productId, String sizeName, 
                   String sugarLevel, String iceLevel, String toppingsCsv);
    
    /**
     * Cập nhật chi tiết item (size, sugar, ice, topping) từ modal edit
     * @return true nếu cập nhật thành công
     */
    boolean updateItemDetails(List<CartItem> cart, int oldProductId, 
                            String oldSize, String oldSugarLevel, String oldIceLevel, String oldToppingsCsv,
                            String newSize, String newSugarLevel, String newIceLevel, String newToppingParam, 
                            int quantity);
}