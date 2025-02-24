package itstep.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.dal.dao.DataContext;
import itstep.learning.rest.RestResponse;
import itstep.learning.rest.RestService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@Singleton
public class UserServlet extends HttpServlet {
    private final DataContext dataContext;
    private final RestService restService;

    @Inject
    public UserServlet(DataContext dataContext, RestService restService) {
        this.dataContext = dataContext;
        this.restService = restService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // щоб по json передати string, треба взяти його в \" \"
        //resp.getWriter().print("\"Works\"");

        // частково формуємо відповідь клієнту
        RestResponse restResponse =
                new RestResponse()
                        .setResourceUrl("GET /user")
                        .setCacheTime( 600 )
                        .setMeta(Map.of(
                                "dataType", "object",
                                "read", "GET /user",
                                "update", "PUT /user",
                                "delete", "DELETE /user"
                        ));
        // .setCacheTime( 600 ) - на скільки будемо
        // видавати токен - на 600 секунд

        // створюємо заголовок
        String authHeader = req.getHeader("Authorization");
        if(authHeader == null){
            restService.sendResponse(resp,
                    restResponse.setStatus(401)
                            .setData("Authorization header required"));
            return;
        }

        // Basic з пробілом в кінці!!!
        String authScheme = "Basic ";
        if(!authHeader.startsWith(authScheme)){
            restService.sendResponse(resp,
                    restResponse.setStatus(401)
                            .setData("Authorization scheme error"));
            return;
        }

        String credentials = authHeader.substring(authScheme.length());

        restResponse.setData(credentials);

        restService.sendResponse(resp, restResponse);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        restService.setCorsHeaders(resp);
    }
}
