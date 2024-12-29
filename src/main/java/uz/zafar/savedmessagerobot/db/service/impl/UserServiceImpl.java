package uz.zafar.savedmessagerobot.db.service.impl;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import uz.zafar.savedmessagerobot.db.domain.File;
import uz.zafar.savedmessagerobot.db.domain.User;
import uz.zafar.savedmessagerobot.db.repositories.FileRepository;
import uz.zafar.savedmessagerobot.db.repositories.UserRepository;
import uz.zafar.savedmessagerobot.db.service.UserService;
import uz.zafar.savedmessagerobot.dto.ResponseDto;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final FileRepository fileRepository;

    public UserServiceImpl(UserRepository userRepository, JavaMailSender mailSender, FileRepository fileRepository) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
        this.fileRepository = fileRepository;
    }

    @Override
    public ResponseDto<User> checkUser(Long chatId) {
        try {
            User user = userRepository.findByChatId(chatId);
            if (user == null) {
                throw new Exception("Not found user");
            }
            return new ResponseDto<>(true, "Ok", user);
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<Void> save(User user) {
        try {
            userRepository.save(user);
            return new ResponseDto<>(true, "Ok");
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }



    @Override
    public ResponseDto<User> findById(Long userId) {
        try {
            Optional<User> userOp = userRepository.findById(userId);
            if (userOp.isPresent()) {
                return new ResponseDto<>(true, "Ok", userOp.get());
            }
            throw new Exception("Not found user");
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<List<User>> findAll() {
        try {
            return new ResponseDto<>(true , "Ok" , userRepository.findAll(Sort.by("id")));
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false , e.getMessage());
        }
    }

    @Override
    public ResponseDto<User> saveFile(File file) {
        try {
            fileRepository.save(file);
            return new ResponseDto<>(true, "Ok");
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<Page<User>> findAll(int page, int size) {
        try {
            Page<User> users = userRepository.findAllByOrderByIdDesc(PageRequest.of(page, size));
            return new ResponseDto<>(true , "Ok", users);
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false  , e.getMessage());
        }
    }

    @Override
    public ResponseDto<Void> sendEmail(String subject, String text, String to) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            mimeMessageHelper.setFrom("saved-message-robot.uz");
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(text, false);
            mailSender.send(mimeMessage);
            return new ResponseDto<>(true, "Ok");
        } catch (Exception e) {
            log.error(e);
            return new ResponseDto<>(false, e.getMessage());
        }
    }
}
