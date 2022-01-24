package chap03;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;


@Slf4j
public class SchdulerEx {

    /**
     * 메인 함수 종료 시 에도 살아있는 스레드를 죽이기 위한 pub,sub 구현 확인
     * operator 에서 shutdown 하지 않았으면 아래 예제는 메인스레드가 끝나도 스레드가 죽지 않는다!
     *
     * @param args
     */
    public static void main(String[] args) {

        CustomPub.pubOnPub.subscribe(new Subscriber<Integer>() {
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
}
