package service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import config.JpaUtil;
import jakarta.persistence.EntityManager;
import model.Settings;
import service.AdminSettingsService;

public class AdminSettingsServiceImpl implements AdminSettingsService {
    
    @Override
    public Map<String, String> getAllSettings() {
        EntityManager em = JpaUtil.em();
        try {
            List<Settings> settingsList = em.createQuery("SELECT s FROM Settings s", Settings.class)
                .getResultList();
            
            return settingsList.stream()
                .collect(Collectors.toMap(Settings::getKey, Settings::getValue));
        } finally {
            em.close();
        }
    }
    
    @Override
    public void updateSettings(Map<String, String> settings) {
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            
            for (Map.Entry<String, String> entry : settings.entrySet()) {
                Settings setting = em.find(Settings.class, entry.getKey());
                if (setting == null) {
                    setting = new Settings();
                    setting.setKey(entry.getKey());
                }
                setting.setValue(entry.getValue());
                em.merge(setting);
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Lỗi khi cập nhật settings: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}