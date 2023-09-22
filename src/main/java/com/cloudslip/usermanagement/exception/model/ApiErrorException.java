package com.cloudslip.usermanagement.exception.model;


public class ApiErrorException extends RuntimeException {

    private static final long serialVersionUID = -8658131859261427602L;

    private String service;

    private boolean needToRollback = false;

    public ApiErrorException(final String service) {
        super();
        this.service = service;
        this.needToRollback = false;
    }


    public ApiErrorException(final String service, final boolean needToRollback) {
        super();
        this.service = service;
        this.needToRollback = needToRollback;
    }

    public ApiErrorException(final String message, final String service) {
        super(message);
        this.service = service;
    }

    public ApiErrorException(final String message, final Throwable cause,
                             final String service) {
        super(message, cause);
        this.service = service;
    }

    public String getService() {
        return service;
    }

    public void setService(final String service) {
        this.service = service;
    }

    public boolean isNeedToRollback() {
        return needToRollback;
    }

    public void setNeedToRollback(boolean needToRollback) {
        this.needToRollback = needToRollback;
    }
}
