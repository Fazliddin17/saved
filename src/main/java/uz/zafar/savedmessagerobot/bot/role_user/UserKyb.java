package uz.zafar.savedmessagerobot.bot.role_user;

import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import uz.zafar.savedmessagerobot.bot.Kyb;

import java.util.ArrayList;
import java.util.List;

@Controller
public class UserKyb extends Kyb {
    public String backButton = "\uD83D\uDD19 Orqaga";
    public ReplyKeyboardMarkup registerLogin = setKeyboards(new String[]{
                "Yangi ochish", "Avvalgisiga ulanish"
    }, 1);
    public ReplyKeyboardMarkup back = this.setKeyboards(new String[]{this.backButton},1);
    public ReplyKeyboardMarkup menu = setKeyboards(new String[]{
            "Barchasi","\uD83C\uDFB5 Musiqalar","\uD83D\uDCF7 Rasmlar",
            "\uD83D\uDCF9 Videolar", "\uD83D\uDCC3 Hujjatlar",
            "\uD83D\uDCDD Matnlar" , "\uD83D\uDD04 Profilni o'zgartirish"
    }, 2);

    public InlineKeyboardMarkup aboutFile(Long fileId) {
        String[] examples = {"❌ Chatdan o'chirish", "❌ Malumotlardan o'chirish"};
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        row.add(createButton(examples[0], "delete-chat"));
        rows.add(row);
        row = new ArrayList<>();
        row.add(createButton(examples[1], fileId));
        rows.add(row);
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

}
