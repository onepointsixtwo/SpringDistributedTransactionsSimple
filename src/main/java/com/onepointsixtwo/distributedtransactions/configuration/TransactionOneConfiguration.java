package com.onepointsixtwo.distributedtransactions.configuration;

import com.atomikos.jdbc.AtomikosDataSourceBean;
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
 * In the case of transaction one's data source, the backend is implemented in PostgreSQL, and the driver that is used
 * is a PGXADataSource.
 */
@Configuration
@DependsOn("transactionManager")
@EnableJpaRepositories(basePackages = "com.onepointsixtwo.distributedtransactions.repository.transactionone", entityManagerFactoryRef = "transactionOneEntityManager", transactionManagerRef = "transactionManager")
public class TransactionOneConfiguration {

    @Autowired
    @Qualifier("postgres")
    JpaVendorAdapter jpaVendorAdapter;

    @Bean(name = "transactionOneDataSource")
    public DataSource transactionOneDataSource() {
        PGXADataSource ds = new PGXADataSource();
        // TODO: Inject these as configuration properties
        ds.setURL("jdbc:postgresql://localhost:5432/johnkartupelis");
        ds.setUser("johnkartupelis");
        ds.setPassword("");

        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(ds);
        xaDataSource.setUniqueResourceName("xads1");
        return xaDataSource;
    }

    @Bean(name = "transactionOneEntityManager")
    @DependsOn("transactionManager")
    public LocalContainerEntityManagerFactoryBean customerEntityManager() throws Throwable {

        HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put("hibernate.transaction.jta.platform", AtomikosJtaPlatform.class.getName());
        properties.put("javax.persistence.transactionType", "JTA");

        LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
        entityManager.setJtaDataSource(transactionOneDataSource());
        entityManager.setJpaVendorAdapter(jpaVendorAdapter);
        entityManager.setPackagesToScan("com.onepointsixtwo.distributedtransactions.model.transactionone");
        entityManager.setPersistenceUnitName("transactionOnePersintenceUnit");
        entityManager.setJpaPropertyMap(properties);
        return entityManager;
    }
}
