package io.ukihsoroy.bean;

/**
 * <p></p>
 *
 * @author K.O
 * @email ko.shen@hotmail.com
 */
public class Response<T> extends ResponseEntity{

    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
