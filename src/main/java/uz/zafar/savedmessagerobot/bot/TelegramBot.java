package uz.zafar.savedmessagerobot.bot;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import uz.zafar.savedmessagerobot.bot.role_admin.AdminFunction;
import uz.zafar.savedmessagerobot.bot.role_admin.AdminKyb;
import uz.zafar.savedmessagerobot.bot.role_admin.RoleAdmin;
import uz.zafar.savedmessagerobot.bot.role_user.RoleUser;
import uz.zafar.savedmessagerobot.bot.role_user.UserFunction;
import uz.zafar.savedmessagerobot.bot.role_user.UserKyb;
import uz.zafar.savedmessagerobot.db.domain.User;
import uz.zafar.savedmessagerobot.db.repositories.FileRepository;
import uz.zafar.savedmessagerobot.db.service.FileUserService;
import uz.zafar.savedmessagerobot.db.service.UserService;
import uz.zafar.savedmessagerobot.dto.ResponseDto;

@Service
@Log4j2
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserService userService;
    @Value("${bot.token}")
    public String botToken;
    @Value("${size}")
    public int size;
    @Value("${admin.chat.id}")
    private Long adminChatId;


    @Value("${bot.username}")
    public String botUsername;
    @Autowired
    private AdminKyb adminKyb;
    @Autowired
    private UserKyb userKyb;
    @Autowired
    private FileUserService fileUserService;
    @Autowired
    private FileRepository fileRepository;

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        log.info(update);
        Long chatId;
        String username, firstname, lastname;
        if (update.hasMessage()) {
            username = update.getMessage().getFrom().getUserName();
            firstname = update.getMessage().getFrom().getFirstName();
            lastname = update.getMessage().getFrom().getLastName();
            chatId = update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            username = update.getCallbackQuery().getFrom().getUserName();
            firstname = update.getCallbackQuery().getFrom().getFirstName();
            lastname = update.getCallbackQuery().getFrom().getLastName();
            chatId = update.getCallbackQuery().getMessage().getChatId();
        } else {
            return;
        }
        ResponseDto<User> checkUser = userService.checkUser(chatId);
        User user;
        if (checkUser.isSuccess())
            user = checkUser.getData();
        else {
            user = new User();
            user.setChatId(chatId);
            user.setRole("user");
            user.setPage(0);
            user.setEventCode("");
            userService.save(user);
            checkUser = userService.checkUser(chatId);
            user = checkUser.getData();
        }
        if (user.getActive() == null) user.setActive(true);
        user.setUsername(username);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        userService.save(user);
        if (user.getChatId().equals(adminChatId)) {
            RoleAdmin roleAdmin = new RoleAdmin(
                    new AdminFunction(
                            this, userService, adminKyb,fileUserService
                    )
            );
            roleAdmin.mainMenu(user, update,size);
        } else {
            if (user.getRole().equals("user")) {
                if (!user.getActive()) return;
                RoleUser roleUser = new RoleUser(
                        new UserFunction(
                                this, userService,
                                userKyb, fileUserService,
                                fileRepository
                        )
                );
                roleUser.menu(user, update);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return this.botUsername;
    }

    @Override
    public String getBotToken() {
        return this.botToken;
    }


    @SneakyThrows
    public void sendMessage(Long chatId, String text) {
        execute(
                SendMessage
                        .builder()
                        .chatId(chatId)
                        .text(text)
                        .parseMode(ParseMode.HTML)
                        .disableWebPagePreview(true)
                        .build()
        );
    }

    @SneakyThrows
    public void sendMessage(Long chatId, String text, ReplyKeyboardMarkup markup) {
        execute(
                SendMessage
                        .builder()
                        .chatId(chatId)
                        .text(text)
                        .parseMode(ParseMode.HTML)
                        .replyMarkup(markup)
                        .disableWebPagePreview(true)
                        .build()
        );
    }

    @SneakyThrows
    public void sendMessage(Long chatId, String text, InlineKeyboardMarkup markup) {
        execute(
                SendMessage
                        .builder()
                        .chatId(chatId)
                        .text(text)
                        .parseMode(ParseMode.HTML)
                        .replyMarkup(markup)
                        .disableWebPagePreview(true)
                        .build()
        );
    }

    @SneakyThrows
    public void sendMessage(Long chatId, String text, Boolean remove) {
        execute(
                SendMessage
                        .builder()
                        .chatId(chatId)
                        .text(text)
                        .parseMode(ParseMode.HTML)
                        .replyMarkup(new ReplyKeyboardRemove(remove))
                        .disableWebPagePreview(true)
                        .build()
        );
    }

    @SneakyThrows
    public void alertMessage(CallbackQuery callbackQuery, String alertMessageText) {

        String callbackQueryId = callbackQuery.getId();
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setShowAlert(true);
        answerCallbackQuery.setText(alertMessageText);
        answerCallbackQuery.setCallbackQueryId(callbackQueryId);

        execute(answerCallbackQuery);
    }

    @SneakyThrows
    public void sendVideo(Long chatId, String fileId, String caption, InlineKeyboardMarkup markup, boolean isAdmin) {
        execute(
                SendVideo.builder()
                        .video(new InputFile(fileId))
                        .chatId(chatId)
                        .protectContent(!isAdmin)
                        .caption(caption)
                        .parseMode("HTML")
                        .replyMarkup(markup)
                        .build()
        );
    }

    @SneakyThrows
    public void deleteMessage(Long chatId, int messageId) {
        execute(DeleteMessage.builder().messageId(messageId).chatId(chatId).build());
    }
}
