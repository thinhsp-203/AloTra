package stnw.service.impl;

import stnw.dao.ToppingDao;
import stnw.dao.impl.ToppingDaoImpl;
import stnw.model.Topping;
import stnw.service.AdminToppingService;

import java.util.List;

public class AdminToppingServiceImpl implements AdminToppingService {
    
    private final ToppingDao toppingDao = new ToppingDaoImpl();
    
    @Override
    public List<Topping> getAllToppings() {
        return toppingDao.findAll();
    }
    
    @Override
    public Topping getToppingById(int id) {
        return toppingDao.findById(id);
    }
    
    @Override
    public void saveTopping(Topping topping) {
        if (topping.getTopping_id() == null) {
            toppingDao.save(topping);
        } else {
            toppingDao.update(topping);
        }
    }
    
    @Override
    public void deleteTopping(int id) {
        toppingDao.delete(id);
    }
}