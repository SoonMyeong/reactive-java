import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class FluxScEx {

    /**
     * 리액터 네티나 별도의 was 를 사용했다면 그 컨테이너안에서 사이클관리가 되었을텐데 현재처럼 메인에서 돌릴 경우
     * 별도의 스케쥴러로 건 스레드가 메인스레드가 끝났음에도 살아있음. publish 가 완료되면 해당 스레드를 shutdown 시켜야 함
     * @param args
     */
    //TODO. 스케쥴 thread shutdown
    public static void main(String[] args) {
        Flux.range(1,10)
                .publishOn(Schedulers.newSingle("pub"))
                .log()
                .subscribeOn(Schedulers.newSingle("sub"))
                .subscribe(System.out::println);
        System.out.println("exit");
    }
}
