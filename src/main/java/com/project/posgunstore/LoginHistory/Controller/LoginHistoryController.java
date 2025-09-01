package com.project.posgunstore.LoginHistory.Controller;

import com.project.posgunstore.LoginHistory.Model.LoginHistory;
import com.project.posgunstore.LoginHistory.Service.LoginHistoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/loginHistory")
public class LoginHistoryController {

    private final LoginHistoryService loginHistoryService;

    public LoginHistoryController(LoginHistoryService loginHistoryService) {
        this.loginHistoryService = loginHistoryService;
    }

    @GetMapping
    public List<LoginHistory> getAll() {
        return loginHistoryService.getAll();
    }
}
