
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
- 요즘 강의를 잘 안보고있어서 관심있던 CompletableFutre 먼저 보기..

