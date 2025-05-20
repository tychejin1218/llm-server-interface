package com.wanted.assignment.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wanted.assignment.common.constants.Constants;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = MainDataSourceConfig.JPA_REPOSITORY_PACKAGES,
    entityManagerFactoryRef = MainDataSourceConfig.DATASOURCE_PREFIX + "EntityManagerFactory",
    transactionManagerRef = MainDataSourceConfig.DATASOURCE_PREFIX + "TransactionManager"
)
@EnableJpaAuditing
@Configuration
public class MainDataSourceConfig {

  public static final String DATASOURCE_PREFIX = "main";
  public static final String DATASOURCE_PROPERTY_PREFIX = "main.datasource";
  public static final String DATASOURCE_BEAN_NAME = "DataSource";

  public static final String[] JPA_ENTITY_PACKAGES = {Constants.BASE_PACKAGE + ".**.entity"};
  public static final String JPA_REPOSITORY_PACKAGES = Constants.BASE_PACKAGE + ".**.repository";

  /**
   * DataSource 설정 (HikariCP)
   *
   * @return DataSource 객체
   */
  @ConfigurationProperties(prefix = DATASOURCE_PROPERTY_PREFIX)
  @Bean(DATASOURCE_BEAN_NAME)
  public DataSource dataSource() {
    return DataSourceBuilder.create().type(HikariDataSource.class).build();
  }

  /**
   * 로컬 환경에서 데이터 초기화 실행
   *
   * @param dataSource 데이터 소스
   * @return DataSourceInitializer 객체
   */
  @Profile("local")
  @Bean
  public DataSourceInitializer dataSourceInitializer(
      @Qualifier(DATASOURCE_BEAN_NAME) DataSource dataSource) {
    ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
    databasePopulator.addScript(new ClassPathResource("script/schema.sql"));
    databasePopulator.addScript(new ClassPathResource("script/data.sql"));

    DataSourceInitializer initializer = new DataSourceInitializer();
    initializer.setDataSource(dataSource);
    initializer.setDatabasePopulator(databasePopulator);

    return initializer;
  }

  /**
   * JPA 관련 속성 설정
   *
   * @return JpaProperties 객체
   */
  @Primary
  @Bean(DATASOURCE_PREFIX + "JpaProperties")
  @ConfigurationProperties(prefix = DATASOURCE_PREFIX + ".jpa")
  public JpaProperties jpaProperties() {
    return new JpaProperties();
  }

  /**
   * EntityManagerFactory 설정
   *
   * @param dataSource    데이터 소스
   * @param jpaProperties JPA 관련 속성
   * @return LocalContainerEntityManagerFactoryBean 객체
   */
  @Bean(DATASOURCE_PREFIX + "EntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      @Qualifier(DATASOURCE_BEAN_NAME) DataSource dataSource,
      @Qualifier(DATASOURCE_PREFIX + "JpaProperties") JpaProperties jpaProperties) {
    return this.entityManagerFactoryBuilder(jpaProperties)
        .dataSource(dataSource)
        .packages(JPA_ENTITY_PACKAGES)
        .properties(jpaProperties.getProperties())
        .persistenceUnit(DATASOURCE_PREFIX + "EntityManager")
        .build();
  }

  /**
   * JPA 설정을 위한 EntityManagerFactoryBuilder 빌더 설정
   *
   * @param jpaProperties JPA 속성
   * @return EntityManagerFactoryBuilder 객체
   */
  private EntityManagerFactoryBuilder entityManagerFactoryBuilder(JpaProperties jpaProperties) {
    HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
    jpaVendorAdapter.setGenerateDdl(jpaProperties.isGenerateDdl());
    jpaVendorAdapter.setShowSql(jpaProperties.isShowSql());
    jpaVendorAdapter.setDatabasePlatform(jpaProperties.getDatabasePlatform());
    return new EntityManagerFactoryBuilder(
        jpaVendorAdapter,
        dataSource -> jpaProperties.getProperties(),
        null,
        null
    );
  }

  /**
   * 트랜잭션 매니저 설정
   *
   * @param entityManagerFactory 엔티티 매니저 팩토리
   * @return PlatformTransactionManager 객체
   */
  @Bean(DATASOURCE_PREFIX + "TransactionManager")
  public PlatformTransactionManager platformTransactionManager(
      @Qualifier(DATASOURCE_PREFIX + "EntityManagerFactory")
      LocalContainerEntityManagerFactoryBean entityManagerFactory
  ) {
    JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
    jpaTransactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
    return jpaTransactionManager;
  }

  /**
   * JdbcTemplate 설정
   *
   * @param dataSource 데이터 소스
   * @return JdbcTemplate 객체
   */
  @Bean(name = DATASOURCE_PREFIX + "JdbcTemplate")
  public JdbcTemplate jdbcTemplate(@Qualifier(DATASOURCE_BEAN_NAME) DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }

  /**
   * QueryDSL 설정
   */
  @Configuration
  static class QuerydslConfig {

    @PersistenceContext(unitName = DATASOURCE_PREFIX + "EntityManager")
    private EntityManager mainEntityManager;

    /**
     * JPAQueryFactory 설정
     *
     * @return JPAQueryFactory 객체
     */
    @Bean
    public JPAQueryFactory mainJpaQueryFactory() {
      return new JPAQueryFactory(mainEntityManager);
    }
  }
}
