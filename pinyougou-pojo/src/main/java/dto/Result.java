package dto;

import java.io.Serializable;

/**
 * 返回结果
 *
 * @Author ll
 * @Date 2018/10/31 9:37
 */
public class Result implements Serializable {

    private Boolean success;
    private String message;

    public Result(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Boolean getSuccess() {

        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
