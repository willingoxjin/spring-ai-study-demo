package org.willingoxjin.springai.entity;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import lombok.ToString;

/**
 *
 * @author Jin.Nie
 */
@ToString
@Data
public class ResponseVO<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer code;

    private String message;

    private T data;


    public static <T> ResponseVO<T> success() {
        ResponseVO<T> response = new ResponseVO<>();
        response.setCode(200);
        response.setMessage("success");
        return response;
    }

    public static <T> ResponseVO<T> success(T data) {
        ResponseVO<T> response = new ResponseVO<>();
        response.setCode(200);
        response.setMessage("success");
        response.setData(data);
        return response;
    }

    public static <T> ResponseVO<T> fail(Integer code, String message) {
        ResponseVO<T> response = new ResponseVO<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }

    public static <T> ResponseVO<T> fail(String message) {
        ResponseVO<T> response = new ResponseVO<>();
        response.setCode(400);
        response.setMessage(message);
        return response;
    }

}
