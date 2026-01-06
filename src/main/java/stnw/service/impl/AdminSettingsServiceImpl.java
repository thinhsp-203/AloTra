package stnw.service.impl;

import stnw.dao.SettingsDao;
import stnw.dao.impl.SettingsDaoImpl;
import stnw.model.Settings;
import stnw.service.AdminSettingsService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminSettingsServiceImpl implements AdminSettingsService {
    
    private final SettingsDao settingsDao = new SettingsDaoImpl();
    
    @Override
    public Map<String, String> getAllSettings() {
        List<Settings> settingsList = settingsDao.findAll();
        return settingsList.stream()
            .collect(Collectors.toMap(Settings::getKey, Settings::getValue));
    }
    
    @Override
    public void updateSettings(Map<String, String> settings) {
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            Settings setting = settingsDao.findByKey(entry.getKey());
            if (setting == null) {
                setting = new Settings();
                setting.setKey(entry.getKey());
                setting.setValue(entry.getValue());
                settingsDao.save(setting);
            } else {
                setting.setValue(entry.getValue());
                settingsDao.update(setting);
            }
        }
    }
}