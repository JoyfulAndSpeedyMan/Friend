package top.pin90.common.pojo;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 分页返回视图
 * @param <T>
 */
public class Page<T> {
    private long total;
    private long totalPages = -1;
    private int page;
    private int pageSize;
    private List<T> list;



    public static <T> Mono<Page<T>> from(Flux<T> objectFlux, Mono<Long> totalMono) {
        return from(objectFlux,totalMono, -1,-1);
    }


    public static <T> Mono<Page<T>> from(Flux<T> objectFlux, Mono<Long> totalMono, int page, int pageSize) {
        return objectFlux
                .collectList()
                .zipWith(totalMono)
                .map(tuple -> {
                    final List<T> list = tuple.getT1();
                    final Long total = tuple.getT2();
                    return new Page<T>(total, page, pageSize, list);
                });
    }


    private Page(long total, List<T> list) {
        this(total, -1, -1, list);
    }

    public Page(long total, int page, int pageSize, List<T> list) {
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.list = list;
    }

    public boolean empty(){
        return list.isEmpty();
    }
    public long getTotalPages() {
        return total / pageSize + 1;
    }

    public long getTotal() {
        return total;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public List<T> getList() {
        return list;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
