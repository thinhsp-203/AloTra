package stnw.service.impl;

import stnw.dao.OrderDao;
import stnw.dao.PointTransactionDao;
import stnw.dao.RewardDao;
import stnw.dao.UserDao;
import stnw.dao.impl.OrderDaoImpl;
import stnw.dao.impl.PointTransactionDaoImpl;
import stnw.dao.impl.RewardDaoImpl;
import stnw.dao.impl.UserDaoImpl;
import stnw.model.PointTransaction;
import stnw.model.Reward;
import stnw.model.User;
import stnw.service.LoyaltyService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class LoyaltyServiceImpl implements LoyaltyService {
    
    private final RewardDao rewardDao = new RewardDaoImpl();
    private final PointTransactionDao pointTransactionDao = new PointTransactionDaoImpl();
    private final UserDao userDao = new UserDaoImpl();
    private final OrderDao orderDao = new OrderDaoImpl();
    
    // Tỷ lệ tích điểm: 1 điểm = 1000 VND (có thể điều chỉnh)
    private static final int POINTS_PER_1000_VND = 1;
    
    @Override
    public int earnPointsFromOrder(User user, BigDecimal orderTotal, Integer orderId) {
        if (user == null || orderTotal == null || orderTotal.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        
        // Tính số điểm: orderTotal (VND) / 1000 * POINTS_PER_1000_VND
        int pointsEarned = orderTotal.divide(BigDecimal.valueOf(1000), 0, RoundingMode.DOWN)
                                     .multiply(BigDecimal.valueOf(POINTS_PER_1000_VND))
                                     .intValue();
        
        if (pointsEarned <= 0) {
            return 0;
        }
        
        // Refresh user từ DB để lấy điểm hiện tại
        User currentUser = userDao.findById(user.getId());
        if (currentUser == null) {
            throw new IllegalArgumentException("User không tồn tại");
        }
        
        // Cập nhật điểm cho user
        Integer currentPoints = currentUser.getLoyalty_points() != null ? currentUser.getLoyalty_points() : 0;
        currentUser.setLoyalty_points(currentPoints + pointsEarned);
        userDao.update(currentUser);
        
        // Tạo transaction record
        PointTransaction transaction = new PointTransaction();
        transaction.setUser(currentUser);
        transaction.setPoints(pointsEarned);
        transaction.setType("EARN");
        transaction.setDescription("Tích điểm từ đơn hàng #" + orderId);
        transaction.setBalance_after(currentUser.getLoyalty_points());
        
        // Link với order nếu có
        if (orderId != null) {
            stnw.model.Orders order = orderDao.findById(orderId);
            transaction.setOrder(order);
        }
        
        pointTransactionDao.save(transaction);
        return pointsEarned;
    }
    
    @Override
    public PointTransaction redeemReward(User user, Integer rewardId) throws IllegalArgumentException {
        if (user == null || rewardId == null) {
            throw new IllegalArgumentException("Thông tin không hợp lệ");
        }
        
        // Load reward
        Reward reward = rewardDao.findById(rewardId);
        if (reward == null || reward.getIsActive() == null || !reward.getIsActive()) {
            throw new IllegalArgumentException("Quà tặng không tồn tại hoặc không hoạt động");
        }
        
        // Load user với điểm hiện tại
        User currentUser = userDao.findById(user.getId());
        if (currentUser == null) {
            throw new IllegalArgumentException("User không tồn tại");
        }
        
        Integer currentPoints = currentUser.getLoyalty_points() != null ? currentUser.getLoyalty_points() : 0;
        Integer pointsRequired = reward.getPoints_required();
        
        // Kiểm tra đủ điểm
        if (currentPoints < pointsRequired) {
            throw new IllegalArgumentException("Bạn không đủ điểm để đổi quà này. Cần " + pointsRequired + " điểm, bạn có " + currentPoints + " điểm.");
        }
        
        // Kiểm tra tồn kho
        if (reward.getStock() != null && reward.getStock() <= 0) {
            throw new IllegalArgumentException("Quà tặng đã hết hàng");
        }
        
        // Trừ điểm
        currentUser.setLoyalty_points(currentPoints - pointsRequired);
        userDao.update(currentUser);
        
        // Giảm stock nếu có
        if (reward.getStock() != null) {
            reward.setStock(reward.getStock() - 1);
            rewardDao.update(reward);
        }
        
        // Tạo transaction record
        PointTransaction transaction = new PointTransaction();
        transaction.setUser(currentUser);
        transaction.setPoints(-pointsRequired); // Số âm vì là trừ điểm
        transaction.setType("REDEEM");
        transaction.setDescription("Đổi quà: " + reward.getName());
        transaction.setBalance_after(currentUser.getLoyalty_points());
        transaction.setReward(reward);
        
        pointTransactionDao.save(transaction);
        return transaction;
    }
    
    @Override
    public List<Reward> getActiveRewards() {
        return rewardDao.findAllActive();
    }
    
    @Override
    public Reward getRewardById(Integer rewardId) {
        return rewardDao.findById(rewardId);
    }
    
    @Override
    public List<PointTransaction> getPointHistory(Integer userId) {
        return pointTransactionDao.findByUserIdOrderByDateDesc(userId);
    }
    
    @Override
    public Integer getUserPoints(Integer userId) {
        User user = userDao.findById(userId);
        return user != null && user.getLoyalty_points() != null ? user.getLoyalty_points() : 0;
    }
    
    @Override
    public PointTransaction getTransactionById(Integer transactionId) {
        PointTransaction transaction = pointTransactionDao.findById(transactionId);
        if (transaction != null && transaction.getReward() != null) {
            // Eager load reward để hiển thị thông tin
            transaction.getReward().getName();
        }
        return transaction;
    }
}
