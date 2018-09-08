package com.tkmao.ladder.handlers;

import com.tkmao.ladder.AbstractHandler;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

/**
 * Description:
 *
 * @author hanliang
 * @time 2018/9/7 下午10:32
 */
@Service
public class LenovoHandler extends AbstractHandler {

    @Override
    public boolean support(String url) {
        return url.startsWith("https://item.lenovo.com.cn/product");
    }

    @Override
    public void dumpData(Document $) {
        String productName = $.select("#span_product_name").text();
        String img = $.select("#winpic").attr("src");
        String cpuModel = $.select("#box_configuration .col_values:eq(1)").text();
        String graphicModel = $.select("#box_configuration .col_values:eq(17)").text();
        System.out.println(productName + "\t" + cpuModel + "\t" + graphicModel);
    }
}
