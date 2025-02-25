package itstep.learning.rest;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class RestService {
    private final Gson gson = new Gson();

    public void sendResponse(HttpServletResponse resp, RestResponse restResponse) throws IOException {
        resp.setContentType("application/json");

        setCorsHeaders(resp);

        // Отправляем ответ клиенту
        resp.getWriter().print(
                gson.toJson(restResponse)
        );
    }

    public void setCorsHeaders(HttpServletResponse resp) {
        // налаштували CORS
        // * - значить дозволяємо звертатися з усіх сайтів
        // або замысть * адеса нашого фронтенду, напр "http://localhost:5173/"
        resp.setHeader("Access-Control-Allow-Origin", "*");

        // тут ми дозволяємо передавати content-type в заголовках
        // (ми в методі sendJson при передачі відповіді клієнту
        // передаємо, що у нас content-type - це json
        resp.setHeader("Access-Control-Allow-Headers", "authorization, content-type");

        //resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
    }

    public <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }
}
