package stnw.service.impl;

import stnw.dao.RewardDao;
import stnw.dao.impl.RewardDaoImpl;
import stnw.model.Reward;
import stnw.service.AdminRewardService;

import java.util.List;

public class AdminRewardServiceImpl implements AdminRewardService {
    
    private final RewardDao rewardDao = new RewardDaoImpl();
    
    @Override
    public List<Reward> getAllRewards() {
        return rewardDao.findAll();
    }
    
    @Override
    public Reward getRewardById(Integer id) {
        return rewardDao.findById(id);
    }
    
    @Override
    public void saveReward(Reward reward) {
        rewardDao.save(reward);
    }
    
    @Override
    public void updateReward(Reward reward) {
        rewardDao.update(reward);
    }
    
    @Override
    public void deleteReward(Integer id) {
        rewardDao.delete(id);
    }
    
    @Override
    public long countRewards() {
        return rewardDao.count();
    }
}

