package itstep.learning.servlets;

import com.google.gson.Gson;
import itstep.learning.rest.TimeResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/time")
public class TimeServlet extends HttpServlet {
    private final Gson gson = new Gson();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TimeResponse timeResponse = new TimeResponse()
                .setMessage("Поточний серверний час")
                .setTime();

        String jsonResp = gson.toJson(new TimeResponseDto(timeResponse));
        resp.setContentType("application/json");
        resp.getWriter().write(jsonResp);
    }

    private static class TimeResponseDto {
        private final long timestamp;
        private final String isoTime;
        private final String message;

        public TimeResponseDto(TimeResponse timeResponse) {
            this.message = timeResponse.getMessage();
            this.timestamp = timeResponse.getTimestamp();
            this.isoTime = timeResponse.getIsoTime();
        }
    }
}
