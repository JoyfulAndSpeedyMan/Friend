package top.pin90.server;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class TestReactor {
    Flux<Tuple2<Long, String>> flux;


    @Test
    public void testEmpty() {
        Mono.just("cc")
                .defaultIfEmpty("aa")
                .map(s -> s + "bb")
                .doOnNext(System.out::println)
                .subscribe();
    }

    @Test
    public void testZipWithEmpty() {
        final Mono<String> hh = Mono.just("hh");
        hh
                .zipWith(Mono.empty())
                .map(tuple -> {
                    String s = tuple.getT1() + " " + tuple.getT2();
                    System.out.println(tuple);
                    return s;
                })
                .defaultIfEmpty("aa")
                .doOnNext(s -> {
                    System.out.println(s);
                })
                .subscribe();
    }

    @Test
    public void testEmptyThen() {
        Mono.empty()
                .zipWhen(a -> {
                    System.out.println(a);
                    return Mono.just("bb");
                })
                .map(tuple -> tuple.getT1() + " " + tuple.getT2())
                .defaultIfEmpty("aa")
                .doOnNext(s -> {
                    System.out.println(s);
                })
                .subscribe();
    }

    @Test
    public void testBaseSubscriber() {
        SampleSubscriber<Integer> ss = new SampleSubscriber<Integer>();
        Flux<Integer> ints = Flux.range(1, 4);
        ints.subscribe(ss);

    }

    @Test
    public void testGenerate() {
        Flux<String> flux = Flux.generate(
                () -> 0,
                (state, sink) -> {
                    sink.next("3 x " + state + " = " + 3 * state);
                    if (state == 10) sink.complete();
                    return state + 1;
                });
        flux.subscribe(System.out::println);
    }

    public void testCreate() {


    }

    @Test
    public void testInte() throws InterruptedException {
        List<Flux<String>> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            final int j = i;
            Flux<String> flux =
                    Flux.interval(Duration.ofMillis(1000))
                            .map(input -> {
                                if (input < 1000) return "tick" + j + " " + input;
                                throw new RuntimeException("boom" + j);
                            })
                            .onErrorReturn("Uh oh");
            list.add(flux);
        }
        for (Flux<String> flux : list) {
            flux.subscribe(System.out::println);
        }
        Thread.sleep(2100);
    }

    @Test
    public void testThen() {

    }

    @Test
    public void testFlux() {
        Flux.empty()
                .collectList()
                .switchIfEmpty(Mono.just(Arrays.asList(new String[]{"嗷嗷", "哈哈"})))
                .subscribe(objects -> System.out.println(objects));
    }

    @Test
    public void testThread() throws InterruptedException {
        boolean flag = true;
        Mono<Integer> mono1 = Mono.<String>create(monoSink -> {
            System.out.println(tid("mono1 create"));
            if (flag)
                monoSink.success("11");
            else
                monoSink.success();
        })
//                .subscribeOn(Schedulers.single())
//                .publishOn(Schedulers.single())
                .map(s -> {
                    System.out.println(tid("mono1 map"));
                    return Integer.valueOf(s);
                });
        Mono<Integer> mono2 = Mono.<String>create(monoSink -> {
            System.out.println(tid("mono2 create"));
            if (flag)
                monoSink.success("22");
            else
                monoSink.success();
        })
//                .publishOn(Schedulers.parallel())

                .subscribeOn(Schedulers.elastic())
                .map(s -> {
                    System.out.println(tid("mono2 map"));
                    return Integer.valueOf(s);
                });
        Mono<String> zipWith = mono1.zipWith(mono2)
                .map(tuple -> {
                    System.out.println(tid("zip map"));
                    return String.format("mono1:%2d , mono2:%2d", tuple.getT1(), tuple.getT2());
                });
        Mono<String> zipWhen = mono1.zipWhen(m -> mono2)

                .map(tuple -> {
                    System.out.println(tid("zip map"));
                    return String.format("mono1:%2d , mono2:%2d", tuple.getT1(), tuple.getT2());
                });
        zipWhen
                .subscribe(s -> {
                    System.out.println(tid("zip subscribe"));
                    System.out.println(s);
                });
        Thread.currentThread().join();
    }

    private String tid(String id) {
        return String.format("%-15s%s", Thread.currentThread().getName(), id);
    }

}

class SampleSubscriber<T> extends BaseSubscriber<T> {

    public void hookOnSubscribe(Subscription subscription) {
        System.out.println("Subscribed");
        request(1);
    }

    public void hookOnNext(T value) {
        System.out.println(value);
//        request(1);
    }
}

interface MyEventListener<T> {
    void onDataChunk(List<T> chunk);

    void processComplete();
}