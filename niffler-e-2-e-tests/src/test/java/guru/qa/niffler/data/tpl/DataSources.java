package guru.qa.niffler.data.tpl;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import org.apache.commons.lang3.StringUtils;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class  DataSources {
    private DataSources() {

    }

    private static final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();
    private static final Map<String, DataSource> testDataSources = new ConcurrentHashMap<>();

    public static DataSource dataSource(String jdbcUrl) {
        return dataSources.computeIfAbsent(
                jdbcUrl,
                key -> {
                    AtomikosDataSourceBean dsBean = new AtomikosDataSourceBean();
                    final String uniqueId = StringUtils.substringAfter(jdbcUrl, "5432/");
                    dsBean.setUniqueResourceName(uniqueId);
                    dsBean.setXaDataSourceClassName("org.postgresql.xa.PGXADataSource");
                    Properties props = new Properties();
                    props.put("URL", jdbcUrl);
                    props.put("user", "postgres");
                    props.put("password", "secret");
                    dsBean.setXaProperties(props);
                    dsBean.setPoolSize(3);
                    dsBean.setMaxPoolSize(10);
                    return dsBean;
                }
        );
    }

    //метод для получения DataSource, чтобы протестировать ChainedTransactionManager
    //вместо Atomikos
    public static DataSource testDataSource(String jdbcUrl) {
        return testDataSources.computeIfAbsent(
                jdbcUrl,
                key -> {
                    PGSimpleDataSource ds = new PGSimpleDataSource();
                    ds.setUser("postgres");
                    ds.setPassword("secret");
                    ds.setUrl(key);
                    return ds;
                }
        );
    }
}
