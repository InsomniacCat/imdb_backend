package com.imdb.backend.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

//@Converter该类为JPA属性转换器，告诉JPA框架在持久化和检索过程中使用该转换器。
//attribute.stream()是Stream API的一部分，作用是将Java中的List<String>集合转换为一个流(Stream)，以便进行一系列的函数式操作。

/**
 * JPA属性转换器，用于在Java的List<String>和数据库的逗号分隔字符串之间进行转换
 */
@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    // 定义分隔符，用于在数据库中分隔列表元素
    private static final String SEP = ",";

    // IMDB数据中表示NULL的特殊值
    private static final String NULL_REPRESENTATION = "\\N";

    /**
     * 将Java中的List<String>转换为数据库列值（逗号分隔的字符串）
     */
    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        // 如果传入的列表为null或空，直接返回null，不在数据库中存储空字符串
        if (attribute == null || attribute.isEmpty()) return null;
        
        // 将列表中的每个元素去除首尾空格后，用逗号连接成一个字符串
        return attribute.stream()
                .map(String::trim)  // 去除每个元素的首尾空格
                .map(s -> s.equals("\\N") ? "" : s)  // 处理特殊NULL值
                .collect(Collectors.joining(SEP));  // 使用逗号连接所有元素
    }

    /**
     * 将数据库列值（逗号分隔的字符串）转换为Java中的List<String>
     */
    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        // 如果数据库中的值为null或特殊NULL表示，则返回一个空的列表
        if (dbData == null || dbData.equals(NULL_REPRESENTATION)) return Collections.emptyList();
        
        // 将逗号分隔的字符串拆分为数组，然后转换为列表
        return Arrays.stream(dbData.split(SEP))  // 按逗号分割字符串
                .map(String::trim)  // 去除每个元素的首尾空格
                .filter(s -> !s.isEmpty() && !s.equals(NULL_REPRESENTATION))  // 过滤掉空字符串和NULL表示
                .collect(Collectors.toList());  // 收集为List<String>
    }
}