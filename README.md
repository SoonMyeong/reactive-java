
## 토비님의 유튜브에 있는 리액티브 영상 관련 내용 정리
### [영상 주소]
- https://www.youtube.com/watch?v=8fenTR3KOJo&list=PLv-xDnFD-nnmof-yoZQN8Fs2kVljIuFyC&index=10



### [챕터1. Reactive Streams] https://velog.io/@soonworld/Reactive-Streams-1
### [챕터2. Reactive Operator] https://velog.io/@soonworld/Reactive-Streams-2-Operator
### [챕터3. Reactive Schedulers] https://velog.io/@soonworld/Reactive-Streams-3-Scheduler
### [챕터4. 자바와 스프링의 비동기 기술]
### 비동기를 처리하기 위한 방법 
- Future or Callback
- Future 보단 Callback 구현하는게 더 우아함 (테스트코드 참고)
- 스프링에서 @Async 를 쓰면 기본적으로 SimpleAsyncThread 가 동작 하는데 
이 스레드의 문제는 요청 시 마다 무한히 계속 스레드를 생성하므로, 실무에서 잦은 @Async 를 사용시 자원을 매우 소모하게 된다.
- 이 문제를 해결하기 위해 별도의 ThreadPoolTaskExecutor 를 리턴하는 bean 을 생성해 두면
@Async 사용시 해당 ThreadPool 을 사용하게 된다! (중요) 

``` 
@Bean
ThreadPoolTaskExecutor tp() {
  ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
  te.setCorePoolSize(10); //ex
  te.setMaxPoolSize(100); //ex
  te.setQueueCapacity(200); //ex
  te.setRheadNamePrefix("test");
  return te;
}

위 메서드에서 중요한 점은
MaxPoolSize 의 경우 QueueCapa 한도 역시 가득 찰 경우
MaxPoolSize 만큼 스레드가 생성되는 동작을 하게 된다.
보통 QueueCapa 를 생각안하게 되는데 QueueCapa 를 잊지말 것 

```

### [챕터7. CompletableFuture] 
- 요즘 강의를 잘 안보고있어서 관심있던 CompletableFutre 먼저 보기
- 백기선님 java8 강의에 거의 대부분의 내용이 요약 되어 들어있음..(복습겸 들었음)
### 기존에 있던 Future 의 한계
- Future 를 외부에서 완료 시킬 수 없고 취소하거나 get() 에 대한 타임아웃 설정 불가
- 블로킹 코드 없이는 콜백 실행 할 수 없다.
- 여러 Future 를 조합할 수 없다. (ex. Event 정보 가져온 다음 Event 에 참석하는 회원 목록 가져오기 등...)
- 예외 처리 API 없음

### 비동기로 작업 실행
- 리턴 값이 없는 경우 : runAsync()
- 리턴 값이 있는 경우 : supplyAsync()
- 원하는 Executor 를 사용해서 실행할 수 있다. (default. ForkJoinPool.commonPool())

### 콜백 제공하기
- thenApply(Function) : 리턴 값을 받아 다른 값으로 바꿈
- thenAccept(Consumer) : 리턴 값을 또 다른 작업을 처리하는 콜백 (리턴 없이)
- thenRun(Runnable) : 리턴 값 받지않고 다른 작업 처리하는 콜백
- 콜백 자체를 또 다른 스레드에서 실행 가능

### 조합
- thenCompose() : 두 작업이 서로 이어서 실행하도록 조합
- thenCombine() : 두 작업을 독립적으로 실행, 둘 다 종료되었을 때 콜백 발생
- allOf() : 여러 작업을 모두 실행하고 모든 작업 결과에 콜백 발생
- anyOf() : 여러 작업 중 가장 빨리 끝난 하나의 결과에 콜백

### 예외처리
- exceptionally(Function)
- handle(BiFunction)
