package com.tkmao.ladder;

import com.tkmao.ladder.data.Ladderable;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.url.WebURL;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.util.regex.Pattern;

/**
 * Description:
 *
 * @author hanliang
 * @time 2018/9/10 下午11:02
 */
@Slf4j
public abstract class AutoRunnableCrawler extends WebCrawler implements Handler {
    @Value("${each.crawler.thread}")
    protected int numberOfCrawlers;
    @Autowired
    private CrawlConfig config;

    /**
     * 默认不需要匹配原则
     */
    public Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g|ico"
            + "|png|tiff?|mid|mp2|mp3|mp4" + "|wav|avi|mov|mpeg|ram|m4v|pdf" + "|rm|smil|wmv|swf|wma|zip|rar|gz|exe))$");

    /**
     * 爬虫匹配的网址前缀
     */
    public abstract Pattern urlPrefix();

    /**
     * 入口
     * @param controller
     */
    public abstract void addSeed(CrawlController controller);

    @Override
    public void onStart() {
        super.onStart();
        log.info("[{}] start run.", this.getClass().getName());

    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        final String href = url.getURL();
        //不在过滤列表且前缀匹配
        return !FILTERS.matcher(href).matches() && urlPrefix().matcher(href).matches();
    }

    public void visit(Page page) {
        Document doc = tryIfParsable(page);
        if(doc != null) {
            Ladderable ladderable = handle(doc);
            if(ladderable != null) {
                ladderable.dump();
            }
        }
    }

    /**
     * 初始化方法
     */
    @PostConstruct
    void init() {
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = null;
        try {
            controller = new CrawlController(config, pageFetcher, robotstxtServer);
        } catch (Exception e) {
            log.error("new CrawlController error.", e);
            throw new RuntimeException(e);
        }
        addSeed(controller);
        controller.start(this.getClass(), numberOfCrawlers);
    }

}