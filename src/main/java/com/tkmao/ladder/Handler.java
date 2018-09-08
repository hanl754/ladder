package com.tkmao.ladder;

/**
 * Description: 爬虫处理基类
 * @author hanliang
 * @time 2018/9/7 下午10:06
 */
public interface Handler {
    /**
     * 处理页面
     * @param url url地址
     * @param html 原始html字符串
     */
    void handle(String url, String html);

}
