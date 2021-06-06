package cn.hellobike.hippo.json;

public interface Order {
    int LAST = Integer.MIN_VALUE;
    int FIRST = Integer.MAX_VALUE;

    int getOrder();
}
