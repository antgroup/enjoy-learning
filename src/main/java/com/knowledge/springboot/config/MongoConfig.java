package com.knowledge.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Arrays;

/**
 * MongoDB配置类
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.knowledge.springboot.repository")
@EnableMongoAuditing
public class MongoConfig {

    /**
     * 配置MongoDB事务管理器
     * 注意：事务支持需要MongoDB 4.0以上版本，且必须是副本集
     */
    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory factory) {
        return new MongoTransactionManager(factory);
    }
    
    /**
     * 自定义MongoDB转换器，禁用_class字段并添加Integer转换器
     */
    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory factory, MongoMappingContext context) {
        DefaultDbRefResolver resolver = new DefaultDbRefResolver(factory);
        MappingMongoConverter converter = new MappingMongoConverter(resolver, context);
        
        // 使用空的类型映射器禁用_class字段
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        
        // 添加自定义转换器
        converter.setCustomConversions(mongoCustomConversions());
        
        return converter;
    }
    
    /**
     * 自定义转换器配置
     */
    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(Arrays.asList(
            new IntegerToIntegerConverter(),
            new IntegerFromIntegerConverter()
        ));
    }
    
    /**
     * Integer写入转换器 - 确保Integer类型写入为int32
     */
    @WritingConverter
    public static class IntegerToIntegerConverter implements Converter<Integer, Integer> {
        @Override
        public Integer convert(Integer source) {
            return source;
        }
    }
    
    /**
     * Integer读取转换器 - 确保从MongoDB读取为Integer类型
     */
    @ReadingConverter
    public static class IntegerFromIntegerConverter implements Converter<Integer, Integer> {
        @Override
        public Integer convert(Integer source) {
            return source;
        }
    }
}
