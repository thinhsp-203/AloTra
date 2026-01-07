package stnw.service;

import java.util.Map;

public interface AdminSettingsService {
    Map<String, String> getAllSettings();
    void updateSettings(Map<String, String> settings);
}