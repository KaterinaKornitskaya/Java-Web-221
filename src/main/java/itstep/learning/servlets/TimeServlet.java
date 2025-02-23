package itstep.learning.servlets;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.rest.RestResponse;
import itstep.learning.rest.TimeResponse;
import itstep.learning.services.datetime.DatetimeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;

//@WebServlet("/time")
@Singleton
public class TimeServlet extends HttpServlet {
    private final Gson gson = new Gson();

    private final DatetimeService datetimeService;

    @Inject
    public TimeServlet(DatetimeService datetimeService) {
        this.datetimeService = datetimeService;
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        sendJson(resp,
                new RestResponse()
                        .setStatus(200)
                        .setMessage(datetimeService.getCurrentDateTime()));

        // закоментовано попередній варіант без інжекції
//        TimeResponse timeResponse = new TimeResponse()
//                .setMessage("Поточний серверний час")
//                .setTime();
//
//        String jsonResp = gson.toJson(new TimeResponseDto(timeResponse));
//        resp.setContentType("application/json");
//        resp.getWriter().write(jsonResp);
    //}

//    private static class TimeResponseDto {
//        private final long timestamp;
//        private final String isoTime;
//        private final String message;
//
//        public TimeResponseDto(TimeResponse timeResponse) {
//            this.message = timeResponse.getMessage();
//            this.timestamp = timeResponse.getTimestamp();
//            this.isoTime = timeResponse.getIsoTime();
//        }
    }

    private void sendJson(HttpServletResponse resp, RestResponse restResponse) throws IOException {
        // Указываем, что ответ — в формате json
        resp.setContentType("application/json");

        // налаштували CORS
        resp.setHeader("Access-Control-Allow-Origin", "*");
        // Отправляем ответ клиенту

        resp.getWriter().print(
                gson.toJson(restResponse)
        );
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // налаштували CORS
        // * - значить дозволяємо звертатися з усіх сайтів
        // або замысть * адеса нашого фронтенду, напр "http://localhost:5173/"
        resp.setHeader("Access-Control-Allow-Origin", "*");

        // тут ми дозволяємо передавати content-type в заголовках
        // (ми в методі sendJson при передачі відповіді клієнту
        // передаємо, що у нас content-type - це json
        resp.setHeader("Access-Control-Allow-Headers", "content-type");
    }
}
