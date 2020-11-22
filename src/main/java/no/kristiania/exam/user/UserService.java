package no.kristiania.exam.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;



    public double getUserCount(UserService userService) {
        return userRepository.findAll().size();
    }

    public String getUserCountToString(Double count) {
        return String.valueOf(count);
    }
}
