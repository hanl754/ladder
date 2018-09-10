package com.tkmao.ladder.data;

import lombok.Data;
import lombok.ToString;

/**
 * Description: 可以被天梯排名的东西
 * 试试充血模型
 * @author hanliang
 * @time 2018/9/10 下午11:32
 */
@Data
public abstract class Ladderable {
    /**
     * 名字
     */
    public String name;

    public abstract void dump();
}
