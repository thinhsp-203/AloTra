package stnw.dao.impl;

import stnw.utils.JpaUtils;
import stnw.dao.SettingsDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import stnw.model.Settings;

import java.util.List;

public class SettingsDaoImpl implements SettingsDao {

    @Override
    public List<Settings> findAll() {
        EntityManager em = JpaUtils.em();
        try {
            return em.createQuery("SELECT s FROM Settings s", Settings.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Settings findByKey(String key) {
        EntityManager em = JpaUtils.em();
        try {
            return em.find(Settings.class, key);
        } finally {
            em.close();
        }
    }

    @Override
    public void save(Settings setting) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.persist(setting);
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Settings setting) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.merge(setting);
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void saveOrUpdate(Settings setting) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            Settings existing = em.find(Settings.class, setting.getKey());
            if (existing == null) {
                em.persist(setting);
            } else {
                existing.setValue(setting.getValue());
                em.merge(existing);
            }
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}

