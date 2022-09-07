package crm.commons.pojo;

import org.springframework.lang.Nullable;


public class ReturnObject {
    private String code;     //处理成功或失败的标记
    private @Nullable
    String message;  //提示信息

    private @Nullable Object retData;  //其他数据

    public ReturnObject(String code) {
        this.code = code;
    }

    public ReturnObject(String code, @Nullable String message) {
        this.code = code;
        this.message = message;
    }

    public ReturnObject(String code, @Nullable String message, @Nullable Object retData) {
        this.code = code;
        this.message = message;
        this.retData = retData;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getRetData() {
        return retData;
    }

    public void setRetData(Object retData) {
        this.retData = retData;
    }
}
