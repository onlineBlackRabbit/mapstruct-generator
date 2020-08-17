package com.zl.generator.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Example {

    public static void main(String[] args) throws Exception {
        MapStructCodeGenUtil generator = new MapStructCodeGenUtil();

        generate(generator);

        scanAndGenerate(generator);
    }

    private static void generate(MapStructCodeGenUtil generator) throws IOException {
        Map<String, String> config = new HashMap<>();
        config.put("package", "com.example.car");
        config.put("className", "CarMapper");

        config.put("do", "com.example.model.Car");
        config.put("dto", "com.example.model.CarDto");
        config.put("vo", "com.example.test");

        config.put("doName", "Car");
        config.put("dtoName", "CarDto");
        config.put("voName", "CarDto");
        config.put("moduleName", "common");
        config.put("clone", "true");
        generator.generate(config);
    }

    private static void scanAndGenerate(MapStructCodeGenUtil generator) throws Exception {
        Map<String, String> config = new HashMap<>();
        config.put("doPath", "com.example.model");
        config.put("dtoPath", "com.example.model");
        config.put("voPath", "com.example.model");
        config.put("package", "com.example");
        config.put("override", "true");
        config.put("moduleName", "common");
        config.put("clone", "true");
        generator.scanAndGenerate(config);
    }
}
