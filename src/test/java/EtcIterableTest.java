import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 *  Iterable (pull)  <------------------------------------------> Observable (push)
 *  (데이터를 달라고 요청함 ex. next() )                            (데이터를 가져가라고 전달함)
 */

public class EtcIterableTest {

    /**
     *  for-each 문에는 컬렉션만 들어갈 수 있는게 아니라 Iterable 인터페이스 구현체는
     *  모두 들어 갈 수 있다.
     */
    @DisplayName("for-each 문에는 컬렉션 뿐만 아닌 Iterable 구현체면 들어갈 수 있다.")
    @Test
    void foreach_iterable_test() {
        Iterable<Integer> iter = Arrays.asList(1,2,3,4,5);
        for(Integer i : iter) { //컬렉션이 아님에도 문법에러가 발생하지 않음!
            System.out.println(i); //정상 출력
        }
    }

    /**
     * Iterator : iterable 을 순회하게 끔 해주는 인터페이스
     * Iterable 구현체 안에서 Iterator 를 구현하여 Iterable 을 순회하게끔 한다.
     */
    @DisplayName("iterable 구현")
    @Test
    void iterable_implement() {
        Iterable<Integer> iter = () -> new Iterator<Integer>() {
            int i = 0;
            static final int MAX = 5;
            @Override
            public boolean hasNext() {
                return i<MAX;
            }

            @Override
            public Integer next() {
                return ++i;
            }
        };

        for(Integer i : iter) {
            System.out.println(i);
        }
    }


    /**
     *
     *  Observable (source) --> Event/Data -> Observer (데이터를 받는 애, 관찰자, 흔히 아는 옵저버패턴)
     *
     *  reactive stream 에서는 Observable 을 publisher 라고 표현하며,
     *  Observer 를 subscriber 라고 표현 한다. ( 이를 보통 줄여서 pub/sub 관계라고 표현하지요~ 여기까진 아는부분)
     *
     *  테스트 결과를 보면 "main  EXIT" 이라는 문구가 먼저 출력 된다!
     *  리액티브의 시작이다!!
     *
     *  현재 아래 테스트 한 옵저버 패턴의 단점
     *  1. Complete 이 없다.
     *  2. Error 처리 (비동기 이기에 옵저버 패턴만 가지고는 구현이 쉽지 않다.)
     *
     *
     */
    @DisplayName("Observable 테스트")
    @Test
    void observable_test() {

        class IntObservable extends Observable implements Runnable {
            @Override
            public void run() {
                for(int i=1; i<=10; i++) {
                    setChanged(); // 변화가 생김을 해당 메서드 호출로 알림
                    notifyObservers(i); //옵저버에게 노티를 날림 (값 전달 가능)   -- push
               }
            }
        }

        Observer ob = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                System.out.println(Thread.currentThread().getName() +" "+arg);
            }
        };

        IntObservable io = new IntObservable();
        io.addObserver(ob); // 옵저버블에게 옵저버 등록

        ExecutorService es = Executors.newSingleThreadExecutor();
        es.execute(io);

        System.out.println(Thread.currentThread().getName() +"  EXIT");
        es.shutdown();
    }

}
