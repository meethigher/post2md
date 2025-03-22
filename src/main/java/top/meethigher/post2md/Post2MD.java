package top.meethigher.post2md;

import ws.vinta.pangu.Pangu;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Post2MD {

    /**
     * 匹配字符串 "asset_img" 后面跟着一个或多个空格，然后是一个或多个非空格字符，最后是一个或多个空格字符。
     */
    private static Pattern imgPattern = Pattern.compile("asset_img\\s+(\\S+)\\s+");
    /**
     * 匹配4个数值
     */
    private static Pattern datePattern = Pattern.compile("\\d{4}");

    private final String imageUrlTemplate;
    private final String blogUrlTemplate;
    private final Pangu pangu = new Pangu();

    public Post2MD(String imageUrlTemplate, String blogUrlTemplate) {
        this.imageUrlTemplate = imageUrlTemplate;
        this.blogUrlTemplate = blogUrlTemplate;
    }

    public Post2MD() {
        this.imageUrlTemplate = "![](https://meethigher.top/blog/{createYear}/{mdName}/{imageName})";
        this.blogUrlTemplate = "https://meethigher.top/blog/{createYear}/{mdName}/";
    }

    public void prettify(String sourceFilePath) throws Exception {
        File file = new File(sourceFilePath);
        String originFileName = file.getName();
        try (BufferedReader reader = new BufferedReader(new FileReader(file)); BufferedWriter writer = new BufferedWriter(new FileWriter(originFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String tLine = line;
                tLine = pangu.spacingText(tLine);
                writer.write(tLine);
                writer.newLine();
                writer.flush();
            }
        }
    }

    public void mdImgLink2HexoImgLink(String sourceFilePath) throws Exception {
        File file = new File(sourceFilePath);
        String originFileName = file.getName();
        try (BufferedReader reader = new BufferedReader(new FileReader(file)); BufferedWriter writer = new BufferedWriter(new FileWriter(originFileName))) {
            String line;
            //true表示当前文件读取游标已经处于正文中了
            boolean reachedContent = false;
            while ((line = reader.readLine()) != null) {
                String tLine = line;
                if (isMatchingMdImgLink(line)) {
                    String s = extractImg(tLine);
                    if (s != null) {
                        tLine = "{% asset_img " + s + " %}";
                    }
                }
                writer.write(tLine);
                writer.newLine();
                writer.flush();
            }
        }

    }

    /**
     * 将源文件转换成以title命名的.md文件
     *
     * @param sourceFilePath 原文件地址
     */
    public void assetImg2UrlImg(String sourceFilePath) throws Exception {
        File file = new File(sourceFilePath);
        String originFileName = file.getName();
        String mdName = originFileName.replace(".md", "");
        String createYear = "", blogTitle = "", createTime = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            BufferedWriter writer = null;
            String line;
            //true表示当前文件读取游标已经处于正文中了
            boolean reachedContent = false;
            while ((line = reader.readLine()) != null) {
                /**
                 * 如果到了正文中，就需要进行判定操作，否则原封不动的继续输出就可以了
                 * 如果未到正文中，就进行基础信息的截取
                 */
                if (reachedContent) {
                    if (line.contains("asset_img")) {
                        Matcher matcher = imgPattern.matcher(line);
                        while (matcher.find()) {
                            String imageName = matcher.group(1);
                            String imageUrl = imageUrlTemplate.replace("{createYear}", createYear)
                                    .replace("{mdName}", mdName)
                                    .replace("{imageName}", imageName);
                            writer.write(imageUrl);
                            writer.newLine();
                        }
                    } else {
                        writer.write(line, 0, line.length());
                        writer.newLine();
                    }
                } else {
                    /**
                     * 判定正文前内容，如标题、创建时间等等
                     */
                    if ("<!--more-->".equals(line)) {
                        reachedContent = true;
                        // 添加一行说明
                        String template = "注意： 本文内容于 {createTime} 创建，可能不会在此平台上进行更新。如果您希望查看最新版本或更多相关内容，请访问原文地址：[{blogTitle}]({blogUrl})。感谢您的关注与支持！";
                        String description = template.replace("{createTime}", createTime)
                                .replace("{blogTitle}", blogTitle)
                                .replace("{blogUrl}", blogUrlTemplate.replace("{createYear}", createYear).replace("{mdName}", mdName));
                        writer.write(description);
                        writer.newLine();
                    } else if (line.contains("date: ")) {
                        // 截取创建时间
                        createYear = extractYear(line);
                        createTime = line.replace("date: ", "");
                    } else if (line.contains("title: ")) {
                        // 截取博客标题
                        blogTitle = line.replace("title: ", "");
                        writer = new BufferedWriter(new FileWriter(String.format(System.getProperty("user.dir").replace("\\", "/") + "/%s.md", blogTitle)));
                    }
                }
            }
            if (writer != null) {
                writer.close();
            }
        }

    }


    private String extractYear(String input) {
        // 定义正则表达式，匹配四位数字年份
        String regex = "\\b(\\d{4})\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            // 返回匹配到的年份
            return matcher.group(1);
        }
        return null;
    }

    private String extractImg(String input) {
        String regex = getExtractImgRegex();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        // 如果匹配成功，提取内容
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null; // 如果没有匹配到，返回null
    }

    private static String getExtractImgRegex() {
        // 定义正则表达式，匹配()内最后一个/后的内容
        return "!\\[.*?\\]\\(.*/([^/]+)\\)";
    }

    private boolean isMatchingMdImgLink(String input) {
        String regex = getExtractImgRegex();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }
}
