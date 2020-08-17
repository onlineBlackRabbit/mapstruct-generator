# mapstruct-generator

#### 介绍
mapstruct转换工具接口代码生成工具

#### 软件架构
软件架构说明


#### 安装教程

1.  xxxx
2.  xxxx
3.  xxxx

#### 使用说明

1. 引入相关依赖

   ```xml
   <dependency>
       <groupId>org.mapstruct</groupId>
       <artifactId>mapstruct-jdk8</artifactId>
       <version>1.3.1.Final</version>
   </dependency>
   
   <dependency>
       <groupId>org.mapstruct</groupId>
       <artifactId>mapstruct-processor</artifactId>
       <version>1.3.1.Final</version>
   </dependency>
   
   <dependency>
       <groupId>org.apache.velocity</groupId>
       <artifactId>velocity</artifactId>
       <version>1.7</version>
   </dependency>
   
   <dependency>
       <groupId>com.alibaba</groupId>
       <artifactId>fastjson</artifactId>
       <version>1.2.73</version>
   </dependency>
   ```

   

2. 下载jar包（https://gitee.com/Losisco/mapstruct-generator/releases）

3. jar包放在resources/lib/目录下

4. 引入jar包

   ```xml
   <dependency>
       <groupId>com.zl</groupId>
       <artifactId>mapstruct-generator</artifactId>
       <version>1.0</version>
       <scope>system</scope>
       <systemPath>${project.basedir}/src/main/resources/lib/mapstruct-generator-1.0.jar</systemPath>
   </dependency>
   ```

5. 生成代码

   1）只生成一个

   ```java
   MapStructCodeGenUtil generator = new MapStructCodeGenUtil();
   Map<String, String> config = new HashMap<>();
   config.put("package", "com.example.car");//生成代码目录
   config.put("className", "CarMapper");//生成代码文件名称与Converter拼接
   
   config.put("do", "com.example.model.Car");//读取的do类
   config.put("dto", "com.example.model.CarDto");//读取的dto类
   config.put("vo", "com.example.test");//读取的vo类
   
   config.put("doName", "Car");//do名称
   config.put("dtoName", "CarDto");//dto名称
   config.put("voName", "CarDto");//vo名称
   config.put("override", "true");//是否覆盖已经存在的文件，默认不覆盖
   config.put("clone", "true");//是否生成clone方法
   generator.generate(config);
   ```

   2）批量生成

   ```java
   MapStructCodeGenUtil generator = new MapStructCodeGenUtil();
   Map<String, String> config = new HashMap<>();
   config.put("doPath", "com.example.model");//do所在包路径
   config.put("dtoPath", "com.example.model");//dto所在包路径
   config.put("voPath", "com.example.model");//vo所在包路径
   config.put("package", "com.example");//生成代码所在包路径
   config.put("override", "true");//是否覆盖已经存在的文件，默认不覆盖
   config.put("clone", "true");//是否生成clone方法
   generator.scanAndGenerate(config);
   ```

6.  生成代码结果

    ```java
    package com.example.car;
    
    import com.example.model.Car;
    import com.example.model.CarDto;
    import com.example.model.CarVo;
    import org.mapstruct.Mapper;
    import org.mapstruct.NullValuePropertyMappingStrategy;
    import org.mapstruct.factory.Mappers;
    
    /**
     * @author Losisco
     */
    @Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public interface CarConverter {
        CarConverter INSTANCE = Mappers.getMapper(CarConverter.class);
    
        CarDto do2dto(Car doObj);
    
        Car dto2do(CarDto dtoObj);
    
        CarDto vo2dto(CarVo voObj);
    
        CarVo dto2vo(CarDto dtoObj);
    }
    ```

    

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 码云特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  码云官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解码云上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是码云最有价值开源项目，是码云综合评定出的优秀开源项目
5.  码云官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  码云封面人物是一档用来展示码云会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
