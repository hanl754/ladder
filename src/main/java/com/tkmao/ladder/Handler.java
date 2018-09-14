package com.tkmao.ladder;

import com.tkmao.ladder.data.Ladderable;
import edu.uci.ics.crawler4j.crawler.Page;
import org.jsoup.nodes.Document;

/**
 * Description: 爬虫处理基类
 * @author hanliang
 * @time 2018/9/7 下午10:06
 */
public interface Handler {


    /**
     * 处理页面，只有{@link Handler#parsable(Page)} 有返回时才调用此方法
     * {@link AutoRunnableCrawler#visit(Page)}
     * @param $
     * @return 返回一个可以被天梯化的对象（感觉自己正在猪化）
     */
    Ladderable handle(Document $);


    /**
     * 尝试解析页面
     * @param page
     * @return 能处理返回document进行下一步处理, 不能处理返回null
     */
    Document parsable(Page page);
}
