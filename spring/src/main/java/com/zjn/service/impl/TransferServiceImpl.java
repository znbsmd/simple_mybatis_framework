package com.zjn.service.impl;

import com.zjn.annotation.Autowired;
import com.zjn.annotation.Service;
import com.zjn.annotation.Transactional;
import com.zjn.dao.AccountDao;
import com.zjn.dao.impl.JdbcAccountDaoImpl;
import com.zjn.pojo.Account;
import com.zjn.service.TransferService;

/**
 * @author zjn
 * @create 2020-05-28 15:33
 * @description
 */
@Service(value = "one")
@Transactional
public class TransferServiceImpl implements TransferService {
    @Autowired
    private AccountDao accountDao;

    @Override
    public boolean transfer(String fromCardNo, String toCardNo, int money) throws Exception {

        Account from = accountDao.queryAccountByCardNo(fromCardNo);
        Account to = accountDao.queryAccountByCardNo(toCardNo);

//        System.out.println("from begin ----"+from.getMoney());
        from.setMoney(from.getMoney()-money);
//
//        System.out.println("from end ----"+from.getMoney());
//        System.out.println("to begin ----"+to.getMoney());

        to.setMoney(to.getMoney()+money);

//        System.out.println("to end ----"+to.getMoney());
        if(from.getMoney() < 0){
            return false;
        }
         accountDao.updateAccountByCardNo(to);
//         int c = 1/0;
         accountDao.updateAccountByCardNo(from);

        return true;
    }
}
