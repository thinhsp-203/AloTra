package service;

import model.Reward;
import java.util.List;

public interface AdminRewardService {
    List<Reward> getAllRewards();
    Reward getRewardById(Integer id);
    void saveReward(Reward reward);
    void updateReward(Reward reward);
    void deleteReward(Integer id);
    long countRewards();
}

