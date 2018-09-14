package com.tkmao.ladder.data;

import lombok.Data;

/**
 * Description: 可以被天梯排名的东西
 * @author hanliang
 * @time 2018/9/10 下午11:32
 */
@Data
public abstract class Ladderable {
    /**
     * 名字
     */
    public String name;
}
