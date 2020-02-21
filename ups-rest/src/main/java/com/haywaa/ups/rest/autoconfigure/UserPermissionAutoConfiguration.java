package com.haywaa.ups.rest.autoconfigure;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.zaxxer.hikari.HikariDataSource;

/**
 * @description
 * @author: haywaa
 * @create: 2019-12-03 14:23
 */
@MapperScan(basePackages =
        {
                "com.haywaa.ups.dao"
        }, sqlSessionTemplateRef  = "userPermissionSqlSessionTemplate")
@Configurable
@ComponentScan(basePackages = "com.haywaa.ups")
public class UserPermissionAutoConfiguration {

    @Bean("userPermissionDatabase")
    @ConfigurationProperties("datasource.ups")
    @ConditionalOnProperty("datasource.ups.url")
    public DataSource userPermissionDatabase(
            @Value("${datasource.ups.url}") String url,
            @Value("${datasource.ups.username}") String username,
            @Value("${datasource.ups.password}") String password

    ){
        HikariDataSource dataSource = new HikariDataSource();
        //        dataSource.setDriverClassName(this.driverClassName);
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean(name = "userPermissionTransactionManager")
    public PlatformTransactionManager dataMigrationTransactionManager(@Qualifier("userPermissionDatabase") DataSource dataMigrationDatabase) {
        return new DataSourceTransactionManager(dataMigrationDatabase);
    }

    @Bean(name = "userPermissionTransactionTemplate")
    public TransactionTemplate dataMigrationTransactionTemplate(@Qualifier("userPermissionTransactionManager") PlatformTransactionManager dataMigrationTransactionManager) {
        return new TransactionTemplate(dataMigrationTransactionManager);
    }

    @Bean(name = "userPermissionSqlSessionFactory")
    public SqlSessionFactory dataMigrationSqlSessionFactory(@Qualifier("userPermissionDatabase") DataSource dataMigrationDatabase) throws Exception {
        org.springframework.core.io.Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath*:com/haywaa/ups/dao/*.xml");
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataMigrationDatabase);
        bean.setMapperLocations(resources);
        return bean.getObject();
    }

    @Bean(name = "userPermissionSqlSessionTemplate")
    public SqlSessionTemplate testSqlSessionTemplate(@Qualifier("userPermissionSqlSessionFactory") SqlSessionFactory dataMigrationSqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(dataMigrationSqlSessionFactory);
    }
}
