package com.tkmao.ladder.data;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Description: 笔记本实体对象。
 * 仅需要名称，cpu，显卡，内存，硬盘这几个关键属性
 * @author hanliang
 * @time 2018/9/10 下午11:21
 */
@Data
@Slf4j
public class Notebook extends Ladderable{

    /**
     * cpu型号
     */
    private String cpuModel;

    /**
     * 内存大小
     */
    private String memorySize;

    /**
     * 显卡型号
     */
    private String graphicModel;

    /**
     * 硬盘大小
     */
    private String diskSize;

    @Override
    public String toString() {
        return "Notebook{" +
                "name='" + name + '\'' +
                ", cpuModel='" + cpuModel + '\'' +
                ", graphicModel='" + graphicModel + '\'' +
                ", memorySize='" + memorySize + '\'' +
                ", diskSize='" + diskSize + '\'' +
                '}';
    }

    @Override
    public void dump() {
       log.info("{}", this.toString());
    }
}
