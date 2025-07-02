package com.lksnext.parkingplantilla.domain;


public interface CallbackWithResult<T> {
    void onSuccess(T result);
    void onFailure(Exception e);
}