package stnw.service;
import java.util.List;
import stnw.model.Category;
public interface CategoryService {
void insert(Category category);
/**
 * Thêm danh mục từ parameters
 * Controller chỉ truyền parameters, Service tự tạo Entity
 */
void insertFromParams(String name, String iconPath, Boolean isDrink);
void edit(Category category);
/**
 * Cập nhật danh mục từ parameters
 * Controller chỉ truyền parameters, Service tự tạo Entity
 */
void editFromParams(Integer id, String name, String iconPath, Boolean isDrink);
void delete(int id);
Category get(int id);
Category get(String name);
List<Category> getAll();
List<Category> search(String keyword);
}