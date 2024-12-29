package uz.zafar.savedmessagerobot.bot.role_admin;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.zafar.savedmessagerobot.bot.Kyb;
import uz.zafar.savedmessagerobot.db.domain.User;
//import uz.zafar.ibratfarzandlari.bot.Kyb;
//import uz.zafar.ibratfarzandlari.db.domain.User;

import java.util.ArrayList;
import java.util.List;



@Controller
@Log4j2
public class AdminKyb extends Kyb {
    public ReplyKeyboardMarkup menu = setKeyboards(new String[]{
            "Foydalanuvchilar bo'limi" , "Reklama bo'limi"
    },2);
    public InlineKeyboardMarkup editUser(Boolean active){
        List<InlineKeyboardButton>row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        row.add(createButton("Foydalanuvchining xabarlarini ko'rish" , "see messages"));
        rows.add(row);
        row = new ArrayList<>();
        row.add(createButton(active?"Bloklash":"Blokdan chiqarish" , active?"active":"inactive"));
        row.add(createButton("Orqaga qaytisg" , "to back"));
        rows.add(row);
        return new InlineKeyboardMarkup(rows);
    }

    public InlineKeyboardMarkup setUsers(List<User> users) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            row.add(createButton(users.get(i).getFirstname(), users.get(i).getId()));
            if ((i + 1) % 2 == 0) {
                rows.add(row);
                row = new ArrayList<>();
            }
        }
        rows.add(row);
        row = new ArrayList<>();
        row.add(createButton("⬅️", "back"));
        row.add(createButton("➡️", "next"));
        rows.add(row);
        return new InlineKeyboardMarkup(rows);
    }

}
