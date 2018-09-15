package com.tkmao.ladder.service.impl;

import com.tkmao.ladder.data.Notebook;
import com.tkmao.ladder.service.DumpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Description:
 *
 * @author hanliang
 * @time 2018/9/14 下午7:10
 */
@Slf4j
@Service("notebookDumpService")
public class NoteBookDumpService implements DumpService<Notebook> {

    @Override
    public void dump(Notebook notebook) {
        log.info("{}", notebook);
    }
}

