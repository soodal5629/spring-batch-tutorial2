package io.spring.springbatch.eventlistener;

import org.springframework.aop.support.AopUtils;
import org.springframework.batch.item.ItemReader;

import java.util.LinkedList;
import java.util.List;
// ItemReader 할 때는 retry 지원 X
public class LinkedListRetryListenerItemReader<T> implements ItemReader<T> {
    private List<T> list;
    public LinkedListRetryListenerItemReader(List<T> list) {
        if(AopUtils.isAopProxy(list)) {
            this.list = list;
        } else {
            this.list = new LinkedList<>(list);
        }
    }

    @Override
    public T read() throws CustomSkipException {
        if(!list.isEmpty()) {
            T remove = (T)list.remove(0);
            return remove;
        }
        return null;
    }
}
