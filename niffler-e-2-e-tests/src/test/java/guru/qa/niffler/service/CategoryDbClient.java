package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDAO;
import guru.qa.niffler.data.dao.impl.CategoryDAOJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;

import java.util.Optional;


public class CategoryDbClient {

    private static final Config CFG = Config.getInstance();

    private final CategoryDAO categoryDAO = new CategoryDAOJdbc();

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    public Optional<CategoryJson> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        return jdbcTxTemplate.execute(() -> {
                    Optional<CategoryEntity> category = categoryDAO
                            .findCategoryByUsernameAndCategoryName(username, categoryName);
                    return category.map(CategoryJson::fromEntity);
                }
        );
    }

    public CategoryJson createCategory(CategoryJson categoryJson) {
        return jdbcTxTemplate.execute(() -> {
                    CategoryEntity category = categoryDAO.createCategory(
                            CategoryEntity.fromJson(categoryJson)
                    );
                    return CategoryJson.fromEntity(category);
                }
        );

    }

    public CategoryJson updateCategory(CategoryJson categoryJson) {
        return jdbcTxTemplate.execute(() -> {
                    CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
                    return CategoryJson.fromEntity(categoryDAO.updateCategory(categoryEntity));
                }
        );
    }
}
