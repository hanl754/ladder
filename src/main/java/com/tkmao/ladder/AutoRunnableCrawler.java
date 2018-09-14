package com.tkmao.ladder;

import com.tkmao.ladder.data.Ladderable;
import com.tkmao.ladder.service.DumpService;
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
 * Description: Crawler基类，子类按照各自的需求实现{@link #addSeed}, {@link #getDumpService}, {@link #urlPrefix}
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

    /**
     * 对应的持久化服务
     * @return
     */
    public abstract DumpService<Ladderable> getDumpService();

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

    /**
     * visit的逻辑就是尝试解析这个页面，如果是页面你的条件，就handle这个页面，生成{@link Ladderable}并持久化
     * @param page
     */
    public void visit(Page page) {
        Document doc = parsable(page);
        if(doc != null) {
            Ladderable ladderable = handle(doc);
            if(ladderable != null) {
                getDumpService().dump(ladderable);
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
