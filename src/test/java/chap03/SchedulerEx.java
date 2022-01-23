package chap03;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
public class SchedulerEx {

    /**
     * 리액티브 스케쥴러 종류 : publishOn , SubscribeOn
     *
     */
    @DisplayName("스케쥴러 #1 오퍼레이터를 이용해 별도의 스레드에서 동작하게 함 (SubscribeOn)")
    @Test
    void test01() {
        Publisher<Integer> pub = sub -> {
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

        Publisher<Integer> subOnPub = sub -> {
            ExecutorService es = Executors.newSingleThreadExecutor();
            es.execute(()-> pub.subscribe(sub));
        };

        subOnPub.subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                log.info("onSubscribe");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                log.info("onNext:{}" , integer);
            }

            @Override
            public void onError(Throwable t) {
                log.info("onError:{}", t);
            }

            @Override
            public void onComplete() {
                log.info("onComplete");
            }
        });
        System.out.println("exit");
    }

    @DisplayName("스케쥴러 #2 오퍼레이터를 이용해 별도의 스레드에서 동작하게 함 (PublishOn)")
    @Test
    void test02() {
        Publisher<Integer> pub = sub -> {
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

        Publisher<Integer> pubOnPub = sub -> {
            pub.subscribe(new Subscriber<Integer>() {
                //실제 구현체들도 Publish 할 때도 single thread 로 처리 함
                ExecutorService es = Executors.newSingleThreadExecutor();

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
                }

                @Override
                public void onComplete() {
                    es.execute(()->sub.onComplete());
                }
            });
        };

        pubOnPub.subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                log.info("onSubscribe");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                log.info("onNext:{}" , integer);
            }

            @Override
            public void onError(Throwable t) {
                log.info("onError:{}", t);
            }

            @Override
            public void onComplete() {
                log.info("onComplete");
            }
        });
        System.out.println("exit");
    }

    @DisplayName("스케쥴러 #3 오퍼레이터를 이용해 PublishOn 과 SubscribeOn 같이 사용")
    @Test
    void test3(){
        //TODO. 이거 순서 헤깔린다... 정리 다시 해야됨

        Publisher<Integer> pub = sub -> {
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

        Publisher<Integer> subOnPub = sub -> {
            ExecutorService es = Executors.newSingleThreadExecutor(new CustomizableThreadFactory() {
                @Override
                protected String getDefaultThreadNamePrefix() {
                    return "subOn-"; //thread 팩토리 이름 커스텀
                }
            });
            es.execute(()-> pub.subscribe(sub));
        };

        Publisher<Integer> pubOnPub = sub -> {
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
                }

                @Override
                public void onComplete() {
                    es.execute(()->sub.onComplete());
                }
            });
        };


        pubOnPub.subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                log.info("onSubscribe");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                log.info("onNext:{}" , integer);
            }

            @Override
            public void onError(Throwable t) {
                log.info("onError:{}", t);
            }

            @Override
            public void onComplete() {
                log.info("onComplete");
            }
        });

    }

    @Test
    @DisplayName("Flux 에서 스케쥴러 적용 잠깐 보기")
    void test4() {
        Flux.range(1,10)
                .log()
                .subscribeOn(Schedulers.newSingle("sub")) //중간 오퍼레이터이며 별도의 스케쥴 single thread 를 둠. test3의 원리를 적용한 것
                .subscribe(System.out::println);
        System.out.println("exit");
    }

    @Test
    @DisplayName("Flux 에서 스케쥴러 적용 잠깐 보기#2")
    void test5() {
        Flux.range(1,10)
                .publishOn(Schedulers.newSingle("pub"))
                .log()
                .subscribeOn(Schedulers.newSingle("sub"))
                .subscribe(System.out::println);
        System.out.println("exit");
    }


}
