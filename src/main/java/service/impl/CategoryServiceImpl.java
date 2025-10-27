package service.impl;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dao.CategoryDao;
import dao.impl.CategoryDaoImpl;
import model.Category;
import service.CategoryService;
import utils.Constant; // Assuming Constant.DIR holds the base upload directory

public class CategoryServiceImpl implements CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
    private final CategoryDao categoryDao = new CategoryDaoImpl();

    @Override
    public void insert(Category category) {
        categoryDao.insert(category);
    }

    @Override
    public void delete(int id) {
        // It's good practice to also delete the associated icon file from the server
        Category category = get(id);
        if (category != null && category.getIcon() != null && !category.getIcon().isEmpty()) {
            deleteIconFile(category.getIcon());
        }
        categoryDao.delete(id);
    }

    @Override
    public Category get(int id) {
        return categoryDao.get(id);
    }

    @Override
    public void edit(Category newCategory) {
        Category oldCategory = categoryDao.get(newCategory.getId());
        if (oldCategory == null) {
            logger.error("Category with id {} not found. Cannot edit.", newCategory.getId());
            return;
        }

        oldCategory.setName(newCategory.getName());

        // If a new icon is provided, delete the old one and update the path
        if (newCategory.getIcon() != null && !newCategory.getIcon().isEmpty()) {
            // Delete the old icon file if it exists
            if (oldCategory.getIcon() != null && !oldCategory.getIcon().isEmpty()) {
                deleteIconFile(oldCategory.getIcon());
            }
            oldCategory.setIcon(newCategory.getIcon());
        }

        categoryDao.edit(oldCategory);
    }

    @Override
    public Category get(String name) {
        return categoryDao.get(name);
    }

    @Override
    public List<Category> getAll() {
        return categoryDao.getAll();
    }

    @Override
    public List<Category> search(String catename) {
        return categoryDao.search(catename);
    }

    /**
     * Deletes an icon file from the server.
     *
     * @param iconPath The relative path to the icon file.
     */
    private void deleteIconFile(String iconPath) {
        if (iconPath == null || iconPath.isEmpty()) {
            return;
        }

        try {
            // Construct the absolute path to the file
            // Note: It's better to get the base path from a configuration file or servlet context
            // rather than a hardcoded Constant class.
            String basePath = Constant.DIR;
            File fileToDelete = Paths.get(basePath, iconPath).toFile();

            if (fileToDelete.exists()) {
                if (fileToDelete.delete()) {
                    logger.info("Successfully deleted old icon: {}", iconPath);
                } else {
                    logger.warn("Failed to delete old icon: {}", iconPath);
                }
            }
        } catch (Exception e) {
            logger.error("Error occurred while deleting icon file: {}", iconPath, e);
        }
    }
}