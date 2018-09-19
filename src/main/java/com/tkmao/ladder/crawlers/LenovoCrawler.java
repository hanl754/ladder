package com.tkmao.ladder.crawlers;

import com.tkmao.ladder.AutoRunnableCrawler;
import com.tkmao.ladder.data.Notebook;
import com.tkmao.ladder.service.DumpService;
import com.tkmao.ladder.service.impl.NoteBookDumpService;
import com.tkmao.ladder.util.ApplicationContextHolder;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.parser.ParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * Description: 一定不要@Autowired,@Resource之类的自动注入，这不是spring管理的，是AutoRunnableCrawler中init时候new出来的。
 * 开多少个线程就会new多少个对应的Crawler
 * @author hanliang
 * @time 2018/9/8 下午11:47
 */
@Slf4j
@Service
public class LenovoCrawler extends AutoRunnableCrawler<Notebook> {
    private static final Pattern suffixPattern = Pattern.compile(".+\\d+\\.html");

    private NoteBookDumpService noteBookDumpService;

    @Override
    public boolean shouldVisit(String url) {
        return urlPrefix().matcher(url).matches();
    }

    @Override
    public void addSeed(CrawlController controller) {
        controller.addSeed("https://s.lenovo.com.cn/?key=&destination=&index=293&frompage=home");
    }

    @Override
    public Notebook handle(Document $) {
        Notebook notebook = new Notebook();
        String name = $.select("#span_product_name").text();
        String diskSize = "";
        String cpuModel = "";
        String graphicModel = "";
        String memorySize = "";
        Elements colOnes = $.select("#box_configuration .col_one");
        // TODO: 2018/9/11 有些字段还是拿不到，针对页面定制一下
        for(int i=0; i<colOnes.size(); i++) {
            String keyName = colOnes.get(i).text();
            if("CPU型号".equals(keyName) || "CPU".equals(keyName)) {
                cpuModel = colOnes.get(i+1).text();
            }
            if("系统内存".equals(keyName) || "内存容量".equals(keyName)) {
                memorySize = colOnes.get(i+1).text();
            }
            if("显示芯片".equals(keyName)) {
                graphicModel = colOnes.get(i+1).text();
            }
            if("硬盘容量".equals(keyName)) {
                diskSize = colOnes.get(i+1).text();
            }
        }
        notebook.setName(name);
        notebook.setCpuModel(cpuModel);
        notebook.setDiskSize(diskSize);
        notebook.setGraphicModel(graphicModel);
        notebook.setMemorySize(memorySize);
        return notebook;
    }

    @Override
    public Document parsable(Page page) {
        //url的一次过滤，只要后缀是数字.html的单品页
        String url = page.getWebURL().getURL();
        if(! suffixPattern.matcher(url).matches()) {
            return null;
        }
        ParseData parseData = page.getParseData();
        //ParseData包含三种：Binary、HTML、Text，这里只处理html页面
        if (parseData instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) parseData;
            String html = htmlParseData.getHtml();
            //用jsoup解析原始html
            Document $ = Jsoup.parse(html);
            //内容过滤, 先只是简单的看一下商品名称
            // TODO: 2018/9/11 笔记本包也来了..
            String productName = $.select("#span_product_name").text();
            if(! StringUtils.isEmpty(productName) && productName.contains("笔记本")) {
                return $;
            }
        }
        return null;
    }

    @Override
    public DumpService<Notebook> getDumpService() {
        if(this.noteBookDumpService == null) {
            this.noteBookDumpService = ApplicationContextHolder.getBean(NoteBookDumpService.class);
        }
        return this.noteBookDumpService;
    }

    public Pattern urlPrefix() {
        //爬联想相关的页面
        return Pattern.compile("https://\\w+\\.lenovo\\.com\\.cn.+");
    }

    @Override
    protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
        if(statusCode == 404) {

        }
    }
}
