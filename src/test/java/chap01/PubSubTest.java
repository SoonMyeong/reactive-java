package chap01;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

public class PubSubTest {


    /**
     *
     * [Java 9 이상에서부터 reactive 한 패키지가 포함되어 있음]
     *
     * 옵저버 패턴과 Pubsub
     * Publisher <-Observable
     * Subscriber <- Observer
     *
     * Publisher 가 Subscriber 에게 반드시 전해줘야 하는 메서드
     *  onSubscribe (필수메서드) ,
     *  OnNext* (필수메서드, 0~N번 호출가능) ,
     *  (onError | onComplete) (2중 하나 필수)
     *  참고 : (https://github.com/reactive-streams/reactive-streams-jvm)
     *
     *  Publisher <-----------> Subscription(pub, sub 간 중개 역할) <---Subscriber
     *        | ----------------------------------------------------------|
     *
     *  Subscriber 는 Subscription 에 몇 건씩 데이터를 받을지에 대한 요청을 할 수 있는데
     *  publisher 와 subscriber 간 소통 하는 이러한 과정을 backpressure 라고 한다.
     *  ex) [subscriber] : publisher 야 나 데이터 10건만 먼저주고 나머진 나중에 줘 (이런 식으로)
     *  Subscription 의 메서드 인 request 에서 이러한 요청을 처리 할 수 있다.
     *   >모든 데이터를 처리 시 파라미터 값으로 long.Max 값으로 주면 된다.
     *
     *
     * backpressure 가 필요 한 이유
     * - pub 이나 sub 둘 중 하나가 다른 하나보다 속도가 빠를 경우
     *   pub 과 sub 간 속도를 맞추려 할 때 필요로 함
     *
     *
     *
     *
     *
     *
     */
    @DisplayName("PubSub 테스트")
    @Test
    void pubSubTest() {
        Iterable<Integer> itr = Arrays.asList(1,2,3,4,5);

        Publisher p = new Publisher() {
            @Override
            public void subscribe(Subscriber subscriber) {
                Iterator<Integer> it = itr.iterator();

                subscriber.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        while(n-- >0) {
                            if (it.hasNext()) {
                                subscriber.onNext(it.next());
                            } else {
                                subscriber.onComplete();
                                break;
                            }
                        }
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };


        Subscriber<Integer> s = new Subscriber<Integer>() {
            Subscription subscription;
            @Override
            public void onSubscribe(Subscription subscription) {
                System.out.println("onSubscribe");
                this.subscription = subscription;
                this.subscription.request(1); //1개씩 처리하겠다고  publisher 에게 요청하는 의미
//                this.subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer item) { // 옵저버의 update 와 같은 메서드
                System.out.println("onNext " + item);
                this.subscription.request(1); // 응답 받으면 다시 publisher 에게 1개 달라고 요청 한다는 의미
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("onError");
            }

            @Override
            public void onComplete() { // 데이터를 모두 처리 했을 경우
                System.out.println("oonComplete");
            }
        };

        p.subscribe(s);

    }
}
