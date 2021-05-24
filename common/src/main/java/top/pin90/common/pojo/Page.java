package top.pin90.common.pojo;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 分页返回视图
 *
 * @param <T>
 */
public class Page<T> {
    private Long total;
    private Long totalPages = -1L;
    private Integer page;
    private Integer pageSize;
    private List<T> list;


    public static <T> Mono<Page<T>> from(Flux<T> objectFlux, int page, int pageSize) {
        return objectFlux
                .collectList()
                .map(list -> {
                    return new Page<T>(null, page, pageSize, list);
                });
    }

    public static <T> Mono<Page<T>> from(Flux<T> objectFlux, Mono<Long> totalMono) {
        return from(objectFlux, totalMono, -1, -1);
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


    private Page(Long total, List<T> list) {
        this(total, -1, -1, list);
    }

    public Page(Long total, Integer page, Integer pageSize, List<T> list) {
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.list = list;
    }

    public boolean empty() {
        return list.isEmpty();
    }

    public Long getTotalPages() {
        if(total==null)
            return 0L;
        return total / pageSize + 1;
    }

    public Long getTotal() {
        return total;
    }

    public Integer getPage() {
        return page;
    }

    public Integer getPageSize() {
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
