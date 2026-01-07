package stnw.dao;

import stnw.model.Settings;
import java.util.List;
import java.util.Map;

public interface SettingsDao {
    List<Settings> findAll();
    Settings findByKey(String key);
    void save(Settings setting);
    void update(Settings setting);
    void saveOrUpdate(Settings setting);
}

