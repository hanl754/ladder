package com.tkmao.ladder.service;

import com.tkmao.ladder.data.Ladderable;

/**
 * Description:
 *
 * @author hanliang
 * @time 2018/9/14 下午7:09
 */
public interface DumpService<T extends  Ladderable> {
    /**
     * 持久化
     * @param ladderable
     */
    void dump(T ladderable);
}
