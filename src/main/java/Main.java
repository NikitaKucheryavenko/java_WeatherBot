import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new WeatherBot("...",
                    "...")); //Вставьте вместо многоточий токен бота телеграм и API-ключ
        } catch (TelegramApiException e) {       //от сервиса OpenWeatherMap
            e.printStackTrace();
        }
    }
}