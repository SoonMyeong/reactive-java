package chap02;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher; //reactivestreams 패키지에 있는 Publisher 나 java9 부터 존재하는 publisher 나 똑같다고 보면 된다.
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;


import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class PubSubTestOperator {

    /**
     *  Reactive Streams - Operators
     *
     *  downStream : Pub- >Sub 으로 데이터 흘러가는 형태 ( 위->아래)
     *  upStream : Sub - >Pub 으로 데이터 흘러가는 형태 (아래->위)
     *
     *  Publisher -> [Data1] -> Op1 -> [Data2] -> Op2 -> [Data3] -> Subscriber
     *  : Operator 는 Pub/Sub 간 데이터 흐름에서 데이터를 중간에 가공할 수 있도록 해줌
     *  1. map (data1 -> f -> data2)
     *
     */
    @DisplayName("지난 챕터 내용 + 확장")
    @Test
    void operatorTest() {
        Publisher<Integer> pub = iterPub(Stream.iterate(1,a->a+1).limit(10).collect(Collectors.toList()));
        Publisher<Integer> mapPub = mapPub(pub, s->s*10);  //이러한 걸 Operator 라고 한다.
        Publisher<Integer> map2Pub = mapPub(mapPub, s->-s); //이러한 걸 Operator 라고 한다.
        map2Pub.subscribe(logSub());
    }

//    @DisplayName("sumPub Operator 테스트")
//    @Test
//    void operatorTest2() {
//        Publisher<Integer> pub = iterPub(Stream.iterate(1,a->a+1).limit(10).collect(Collectors.toList()));
//        Publisher<Integer> sumPub = sumPub(pub);
//        sumPub.subscribe(logSub());
//    }

    /**
     * reduce 기본 개념 example
     * 1,2,3,4,5
     * 0 -> (0,1) -> 0+1 =1,
     * 1-> (1,2) -> 1+2 = 3,
     * 3-> (3,3) -> 3+3 = 6,
     * 6-> ..
     *
     */
    @DisplayName("reduce Operator 테스트")
    @Test
    void operatorTest3() {
        Publisher<Integer> pub = iterPub(Stream.iterate(1,a->a+1).limit(10).collect(Collectors.toList()));
        //BiFunction : 파라미터 두개 받아서 리턴을 하나 해주는 함수,  인터페이스에서는 3개다. 결과 제네릭까지 포함
        Publisher<Integer> reducePub = reducePub(pub, 0, (a, b)->a+b);
        reducePub.subscribe(logSub());
    }

    @DisplayName("제네릭한 mapPub")
    @Test
    void operatorTest4() {
        Publisher<Integer> pub = iterPub(Stream.iterate(1,a->a+1).limit(10).collect(Collectors.toList()));
        Publisher<String> mapPub = mapPub(pub, s-> "[" + s + "]");
        mapPub.subscribe(logSub());
    }


    @DisplayName("String reduce 테스트")
    @Test
    void operatorTest5() {
        Publisher<Integer> pub = iterPub(Stream.iterate(1,a->a+1).limit(10).collect(Collectors.toList()));
        Publisher<String> reducePub = reducePub(pub, "", (a, b)->a+ "-" +b);
        reducePub.subscribe(logSub());
    }

    @DisplayName("String reduce 테스트 #2 ")
    @Test
    void operatorTest6() {
        Publisher<Integer> pub = iterPub(Stream.iterate(1,a->a+1).limit(10).collect(Collectors.toList()));
        Publisher<StringBuilder> reducePub = reducePub(pub, new StringBuilder(), (a, b)->a.append(b+","));
        reducePub.subscribe(logSub());
    }



    private <T,R> Publisher<R> reducePub(Publisher<T> pub, R init, BiFunction<R, T, R> bf) {
        return sub -> {
            pub.subscribe(new DelegateSub<T,R>(sub){
                R result  = init;
                @Override
                public void onNext(T i) {
                    result = bf.apply(result,i);
                }

                @Override
                public void onComplete() {
                    sub.onNext(result);
                    sub.onComplete();
                }
            });
        };
    }

//    private Publisher<Integer> sumPub(Publisher<Integer> pub) {
//        return s -> {
//            pub.subscribe(new DelegateSub(s){
//                int sum = 0;
//                @Override
//                public void onNext(Integer i) {
//                    sum+= i;
//
//                }
//
//                @Override
//                public void onComplete() {
//                    sub.onNext(sum); //publisher 에서 onComplete 처리 할 때 sum 값 전달하고 complete
//                    sub.onComplete();
//                }
//            });
//        };
//    }





    /**
     * Operator 메서드
     *          * 여기서 Function<T, R> 인터페이스는
     *          * T 타입으로 데이터를 넣었을 때 R 타입으로 리턴하는 메서드 (apply) 를 구현 하면 된다.
     *
     * [ADD]
     * T -> R 로 되게 제네릭하게 수정
     * @param pub
     * @param f
     * @return
     */
    private <T,R> Publisher<R> mapPub(Publisher<T> pub, Function<T, R> f) {
        return new Publisher<R>() {
            @Override
            public void subscribe(Subscriber<? super R> sub) {
                pub.subscribe(new DelegateSub<T,R>(sub) {
                    @Override
                    public void onNext(T i) {
                        sub.onNext(f.apply(i));
                    }
                });
            }
        };
    }


    private <T> Subscriber<T> logSub() {
        return new Subscriber<T>() {
            @Override
            public void onSubscribe(Subscription s) {
                log.info("onSubscribe:");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(T integer) {
                log.info("onNext:{}",integer);
            }

            @Override
            public void onError(Throwable t) {
                log.info("onError:{}",t);
            }

            @Override
            public void onComplete() {
                log.info("onComplete");
            }
        };
    }

    private Publisher<Integer> iterPub(Iterable<Integer> iter) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                sub.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        try {
                            iter.forEach(sub::onNext);
                            sub.onComplete();
                        }catch(Throwable t) {
                            sub.onError(t);
                        }
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };
    }




}
