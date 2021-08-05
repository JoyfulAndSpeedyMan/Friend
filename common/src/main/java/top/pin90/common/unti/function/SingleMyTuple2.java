package top.pin90.common.unti.function;

public class SingleMyTuple2<T> extends MyTuple2<T, T> {
    public SingleMyTuple2() {
        this(null, null);
    }

    public SingleMyTuple2(T t, T t2) {
        super(t, t2);
    }

}
