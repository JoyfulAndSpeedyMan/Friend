package top.pin90.common.unti.function;

public class MyTuple2<T1, T2> {
    public T1 t1;
    public T2 t2;

    public MyTuple2() {
        this(null, null);
    }

    public MyTuple2(T1 t1, T2 t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    public T1 getT1() {
        return t1;
    }

    public T2 getT2() {
        return t2;
    }

    public boolean hasT1() {
        return t1 != null;
    }

    public boolean hasT2() {
        return t2 != null;
    }

}
