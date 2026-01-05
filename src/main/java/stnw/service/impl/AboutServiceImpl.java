package stnw.service.impl;

import stnw.dao.AboutUsDao;
import stnw.dao.impl.AboutUsDaoImpl;
import stnw.model.AboutUs;
import stnw.service.AboutService;

import java.util.List;

public class AboutServiceImpl implements AboutService {
    
    private final AboutUsDao aboutUsDao = new AboutUsDaoImpl();
    
    @Override
    public List<AboutUs> getActiveAboutUs() {
        return aboutUsDao.findAllActiveOrderBySortOrder();
    }
}

