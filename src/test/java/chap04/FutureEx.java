package chap04;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class FutureEx {

    @Test
    @DisplayName("Future 예제, 응용 들어가면 헤깔릴 수 있다.")
    public void Future_ex_01() throws InterruptedException, ExecutionException {

        ExecutorService es = Executors.newCachedThreadPool(); //최초 만들 때에는 만들고, 다음 만들 때는 캐시하고 있던 스레드를 리턴

        Future<String> f = es.submit(()-> {
           Thread.sleep(2000);
           log.info("Async");
           return "Hello";
        });

        System.out.println(f.isDone());
        Thread.sleep(2100);
        log.info("Exit");
        System.out.println(f.isDone());
        System.out.println(f.get());

        /*
          false
          Async
          Exit
          true
          Hello
         */
    }

}
