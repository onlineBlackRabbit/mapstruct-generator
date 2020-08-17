package com.zl.generator.utils;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;

public class MapStructCodeGenUtil {
    public void scanAndGenerate(Map<String, String> config) throws Exception {
        String doPath = config.get("doPath");
        String dtoPath = config.get("dtoPath");
        String voPath = config.get("voPath");

        List<Class<?>> doClasses = ClassUtil.findClasses(doPath);
        List<Class<?>> dtoClasses = ClassUtil.findClasses(dtoPath);
        List<Class<?>> voClasses = ClassUtil.findClasses(voPath);

        Set<String> doNames = getNames(doClasses);
        Set<String> dtoNames = getNames(dtoClasses);
        Set<String> voNames = getNames(voClasses);

        List<Map<String, String>> all = getAllNames(doNames, dtoNames, voNames);

        for (Map<String, String> map : all) {
            System.out.println(map.get("do") + "," + map.get("name") + "," + map.get("dto") + "," + map.get("vo"));
            Map<String, String> ctx = new HashMap<>();
            ctx.put("package", config.get("package"));
            ctx.put("className", map.get("name"));
            ctx.put("override", config.get("override"));

            if (map.get("do") != null && map.get("do").length() > 0) {
                ctx.put("do", doPath + "." + map.get("do"));
                ctx.put("doName", map.get("do"));
            }

            if (map.get("dto") != null && map.get("dto").length() > 0) {
                ctx.put("dto", dtoPath + "." + map.get("dto"));
                ctx.put("dtoName", map.get("dto"));
            }

            if (map.get("vo") != null && map.get("vo").length() > 0) {
                ctx.put("vo", voPath + "." + map.get("vo"));
                ctx.put("voName", map.get("vo"));
            }
            generate(ctx);
        }
    }

    private List<Map<String, String>> getAllNames(Set<String> doNames, Set<String> dtoNames, Set<String> voNames) {
        List<Map<String, String>> tmp = new ArrayList<>();
        for (String doName : doNames) {
            tmp.add(getOneName(doName, dtoNames, voNames));
        }
        return tmp;
    }

    private Map<String, String> getOneName(String doName, Set<String> dtoNames, Set<String> voNames) {
        Map<String, String> one = new HashMap<>();
        String keyName = getName(doName);
        one.put("do", doName);
        one.put("name", keyName);
        one.put("dto", getDtoOrVoName(keyName, dtoNames, "dto"));
        one.put("vo", getDtoOrVoName(keyName, voNames, "vo"));
        return one;
    }

    private String getDtoOrVoName(String keyName, Set<String> dtoNames, String type) {
        for (String dtoName : dtoNames) {
            String tmp = keyName.toLowerCase() + type;
            if (tmp.equals(dtoName.toLowerCase())) {
                return dtoName;
            }
        }
        return null;
    }

    private String getName(String doName) {
        String entityTail = "entity";
        String doTail = "do";
        String tmpLower = doName.toLowerCase();
        if (tmpLower.endsWith(entityTail)) {
            return doName.substring(0, doName.length() - entityTail.length());
        }
        if (tmpLower.endsWith(doTail)) {
            return doName.substring(0, doName.length() - doTail.length());
        }
        return doName;
    }

    private Set<String> getNames(List<Class<?>> classes) {
        Set<String> names = new HashSet<>();
        for (Class<?> clz : classes) {
            String fullName = clz.getName();
            int last = fullName.lastIndexOf(".");
            String tmp = fullName.substring(last + 1);
            names.add(tmp);
        }
        return names;
    }


    /**
     * 生成文件
     *
     * @throws IOException
     */
    public void generate(Map<String, String> context) throws IOException {
        String separator = Matcher.quoteReplacement(File.separator);
        // 初始化模板引擎
        VelocityEngine ve = initVelocityEngine();
        // 获取模板文件
        Template t = ve.getTemplate(separator + "templates" + separator + "MapStructConverter.vm", "UTF-8");
//        Template t = ve.getTemplate(separator + "MapStructConverter.vm", "UTF-8");
        // 设置变量
        VelocityContext ctx = setVelocityContext(context);
        // 输出
        write(t, ctx, separator, context);
    }

    /**
     * 初始化模板引擎
     *
     * @return
     */
    private VelocityEngine initVelocityEngine() {
        Properties p = new Properties();
        URL location = MapStructCodeGenUtil.class.getProtectionDomain().getCodeSource().getLocation();//获得当前的URL
        File file2 = new File(location.getPath());//构建指向当前URL的文件描述符
        String root = file2.getAbsolutePath();
        String path = "jar:file:/" + root;
        System.out.println("jar path:" + path);
        //设置velocity资源加载方式为jar
        p.setProperty("resource.loader", "jar");
        //设置velocity资源加载方式为file时的处理类
        p.setProperty("jar.resource.loader.class", "org.apache.velocity.runtime.resource.loader.JarResourceLoader");
        //设置jar包所在的位置
        p.setProperty("jar.resource.loader.path", path);
        p.put("input.encoding", "UTF-8");
        p.put("output.encoding", "UTF-8");

        VelocityEngine ve = new VelocityEngine(p);
        ve.init();
        return ve;
    }

    /**
     * 设置变量
     *
     * @return
     */
    private VelocityContext setVelocityContext(Map<String, String> context) {
        VelocityContext ctx = new VelocityContext();

        ctx.put("package", context.get("package"));
        ctx.put("className", context.get("className") + "Converter");

        ctx.put("do", context.get("do"));
        ctx.put("dto", context.get("dto"));
        ctx.put("vo", context.get("vo"));

        ctx.put("doName", context.get("doName"));
        ctx.put("dtoName", context.get("dtoName"));
        ctx.put("voName", context.get("voName"));

        String clone = context.get("clone");
        if(clone != null && clone.equals("true")){
            ctx.put("clone", clone);
        }

        return ctx;
    }

    /**
     * 输出
     *
     * @throws IOException
     */
    private void write(Template t, VelocityContext ctx, String separator, Map<String, String> context) throws IOException {
        String packageName = context.get("package");//类包
        String targetFile = context.get("className") + "Converter.java";
        String templateDir = templateDir(separator);
        String sourcePath = System.getProperty("user.dir") + templateDir;
        String resultDir = resultDir(separator);
        String targetPath = targetPath(resultDir, packageName, separator, context);


        System.out.println("resultDir:" + resultDir);
        System.out.println("targetPath:" + targetPath);

        String override = context.get("override");
        File file = new File(targetPath, targetFile);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists() || !isNotOverride(override)) {
            file.createNewFile();
        } else {
            return;
        }

        FileOutputStream outStream = new FileOutputStream(file);
        OutputStreamWriter writer = new OutputStreamWriter(outStream,
                "UTF-8");
        BufferedWriter sw = new BufferedWriter(writer);
        t.merge(ctx, sw);
        sw.flush();
        sw.close();
        outStream.close();
        System.out.println("成功生成Java文件:"
                + (targetPath + targetFile).replaceAll("/", separator));
    }

    private String templateDir(String separator) {
        return ".src.main.resource.templates.".replaceAll(".", separator);
    }

    private String resultDir(String separator) {
        return separator + "src" + separator + "main" + separator + "java";
    }

    private String targetPath(String resultDir, String packageName, String separator, Map<String, String> context) {
        String moduleName = context.get("moduleName");

        if (moduleName == null || moduleName.length() <= 0) {
            return System.getProperty("user.dir")
                    + resultDir + separator
                    + packageName.replace(".", separator);
        } else {
            return System.getProperty("user.dir")
                    + separator + moduleName
                    + resultDir + separator
                    + packageName.replace(".", separator);
        }

    }

    private boolean isNotOverride(String override) {
        return override == null || override.length() <= 0 || override.equals("false");
    }
}
