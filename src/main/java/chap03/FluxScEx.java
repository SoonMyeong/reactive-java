package chap03;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class FluxScEx {

    /**
     * 리액터 네티나 별도의 was 를 사용했다면 그 컨테이너안에서 사이클관리가 되었을텐데 현재처럼 메인에서 돌릴 경우
     * 별도의 스케쥴러로 건 스레드가 메인스레드가 끝났음에도 살아있음. publish 가 완료되면 해당 스레드를 shutdown 시켜야 함
     * @param args
     */
    //TODO. 스케쥴 thread shutdown
    public static void main(String[] args) {
        Scheduler pub = Schedulers.newSingle("pub");
        Scheduler sub = Schedulers.newSingle("sub");
        Flux.range(1,10)
                .publishOn(pub)
                .log()
                .subscribeOn(sub)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        log.info("onSubscribe");
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        log.info("onNext :" , integer);
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {
                        pub.dispose();
                        sub.dispose();
                    }
                });


        System.out.println("exit");
    }
}
