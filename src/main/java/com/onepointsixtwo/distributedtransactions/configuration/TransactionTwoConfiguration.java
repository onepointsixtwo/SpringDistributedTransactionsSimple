package com.onepointsixtwo.distributedtransactions.configuration;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.mysql.cj.jdbc.MysqlXADataSource;
import org.h2.jdbcx.JdbcDataSource;
import org.hibernate.engine.transaction.jta.platform.internal.AtomikosJtaPlatform;
import org.postgresql.xa.PGXADataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.util.HashMap;

/**
 * In JTA to set up transactions globally across multiple systems, you have to manually configure the data source as
 * a data source for usage with the particular JTA implementation - in this case Atomikos. The data source is setup,
 * and then managed by Atomikos to handle the transactions within it. It just has to be an XADataSource which means
 * a data source which can be managed by transactions.
 *
 * In the case of transaction two's data source, it uses mysql, and therefore uses the MysqlXAdataSource which is a
 * data source supporting transactions in MySQL.
 */
@Configuration
@DependsOn("transactionManager")
@EnableJpaRepositories(basePackages = "com.onepointsixtwo.distributedtransactions.repository.transactiontwo", entityManagerFactoryRef = "transactionTwoEntityManager")
public class TransactionTwoConfiguration {

    @Autowired
    @Qualifier("mysql")
    JpaVendorAdapter jpaVendorAdapter;

    @Bean(name = "transactionTwoDataSource")
    public DataSource transactionOneDataSource() {
        // TODO: Pull these values from a config file.
        MysqlXADataSource dataSource = new MysqlXADataSource();
        dataSource.setURL("jdbc:mysql://localhost:3306/test");
        dataSource.setUser("root");
        dataSource.setPassword("");

        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(dataSource);
        xaDataSource.setUniqueResourceName("xads2");
        return xaDataSource;
    }

    @Bean(name = "transactionTwoEntityManager")
    @DependsOn("transactionManager")
    public LocalContainerEntityManagerFactoryBean customerEntityManager() throws Throwable {

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.transaction.jta.platform", AtomikosJtaPlatform.class.getName());
        properties.put("javax.persistence.transactionType", "JTA");

        LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
        entityManager.setJtaDataSource(transactionOneDataSource());
        entityManager.setJpaVendorAdapter(jpaVendorAdapter);
        entityManager.setPackagesToScan("com.onepointsixtwo.distributedtransactions.model.transactiontwo");
        entityManager.setPersistenceUnitName("transactionTwoPersistenceUnit");
        entityManager.setJpaPropertyMap(properties);
        return entityManager;
    }
}
