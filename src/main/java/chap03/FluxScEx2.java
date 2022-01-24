package chap03;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FluxScEx2 {

    /**
     * Flux의 interval 메서드의 경우 데몬스레드를 만든다. (스레드 : 유저스레드 , 데몬스레드)
     * JVM 에서 메인 함수가 끝났을 때 유저스레드는 한개라도 남아있으면 스레드가 종료되지 않지만,
     * 데몬스레드의 경우는 JVM 이 종료 시켜 버린다.
     * @param args
     */
    public static void main(String[] args) throws InterruptedException {
        Flux.interval(Duration.ofMillis(500))
                .take(10) //10개만 받고 강제 종료 시켜버림
                .subscribe(s->log.debug("onNext:{}",s));

        TimeUnit.SECONDS.sleep(10); //Flux.interval 은 데몬스레드를 생성하므로 메인스레드를 sleep 걸어줘야 함
    }
}
