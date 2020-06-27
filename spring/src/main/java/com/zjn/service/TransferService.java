package com.zjn.service;

/**
 * @author zjn
 * @create 2020-05-28 15:33
 * @description
 */
public interface TransferService {
    boolean transfer(String fromCardNo,String toCardNo,int money) throws Exception;
}
