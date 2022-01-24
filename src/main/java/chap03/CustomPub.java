package chap03;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class CustomPub {

    static Publisher<Integer> pub = sub -> {
        sub.onSubscribe(new Subscription() {
            @Override
            public void request(long n) {
                log.info("request()");
                sub.onNext(1);
                sub.onNext(2);
                sub.onNext(3);
                sub.onNext(4);
                sub.onNext(5);
                sub.onComplete();
            }

            @Override
            public void cancel() {

            }
        });
    };

     static Publisher<Integer> subOnPub = sub -> {
        pub.subscribe(new Subscriber<Integer>() {
            ExecutorService es = Executors.newSingleThreadExecutor(new CustomizableThreadFactory() {
                @Override
                protected String getDefaultThreadNamePrefix() {
                    return "subOn-";
                }
            });
            @Override
            public void onSubscribe(Subscription s) {
                es.execute(()->sub.onSubscribe(s));
            }

            @Override
            public void onNext(Integer integer) {
                es.execute(()->sub.onNext(integer));
            }

            @Override
            public void onError(Throwable t) {
                es.execute(()->sub.onError(t));
                es.shutdown();
            }

            @Override
            public void onComplete() {
                es.execute(()->sub.onComplete());
                es.shutdown();
            }
        });
    };

     static Publisher<Integer> pubOnPub = sub -> {
        subOnPub.subscribe(new Subscriber<Integer>() {
            //실제 구현체들도 Publish 할 때도 single thread 로 처리 함
            ExecutorService es = Executors.newSingleThreadExecutor(new CustomizableThreadFactory() {
                @Override
                protected String getDefaultThreadNamePrefix() {
                    return "pubOn-";
                }
            });

            @Override
            public void onSubscribe(Subscription s) {
                sub.onSubscribe(s);
            }

            @Override
            public void onNext(Integer integer) {
                es.execute(()->sub.onNext(integer));
            }

            @Override
            public void onError(Throwable t) {
                es.execute(()->sub.onError(t));
                es.shutdown();
            }

            @Override
            public void onComplete() {
                es.execute(()->sub.onComplete());
                es.shutdown();
            }
        });
    };


}
