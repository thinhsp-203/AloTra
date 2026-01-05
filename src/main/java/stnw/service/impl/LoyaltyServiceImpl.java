package stnw.service.impl;

import stnw.config.JpaUtil;
import stnw.dao.PointTransactionDao;
import stnw.dao.RewardDao;
import stnw.dao.impl.PointTransactionDaoImpl;
import stnw.dao.impl.RewardDaoImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
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
        
        EntityManager em = JpaUtil.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            
            // Refresh user từ DB để lấy điểm hiện tại
            User currentUser = em.find(User.class, user.getId());
            if (currentUser == null) {
                throw new IllegalArgumentException("User không tồn tại");
            }
            
            // Cập nhật điểm cho user
            Integer currentPoints = currentUser.getLoyalty_points() != null ? currentUser.getLoyalty_points() : 0;
            currentUser.setLoyalty_points(currentPoints + pointsEarned);
            em.merge(currentUser);
            
            // Tạo transaction record
            PointTransaction transaction = new PointTransaction();
            transaction.setUser(currentUser);
            transaction.setPoints(pointsEarned);
            transaction.setType("EARN");
            transaction.setDescription("Tích điểm từ đơn hàng #" + orderId);
            transaction.setBalance_after(currentUser.getLoyalty_points());
            
            // Link với order nếu có
            if (orderId != null) {
                stnw.model.Orders order = em.find(stnw.model.Orders.class, orderId);
                transaction.setOrder(order);
            }
            
            em.persist(transaction);
            
            trans.commit();
            return pointsEarned;
        } catch (Exception e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw new RuntimeException("Lỗi khi tích điểm: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public boolean redeemReward(User user, Integer rewardId) throws IllegalArgumentException {
        if (user == null || rewardId == null) {
            throw new IllegalArgumentException("Thông tin không hợp lệ");
        }
        
        EntityManager em = JpaUtil.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            
            // Load reward
            Reward reward = em.find(Reward.class, rewardId);
            if (reward == null || reward.getIsActive() == null || !reward.getIsActive()) {
                throw new IllegalArgumentException("Quà tặng không tồn tại hoặc không hoạt động");
            }
            
            // Load user với điểm hiện tại
            User currentUser = em.find(User.class, user.getId());
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
            em.merge(currentUser);
            
            // Giảm stock nếu có
            if (reward.getStock() != null) {
                reward.setStock(reward.getStock() - 1);
                em.merge(reward);
            }
            
            // Tạo transaction record
            PointTransaction transaction = new PointTransaction();
            transaction.setUser(currentUser);
            transaction.setPoints(-pointsRequired); // Số âm vì là trừ điểm
            transaction.setType("REDEEM");
            transaction.setDescription("Đổi quà: " + reward.getName());
            transaction.setBalance_after(currentUser.getLoyalty_points());
            transaction.setReward(reward);
            
            em.persist(transaction);
            
            trans.commit();
            return true;
        } catch (IllegalArgumentException e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } catch (Exception e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw new RuntimeException("Lỗi khi đổi quà: " + e.getMessage(), e);
        } finally {
            em.close();
        }
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
        EntityManager em = JpaUtil.em();
        try {
            User user = em.find(User.class, userId);
            return user != null && user.getLoyalty_points() != null ? user.getLoyalty_points() : 0;
        } finally {
            em.close();
        }
    }
}
