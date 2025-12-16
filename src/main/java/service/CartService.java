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
     * @param toppingParam Chuỗi topping "id:qty,id:qty" (có thể null)
     * @return CartItem mới được thêm hoặc đã cập nhật
     */
    CartItem addToCart(List<CartItem> cart, int productId, int quantity, 
                      String sizeName, String toppingParam);
    
    /**
     * Cập nhật số lượng item trong giỏ
     */
    void updateQuantity(List<CartItem> cart, int productId, String sizeName, 
                       String toppingsCsv, int newQuantity);
    
    /**
     * Xóa item khỏi giỏ
     */
    void removeItem(List<CartItem> cart, int productId, String sizeName, 
                   String toppingsCsv);
    
    /**
     * Cập nhật chi tiết item (size, topping) từ modal edit
     * @return true nếu cập nhật thành công
     */
    boolean updateItemDetails(List<CartItem> cart, int oldProductId, 
                            String oldSize, String oldToppingsCsv,
                            String newSize, String newToppingParam, 
                            int quantity);
}