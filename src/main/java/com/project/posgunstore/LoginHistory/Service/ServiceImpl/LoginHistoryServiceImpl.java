package com.project.posgunstore.LoginHistory.Service.ServiceImpl;


import com.project.posgunstore.LoginHistory.Model.LoginHistory;
import com.project.posgunstore.LoginHistory.Repository.LoginHistoryRepository;
import com.project.posgunstore.LoginHistory.Service.LoginHistoryService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LoginHistoryServiceImpl implements LoginHistoryService {

    private final LoginHistoryRepository loginHistoryRepository;

    public LoginHistoryServiceImpl(LoginHistoryRepository loginHistoryRepository) {
        this.loginHistoryRepository = loginHistoryRepository;
    }

    @Override
    public List<LoginHistory> getAll() {
        return loginHistoryRepository.findAll();
    }
}
