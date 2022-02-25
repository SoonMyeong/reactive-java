package chap07;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FutureT {

    @Test
    @DisplayName("CompletableFuture")
    void future_test_01() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> f = CompletableFuture.completedFuture(1); //결과가 완료된 상태의 값을 넣어줌
        System.out.println(f.get());
    }

    @Test
    @DisplayName("CompletableFuture #2")
    void future_test_02() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> f = new CompletableFuture<>();
//        f.complete(2); // 01 테스트와 다르게 complete 을 해주지 않으면 무한대기에 빠짐! (당연함, 결과 기다려야지)
        f.completeExceptionally(new RuntimeException()); //예외를 담을 수 있음
        System.out.println(f.get()); // complete 전에 호출 시 에러 발생
    }

    @Test
    @DisplayName("CompletableFuture #3")
    void future_test_03() throws ExecutionException, InterruptedException {
        CompletableFuture.runAsync(()-> log.info("runAsync"+", " + Thread.currentThread().getName()))
                        .thenRun(()-> log.info("thenRun"));
        log.info("exit");
    }




}
