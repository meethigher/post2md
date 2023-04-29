package top.meethigher.post2md;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Post2MD {

    private static String template = "![](https://meethigher.top/blog/%s/%s/%s)";
    private static String target = System.getProperty("user.dir").replace("\\", "/") + "/%s";
    private static boolean write = false;
    private static String year;

    /**
     * {% asset_img a.png %}
     * <p>
     * {% asset_img b.png 你好 %}
     */
    //匹配字符串 "asset_img" 后面跟着一个或多个空格，然后是一个或多个非空格字符，最后是一个或多个空格字符。然后提取其中的非空格字符
    private static Pattern imgPattern = Pattern.compile("asset_img\\s+(\\S+)\\s+");
    private static Pattern datePattern = Pattern.compile("\\d{4}");
    private static String fileName;

    public static void convert(String source) throws Exception {
        File file = new File(source);
        String originFileName = file.getName();
        fileName = originFileName.replace(".md", "");
        try (BufferedReader reader = new BufferedReader(new FileReader(file));
             BufferedWriter writer = new BufferedWriter(new FileWriter(String.format(target, originFileName)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (write) {
                    if (line.contains("asset_img")) {
                        Matcher matcher = imgPattern.matcher(line);
                        while (matcher.find()) {
                            String imageName = matcher.group(1);
                            writer.write(String.format(template, year, fileName, imageName));
                            writer.newLine();
                        }
                    } else {
                        writer.write(line, 0, line.length());
                        writer.newLine();
                    }
                }
                if (line.equals("<!--more-->")) {
                    write = true;
                } else if (line.contains("date: ")) {
                    Matcher matcher = datePattern.matcher(line);
                    if (matcher.find()) {
                        year = matcher.group();
                    }
                }
            }
        }
    }

}
