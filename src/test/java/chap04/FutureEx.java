package chap04;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.concurrent.*;

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

    interface SuccessCallback {
        void onSuccess(String result);
    }

    interface ExceptionCallback {
        void onError(Throwable t);
    }


    public static class CallbackFutureTask extends FutureTask<String> {

        SuccessCallback sc;
        ExceptionCallback ec;
        public CallbackFutureTask(Callable<String> callable, SuccessCallback sc, ExceptionCallback ec) {
            super(callable);
            this.sc = Objects.requireNonNull(sc); // if(sc == null) 대신 심플하게 Objects 유틸 메서드 사용
            this.ec = Objects.requireNonNull(ec);
        }

        @Override
        protected void done() {
            try {
                sc.onSuccess(get());
            } catch (InterruptedException e) { //해당 exception 의 경우, 신호를 던져주는게 중요함,
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                ec.onError(e.getCause());
            }
        }
    }

    @Test
    @DisplayName("FutureTask 만들어보기")
    public void make_FutureTask() {
        ExecutorService es = Executors.newCachedThreadPool();

        CallbackFutureTask f = new CallbackFutureTask(()-> {
            Thread.sleep(2000);
            if(1==1) throw new RuntimeException("Async Error!"); //일부로 예외 만들어 보기
            log.info("Async");
            return "Hello";
        }, System.out::println, e-> System.out.println("Error : " + e.getMessage()));

        es.execute(f);


        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
