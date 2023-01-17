import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;

import java.sql.SQLException;

public class MainClass {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        try {
            // connessione al bot telegram
            ApiContextInitializer.init();
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
            try {
                telegramBotsApi.registerBot(new BotTelegram());
            } catch (Exception e) {
                e.printStackTrace();
            }
            // start bot
            //bot.StartBot();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
