package com.zben.eshop.cache.ha;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import com.zben.eshop.cache.ha.filter.HystrixRequestContextFilter;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@SpringBootApplication
@MapperScan("com.zben.eshop.cache.ha.mapper")
public class CacheHaApplication {

	@Bean
	@ConfigurationProperties(prefix="spring.datasource")
	public DataSource dataSource() {
		return new org.apache.tomcat.jdbc.pool.DataSource();
	}

	@Bean
	public SqlSessionFactory sqlSessionFactoryBean() throws Exception {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource());
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:/mybatis/*.xml"));
		return sqlSessionFactoryBean.getObject();
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		return new DataSourceTransactionManager(dataSource());
	}

	@Bean
	public FilterRegistrationBean filterRegistrationBean() {
		FilterRegistrationBean bean =
				new FilterRegistrationBean(new HystrixRequestContextFilter());
		bean.addUrlPatterns("/*");
		return bean;
	}

	@Bean
	public ServletRegistrationBean indexServletRegistration() {
		ServletRegistrationBean registration = new ServletRegistrationBean(new HystrixMetricsStreamServlet());
		registration.addUrlMappings("/hystrix.stream");
		return registration;
	}

	public static void main(String[] args) {
		SpringApplication.run(CacheHaApplication.class, args);
	}

}
