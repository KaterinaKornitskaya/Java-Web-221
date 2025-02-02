package itstep.learning.servlets;

import com.google.gson.Gson;
import itstep.learning.rest.RestResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

// @ - анотації, аналог атрибутів [] в c#

@WebServlet("/Home") // WebServlet який буде відгукуватись на адресу /Home
public class HomeServlet extends HttpServlet {
    // final - типу readonly
    private final Gson gson = new Gson();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.getWriter().print(
              gson.toJson(
                   new RestResponse()
                           .setStatus(200)
                           .setMessage("Ok")
              )
        );

    }
}
