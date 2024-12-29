package uz.zafar.savedmessagerobot.bot.role_admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.zafar.savedmessagerobot.db.domain.User;


@Controller
@Log4j2
@RequiredArgsConstructor
public class RoleAdmin {
    private final AdminFunction function;

    public void mainMenu(User user, Update update, int size) {
        String eventCode = user.getEventCode();
        if (eventCode.equals("reklama")) {
            if (update.hasMessage()) {
                Message message = update.getMessage();
                if (message.hasText()) {
                    String text = message.getText();
                    if (text.equals("/start") || text.equals("\uD83D\uDD19 Orqaga")) {
                        function.start(user);
                        return;
                    }
                }
                function.copyMessage(user,message);
            }

        }
        if (update.hasMessage()){
            Message message = update.getMessage();
            if (message.hasText()){
                String text = message.getText();
                if (text.equals("/start")){
                    function.start(user);
                }else {
                    if (eventCode.equals("menu")){
                        function.menu(user , text ,size);
                    }
                }
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();
            if (eventCode.equals("menu")){
                function.menu(user , data  , callbackQuery , callbackQuery.getMessage().getMessageId(),size);
            }
        }
    }

}
