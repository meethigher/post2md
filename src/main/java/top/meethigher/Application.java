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

    @Parameter(names = "--c", required = false, description = "开启md语法图片转为hexo语法")
    private boolean convert;

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
        if (app.isConvert()) {
            post2MD.mdImgLink2HexoImgLink(app.getSource());
        } else {
            post2MD.assetImg2UrlImg(app.getSource());
        }

    }
}
