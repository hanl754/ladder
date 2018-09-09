package com.tkmao.ladder.crawlers;

import com.tkmao.ladder.Handler;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.regex.Pattern;

/**
 * Description:
 *
 * @author hanliang
 * @time 2018/9/8 下午11:47
 */
@Service
@Slf4j
public class LenovoCrawler extends WebCrawler implements Handler {
    /**
     * 爬虫匹配的网址前缀
     */
    private static final Pattern URL_PREFIX = Pattern.compile("https://\\w+\\.lenovo\\.com\\.cn.+");

    @Value("${each.crawler.thread}")
    private int numberOfCrawlers;


    @Autowired
    private CrawlController controller;

    @PostConstruct
    public void init() {
//        controller.addSeed("https://item.lenovo.com.cn/product/1001978.html");
        controller.addSeed("https://tk.lenovo.com.cn/product/100716.html");
        controller.addSeed("https://tk.lenovo.com.cn/product/100724.html");
        controller.addSeed("https://tk.lenovo.com.cn/product/100456.html");
        controller.start(this.getClass(), numberOfCrawlers);
    }

    @Override
    public void dumpData(String url, Document $) {
        //这里做一些过滤逻辑
        String productName = $.select("#span_product_name").text();
        if(productName.contains("笔记本")) {
            String img = $.select("#winpic").attr("src");
            String cpuModel = "";
            String graphicModel = "";
            String memorySize = "";
            Elements colOnes = $.select("#box_configuration .col_one");
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
            }
            log.info("url:{}, productName:{}, cpuModel:{}, memorySize:{}, graphicModel:{}", url, productName, cpuModel, memorySize,  graphicModel);
        }

    }

    @Override
    public boolean support(String url) {
        return url.contains(".lenovo.com.cn/product");
    }

    @Override
    public void onStart() {
        super.onStart();
        log.info("[{}] start run.", this.getClass().getName());

    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        final String href = url.getURL();
        //不在过滤列表且前缀匹配
        return !FILTERS.matcher(href).matches() && URL_PREFIX.matcher(href).matches();
    }

    public void visit(Page page) {
        handle(page);
    }
}
