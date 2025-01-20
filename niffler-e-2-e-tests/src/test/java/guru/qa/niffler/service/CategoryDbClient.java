package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDAO;
import guru.qa.niffler.data.dao.AuthUserDAO;
import guru.qa.niffler.data.dao.CategoryDAO;
import guru.qa.niffler.data.dao.UDUserDAO;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDAOJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDAOJdbc;
import guru.qa.niffler.data.dao.impl.CategoryDAOJdbc;
import guru.qa.niffler.data.dao.impl.UDUserDAOSpringJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

import static java.sql.Connection.TRANSACTION_READ_UNCOMMITTED;


public class CategoryDbClient {

    private static final Config CFG = Config.getInstance();

    public Optional<CategoryJson> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        return transaction(TRANSACTION_READ_UNCOMMITTED, connection -> {
                    Optional<CategoryEntity> category = new CategoryDAOJdbc(connection)
                            .findCategoryByUsernameAndCategoryName(username, categoryName);
                    return category.map(CategoryJson::fromEntity);
                },
                CFG.spendJdbcUrl()
        );
    }

    public CategoryJson createCategory(CategoryJson categoryJson) {
        return transaction(TRANSACTION_READ_UNCOMMITTED, connection -> {
                    CategoryEntity category = new CategoryDAOJdbc(connection).createCategory(
                            CategoryEntity.fromJson(categoryJson)
                    );
                    return CategoryJson.fromEntity(category);
                },
                CFG.spendJdbcUrl()
        );

    }

    public CategoryJson updateCategory(CategoryJson categoryJson) {
        return transaction(TRANSACTION_READ_UNCOMMITTED, connection -> {
                    CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
                    return CategoryJson.fromEntity(new CategoryDAOJdbc(connection).updateCategory(categoryEntity));
                },
                CFG.spendJdbcUrl()
        );
    }
}
