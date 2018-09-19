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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author pengyang pengyang813@foxmail.com
 * @date 2018/9/19
 */
@Service
public class HpCrawler extends AutoRunnableCrawler<Notebook> {
    private static final Pattern urlPattern = Pattern.compile("https://\\w+\\.hpstore\\.cn/.+\\.html");
    private static final String NAME_CSS_SELECTOR = ".page-title > .base";
    private static final String CPU_CSS_SELECTOR = "#product-attribute-specs-table tbody .data[data-th='处理器']";
    private static final String CPUS_CSS_SELECTOR = "#product-attribute-specs-table tbody .data[data-th='处理器系列']";
    private static final String MEMORY_CSS_SELECTOR = "#product-attribute-specs-table tbody .data[data-th='内存（标配）']";
    private static final String GRAPHIC_CSS_SELECTOR = "#product-attribute-specs-table tbody .data[data-th='显卡']";
    private static final String DISK_CSS_SELECTOR = "#product-attribute-specs-table tbody .data[data-th='硬盘说明']";
    private NoteBookDumpService noteBookDumpService;

    public Pattern urlPrefix() {
        return Pattern.compile("https://\\w+\\.hpstore\\.cn.+");
    }

    @Override
    public boolean shouldVisit(String url) {
        if (!urlPrefix().matcher(url).matches()) {
            return false;
        }
        if (url.contains("desktops") || url.contains("printers") || url.contains("ink-toner") ||
                url.contains("monitors") || url.contains("accessories")) {
            return false;
        }
        return true;
    }

    @Override
    public void addSeed(CrawlController controller) {
        controller.addSeed("https://www.hpstore.cn/laptops-tablets.html");
        controller.addSeed("https://www.hpstore.cn/laptops-tablets/personal-laptops.html");
        controller.addSeed("https://www.hpstore.cn/laptops-tablets/premium-computing.html");
        controller.addSeed("https://www.hpstore.cn/laptops-tablets/gaming.html");
        controller.addSeed("https://www.hpstore.cn/laptops-tablets/business-laptops.html");
    }

    @Override
    public DumpService<Notebook> getDumpService() {
        if (Objects.isNull(noteBookDumpService)) {
            this.noteBookDumpService = ApplicationContextHolder.getBean(NoteBookDumpService.class);
        }
        return this.noteBookDumpService;
    }

    @Override
    public Notebook handle(Document $) {
        if (Objects.isNull($)) {
            return null;
        }
        Notebook notebook = new Notebook();
        notebook.setName($.select(NAME_CSS_SELECTOR).text());
        notebook.setGraphicModel($.select(GRAPHIC_CSS_SELECTOR).text());
        notebook.setDiskSize(getStorageSize($.select(DISK_CSS_SELECTOR).text()));
        notebook.setMemorySize(getStorageSize($.select(MEMORY_CSS_SELECTOR).text()));
        // 有些笔记本页面上没有处理器信息，只有处理器系列信息
        String cpuInfo = $.select(CPU_CSS_SELECTOR).text();
        if (StringUtils.isEmpty(cpuInfo)) {
            cpuInfo = $.select(CPUS_CSS_SELECTOR).text();
        }
        notebook.setCpuModel(cpuInfo);
        return notebook;
    }

    @Override
    public Document parsable(Page page) {
        if (Objects.isNull(page)) {
            return null;
        }
        String url = page.getWebURL().getURL();
        if (!urlPattern.matcher(url).matches()) {
            return null;
        }
        ParseData parseData = page.getParseData();
        if (!(parseData instanceof HtmlParseData)) {
            return null;
        }
        HtmlParseData htmlParseData = (HtmlParseData) parseData;
        Document document = Jsoup.parse(htmlParseData.getHtml());
        String productName = document.select(NAME_CSS_SELECTOR).text();
        logger.info("url={} pName={}", url, productName);
        if (StringUtils.isEmpty(productName) || !isLaptopName(productName)) {
            return null;
        }
        return document;
    }

    private static String getStorageSize(String storageInfo) {
        if (StringUtils.isEmpty(storageInfo)) {
            return "";
        }
        String[] split = storageInfo.split(" ");
        if (split.length < 2) {
            return "";
        }
        return String.format("%s%s", split[0], split[1]);
    }

    private static boolean isLaptopName(String name) {
        if (StringUtils.isEmpty(name)) {
            return false;
        }
        return name.contains("笔记本") || name.contains("游戏本") || name.contains("变形本");
    }
}
