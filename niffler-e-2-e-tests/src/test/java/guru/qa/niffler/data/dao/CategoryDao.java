package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.spend.CategoryEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryDao {
  CategoryEntity create(CategoryEntity category);

  void update(CategoryEntity category);

  Optional<CategoryEntity> findCategoryById(UUID id);

  List<CategoryEntity> findAll();

  Optional<CategoryEntity> findCategoryByUsernameAndName(String username, String name);

  void removeCategory(CategoryEntity category);

}
