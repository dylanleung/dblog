package io.dblog.sso.service.sign;

import io.dblog.proto.sso.api.AccountProto;
import io.dblog.sso.constant.AccountConstant;
import io.dblog.sso.controller.sign.RegisterForm;
import io.dblog.sso.entity.Account;
import io.dblog.sso.service.account.AccountService;
import io.dblog.sso.service.common.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Pelin on 17/8/1.
 */
public class RegisterService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private LoginService loginService;

    @Transactional
    public AccountProto.Account register(HttpServletRequest request, RegisterForm form,
                                         String userAgent, String registerIp) {
        String email = form.getEmail();
        String userName = form.getUserName();
        if (accountService.checkAccountExists(userName, email)) {
            // Throw Exception
            return null;
        }

        Account account = new Account();
        account.setStatus(AccountConstant.STATUS_INACTIVED);
        account.setUserName(form.getUserName());
        account.setName(form.getUserName());
        account.setEmail(form.getEmail());
        account.setPassword(form.getPassword());
        account.setGravatar("");
        account.setEmailValidation(false);
        account.setUserAgent(userAgent);
        account.setRegisterIp(registerIp);
        if (!accountService.save(account)) {
            // Throw Exception
            return null;
        }
        emailService.sendRegisterEmail(account);

        String sid = (String) request.getAttribute("sid");
        loginService.updateLoginSession(sid);

        return accountService.toProto(account);
    }
}