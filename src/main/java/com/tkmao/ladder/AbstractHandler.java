package com.tkmao.ladder;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


/**
 * Description:
 *
 * @author hanliang
 * @time 2018/9/7 下午10:11
 */
@Slf4j
public abstract class AbstractHandler implements Handler {

    private String url;
    private String html;

    public abstract void dumpData(Document $);

    public abstract boolean support(String url);

    @Override
    public void handle(String url, String html) {
        if(support(url)) {
            long start = System.currentTimeMillis();
            this.url = url;
            this.html = html;
            //用jsoup解析原始html
            Document doc = Jsoup.parse(html);
            //处理文档
            dumpData(doc);
            long end = System.currentTimeMillis();
            if(log.isDebugEnabled()) {
                log.debug("handle {} cost {}ms", this.url, (end - start));
            }
        }
    }

    public String getUrl() {
        return url;
    }

    public String getHtml() {
        return html;
    }
}
