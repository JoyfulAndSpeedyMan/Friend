package top.pin90.server.utils;

public class PageUtils {
    public static int pageLimit(int page){
        if(page<=0)
            return 0;
        if(page<=Integer.MAX_VALUE-1)
            return page-1;
        return Integer.MAX_VALUE;
    }
    public static int sizeLimit(int size){
        return sizeLimit(size,30);
    }
    public static int sizeLimit(int size,int max){
        if(size<0)
            return 0;
        return Math.min(size, max);
    }
}
