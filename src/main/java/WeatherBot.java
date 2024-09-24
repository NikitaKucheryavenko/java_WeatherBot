import com.google.gson.Gson;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WeatherBot extends TelegramLongPollingBot {

    private final String botToken;
    private final String openWeatherMapApiKey;

    public WeatherBot(String botToken, String openWeatherMapApiKey) {
        this.botToken = botToken;
        this.openWeatherMapApiKey = openWeatherMapApiKey;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasLocation()) {
            Location location = update.getMessage().getLocation();
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            String weatherInfo = getWeatherInfo(latitude, longitude);

            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId().toString());
            message.setText(weatherInfo);

            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private String getWeatherInfo(double latitude, double longitude) {
        String url = String.format(
                "http://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&appid=%s&units=metric",
                latitude, longitude, openWeatherMapApiKey);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.body();

            Gson gson = new Gson();
            WeatherResponse weatherResponse = gson.fromJson(jsonResponse, WeatherResponse.class);

            return String.format("Погода в локации %s:\nТемпература: %.1f°C\nОписание: %s",
                    weatherResponse.getName(),
                    weatherResponse.getMain().getTemp(),
                    weatherResponse.getWeather().get(0).getDescription());

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Не удалось получить информацию о погоде. \nИсправляем ситуацию.";
        }
    }

    @Override
    public String getBotUsername() {
        return "YourBotUsername";
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
