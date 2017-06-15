package com.storn.freechat.rx;

import io.reactivex.Flowable;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subscribers.SerializedSubscriber;

/**
 * 消息订阅类
 * Created by tianshutong on 2017/5/17.
 */

public class RxBus {

    private static volatile RxBus mInstance;
    private FlowableProcessor<Object> _bus;

    public RxBus() {
        _bus = PublishProcessor.create().toSerialized();
    }

    public static RxBus getInstance() {
        if (mInstance == null) {
            synchronized (RxBus.class) {
                if (mInstance == null) {
                    mInstance = new RxBus();
                }
            }
        }
        return mInstance;
    }

    public void post(Object o) {
        new SerializedSubscriber<>(_bus).onNext(o);
    }

    /**
     * 确定接收消息的类型
     * @param aClass
     * @param <T>
     * @return
     */
    public <T> Flowable<T> toFlowable(Class<T> aClass) {
        return _bus.ofType(aClass);
    }

    /**
     * 判断是否有订阅者
     * @return
     */
    public boolean hasSubscribers() {
        return _bus.hasSubscribers();
    }
}
