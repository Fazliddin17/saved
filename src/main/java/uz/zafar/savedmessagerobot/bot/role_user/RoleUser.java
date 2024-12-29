package uz.zafar.savedmessagerobot.bot.role_user;

import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.zafar.savedmessagerobot.db.domain.User;
//import uz.zafar.ibratfarzandlari.db.domain.User;

import java.util.List;


@Controller
public class RoleUser {
    private final UserFunction function;

    public RoleUser(UserFunction function) {
        this.function = function;
    }

    public void menu(User user, Update update) {
        String eventCode = user.getEventCode();

        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasText()) {
                String text = message.getText();
                if (text.equals("/start")) {
                    function.start(user);
                } else if (text.equals("/help")) {
                    function.help(user) ;
                } else {
                    switch (eventCode) {
                        case "choose login or register" -> function.chooseLoginOrRegister(user, text);
                        case "get email for new profile" -> function.get_email_for_new_profile(user, text);
                        case "confirm code for register email" -> function.confirm_code_for_register_email(user, text);
                        case "connecting to profile for get email" ->
                                function.connecting_to_profile_for_get_email(user, text);
                        case "get confirm code for sign in" -> function.get_confirm_code_for_sign_in(user, text);
                        case "menu" -> function.menu(user, text);
                    }
                }
            } else if (message.hasPhoto()) {
                if (eventCode.equals("menu")) {
                    List<PhotoSize> photo = message.getPhoto();
                    function.addSavedMessages(user, "photo", photo.get(photo.size() - 1).getFileId());
                }
            } else if (message.hasVideo()) {
                if (eventCode.equals("menu")) {
                    function.addSavedMessages(user, "video", message.getVideo().getFileId());
                }
            } else if (message.hasDocument()) {
                if (eventCode.equals("menu")) {
                    function.addSavedMessages(user, "document", message.getDocument().getFileId());
                }
            } else if (message.hasAudio()) {
                if (eventCode.equals("menu")) {
                    function.addSavedMessages(user, "audio", message.getAudio().getFileId());
                }
            }
        } else if (update.hasCallbackQuery()) {
            function.menu(user, update.getCallbackQuery().getData(), update.getCallbackQuery(), update.getCallbackQuery().getMessage().getMessageId());
        }
    }
}
