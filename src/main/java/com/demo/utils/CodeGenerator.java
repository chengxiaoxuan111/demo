package com.demo.utils;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.fill.Column;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author 程啸轩
 * @date 2022/8/26 16:09
 */
public class CodeGenerator {

    // 数据库URL，注意将URL改成你自己对应的数据库名称
    private final static String URL = "jdbc:mysql://127.0.0.1:3306/schema_name?useSSL=false&useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true";
  //数据库用户名
    private final static String USER_NAME = "root";
  //数据库密码
    private final static String PASSWORD = "root";
  //注意将这个参数改成你自己项目的目录
    private final static String OUT_PUT_DIR = "/Users/apple/Downloads/wechat/src/main/java/com/demo/utils/CodeGenerator.java";


    public static void main(String[] args) {
    FastAutoGenerator.create(URL, USER_NAME, PASSWORD)
        // 全局配置
         .globalConfig((scanner, builder) -> builder.author(scanner.apply("请输入作者名称？")).fileOverride()
                            .outputDir(OUT_PUT_DIR)
             .fileOverride())
        // 包配置
         .packageConfig((scanner, builder) -> builder.parent(scanner.apply("请输入包名？")))
        // 策略配置
         .strategyConfig((scanner, builder) -> builder.addInclude(getTables(scanner.apply("请输入表名，多个英文逗号分隔？所有输入 all")))
                            .addTablePrefix("t_")
             .controllerBuilder().enableRestStyle().enableHyphenStyle()
             .entityBuilder().enableLombok().addTableFills(
                               new Column("create_time", FieldFill.INSERT)
             ).build())
        /*
          模板引擎配置，默认 Velocity 可选模板引擎 Beetl 或 Freemarker
          .templateEngine(new BeetlTemplateEngine())
          .templateEngine(new FreemarkerTemplateEngine())
         */
         .execute();

   }

     // 处理 all 情况
              protected static List<String> getTables(String tables) {
    return "all".equals(tables) ? Collections.emptyList() : Arrays.asList(tables.split(","));
   }
    
}
