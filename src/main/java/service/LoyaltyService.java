package service;

import model.PointTransaction;
import model.Reward;
import model.User;
import java.util.List;

public interface LoyaltyService {
    /**
     * Tích điểm cho user sau khi đặt hàng thành công
     * @param user User đặt hàng
     * @param orderTotal Tổng tiền đơn hàng
     * @param orderId ID đơn hàng
     * @return Số điểm đã tích được
     */
    int earnPointsFromOrder(User user, java.math.BigDecimal orderTotal, Integer orderId);
    
    /**
     * Đổi quà bằng điểm
     * @param user User đổi quà
     * @param rewardId ID quà tặng
     * @return true nếu thành công
     * @throws IllegalArgumentException nếu không đủ điểm hoặc hết hàng
     */
    boolean redeemReward(User user, Integer rewardId) throws IllegalArgumentException;
    
    /**
     * Lấy danh sách quà tặng đang hoạt động
     */
    List<Reward> getActiveRewards();
    
    /**
     * Lấy thông tin quà tặng theo ID
     */
    Reward getRewardById(Integer rewardId);
    
    /**
     * Lấy lịch sử giao dịch điểm của user
     */
    List<PointTransaction> getPointHistory(Integer userId);
    
    /**
     * Lấy số điểm hiện tại của user
     */
    Integer getUserPoints(Integer userId);
}

