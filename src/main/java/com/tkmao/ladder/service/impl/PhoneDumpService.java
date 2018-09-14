package com.tkmao.ladder.service.impl;

import com.tkmao.ladder.data.Phone;
import com.tkmao.ladder.service.DumpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Description:
 *
 * @author hanliang
 * @time 2018/9/14 下午7:29
 */
@Service("phoneDumpService")
@Slf4j
public class PhoneDumpService implements DumpService<Phone> {

    @Override
    public void dump(Phone phone) {
        log.info("{}", phone);
    }
}
