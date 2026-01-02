package service.impl;

import dao.AboutUsDao;
import dao.impl.AboutUsDaoImpl;
import model.AboutUs;
import service.AboutService;

import java.util.List;

public class AboutServiceImpl implements AboutService {
    
    private final AboutUsDao aboutUsDao = new AboutUsDaoImpl();
    
    @Override
    public List<AboutUs> getActiveAboutUs() {
        return aboutUsDao.findAllActiveOrderBySortOrder();
    }
}

