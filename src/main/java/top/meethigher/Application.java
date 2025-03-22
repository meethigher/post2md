package top.meethigher;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import top.meethigher.post2md.Post2MD;

/**
 * @author chenchuancheng github.com/meethigher
 * @since 2023/4/29 20:22
 */
public class Application {

    @Parameter(names = "-s", required = true, description = "源文件")
    private String source;

    /**
     * true时，将正常的markdown图片格式，转为hexo的asset_img格式
     * false时，将hexo的asset_img格式，转为正常的markdown图片格式
     */
    @Parameter(names = "--c", required = false, description = "将markdown图片链接，转为hexo的asset_img链接")
    private boolean convert;

    /**
     * 文章美化，主要检测中英文排版，添加空格
     */
    @Parameter(names = "--p", required = false, description = "将文章进行美化")
    private boolean prettify;

    @Parameter(names = "--help", help = true)
    private boolean help;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isHelp() {
        return help;
    }

    public boolean isPrettify() {
        return prettify;
    }

    public void setPrettify(boolean prettify) {
        this.prettify = prettify;
    }

    public void setHelp(boolean help) {
        this.help = help;
    }

    public boolean isConvert() {
        return convert;
    }

    public void setConvert(boolean convert) {
        this.convert = convert;
    }

    public static void main(String... args) throws Exception {
        Application app = new Application();
        JCommander jCommander = JCommander.newBuilder()
                .addObject(app)
                .build();
        jCommander.parse(args);
        if (app.isHelp()) {
            jCommander.usage();
            return;
        }
        Post2MD post2MD = new Post2MD();
        if (app.isPrettify()) {
            post2MD.prettify(app.getSource());
            return;
        }
        if (app.isConvert()) {
            post2MD.mdImgLink2HexoImgLink(app.getSource());
        } else {
            post2MD.assetImg2UrlImg(app.getSource());
        }


    }
}
