package com.tkmao.ladder;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.parser.ParseData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.regex.Pattern;

/**
 * Description: 爬虫处理基类
 * @author hanliang
 * @time 2018/9/7 下午10:06
 */
public interface Handler {
    /**
     * 默认不需要匹配原则
     */
    Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g|ico"
            + "|png|tiff?|mid|mp2|mp3|mp4" + "|wav|avi|mov|mpeg|ram|m4v|pdf" + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

    /**
     * 默认的页面处理方式：如果能处理这个页面
     * @param page url地址
     */
    default void handle(Page page) {
        String url = page.getWebURL().getURL();
        if(support(url)) {
            ParseData parseData = page.getParseData();
            //ParseData包含三种：Binary、HTML、Text，这里只处理html页面
            if (parseData instanceof HtmlParseData) {
                HtmlParseData htmlParseData = (HtmlParseData) parseData;
                String html = htmlParseData.getHtml();
                //用jsoup解析原始html
                Document doc = Jsoup.parse(html);
                //处理文档
                dumpData(url, doc);
            }
        }
    }

    /**
     * 解析并且存储需要的数据
     * @param url url
     * @param $ htmlDocument
     */
    void dumpData(String url, Document $);

    /**
     * 是否支持这个页面的处理
     * @param url
     * @return
     */
    boolean support(String url);
}
