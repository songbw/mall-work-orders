package com.fengchao.workorders.util;

public enum MyErrorMap {
    e200(200,"OK"),
    e201(201, "Created"),
    e202(202, "Accepted"),
    e204(204, "No Content"),

    e400(400, "Bad Request：请求未识别，未做任何处理"),
    e401(401, "Unauthorized：用户未提供身份验证凭据，或者没有通过身份验证"),
    e403(403, "Forbidden：用户通过了身份验证，但是不具有访问资源所需的权限"),
    e404(404, "Not Found：所请求的资源不存在，或不可用"),
    e405(405, "Method Not Allowed：用户已经通过身份验证，但是所用的 HTTP 方法不在他的权限之内"),
    e410(410, "Gone：所请求的资源已从这个地址转移，不再可用"),
    e415(415, "Unsupported Media Type：请求中需要的返回格式不支持"),
    e422(422, "Unprocessable Entity ：上传的附件无法处理，导致请求失败"),
    e429(425, "Too Many Requests：请求次数超过限额"),

    e501(501, "bad parameter"),
    e502(502, "-1"),
    e503(503, "-2"),

    ;

    private Integer code;
    private String msg;
    private String error;

    MyErrorMap(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
        this.error = code.toString() + msg;
    }

    @Override
    public String toString() {
        return this.error;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public String getError() {
        return error;
    }
}
