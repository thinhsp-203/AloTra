package service;

import java.util.List;

import model.Topping;

public interface AdminToppingService {
    List<Topping> getAllToppings();
    Topping getToppingById(int id);
    void saveTopping(Topping topping);
    void deleteTopping(int id);
}