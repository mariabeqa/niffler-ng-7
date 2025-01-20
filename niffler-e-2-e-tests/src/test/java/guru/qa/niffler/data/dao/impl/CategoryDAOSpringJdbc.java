package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDAO;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.mapper.CategoryEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryDAOSpringJdbc implements CategoryDAO {

    private static final Config CFG = Config.getInstance();

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));

        KeyHolder kh = new GeneratedKeyHolder();

        jdbcTemplate.update(conn -> {
                    PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO category (name, username, archived) " +
                                    "VALUES (?, ?, ?)",
                            PreparedStatement.RETURN_GENERATED_KEYS
                    );

                    ps.setString(1, category.getName());
                    ps.setString(2, category.getUsername());
                    ps.setBoolean(3, category.isArchived());

                    return ps;
                },
                kh
        );

        final UUID generatedKey = (UUID) kh.getKeys().get("id");
        category.setId(generatedKey);

        return category;
    }

    @Override
    public CategoryEntity updateCategory(CategoryEntity category) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));

        jdbcTemplate.update(
                "UPDATE category SET name = ?, username = ?, archived = ? WHERE id = ?",
                category.getName(), category.getUsername(), category.isArchived(), category.getId()
        );

        return category;
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));

        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM category WHERE id = ?",
                        CategoryEntityRowMapper.INSTANCE,
                        id
                )
        );
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));

        return Optional.ofNullable(
                jdbcTemplate.queryForObject("SELECT * FROM category WHERE username = ? AND name = ?",
                        CategoryEntityRowMapper.INSTANCE,
                        username,
                        categoryName
                )
        );
    }

    @Override
    public List<CategoryEntity> findAllByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));

        return jdbcTemplate.query(
                "SELECT * FROM category WHERE username = ?",
                CategoryEntityRowMapper.INSTANCE,
                username
        );
    }

    @Override
    public List<CategoryEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));

        return jdbcTemplate.query(
                "SELECT * FROM category",
                CategoryEntityRowMapper.INSTANCE
        );
    }

    @Override
    public void deleteCategory(CategoryEntity category) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));

        jdbcTemplate.update(
                "DELETE FROM category WHERE id = ?",
                category.getId());
    }
}
