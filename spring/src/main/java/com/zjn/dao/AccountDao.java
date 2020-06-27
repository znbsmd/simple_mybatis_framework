package com.zjn.dao;

import com.zjn.pojo.Account;

/**
 * @author zjn
 * @create 2020-05-28 15:21
 * @description
 */
public interface AccountDao {

    Account queryAccountByCardNo(String cardNo) throws Exception;

    int updateAccountByCardNo(Account account) throws Exception;
}
