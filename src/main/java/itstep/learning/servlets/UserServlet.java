package itstep.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.dal.dao.DataContext;
import itstep.learning.dal.dto.AccessToken;
import itstep.learning.dal.dto.User;
import itstep.learning.dal.dto.UserAccess;
import itstep.learning.models.UserAuthViewModel;
import itstep.learning.models.UserSignupFormModel;
import itstep.learning.rest.RestResponse;
import itstep.learning.rest.RestService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class UserServlet extends HttpServlet {
    private final DataContext dataContext;
    private final RestService restService;
    private final Logger logger;

    @Inject
    public UserServlet(DataContext dataContext, RestService restService, Logger logger) {
        this.dataContext = dataContext;
        this.restService = restService;
        this.logger = logger;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // щоб по json передати string, треба взяти його в \" \"
        //resp.getWriter().print("\"Works\"");

        // частково формуємо відповідь клієнту
        RestResponse restResponse =
                new RestResponse()
                        .setResourceUrl("GET /user")
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

        try {
            // розпаковуємо, з байтів збираємо стрінг
            credentials = new String(
                    Base64.getDecoder().decode(
                            credentials.getBytes()));
        }
        catch(Exception ex){
            restService.sendResponse(resp,
                    restResponse.setStatus(422)
                            .setData("Decode error" + ex.getMessage()));
            return;
        }

        // дістаєм частини із раніше створеного "мейл:пароль"
        String[] parts = credentials.split(":", 2);
        if(parts.length != 2){
            restService.sendResponse(resp,
                    restResponse.setStatus(422)
                            .setData("Format error spliting by ':' " ));
            return;
        }


        UserAccess userAccess = dataContext.getUserDao().authorize(parts[0], parts[1]);
        if(userAccess == null){
            restService.sendResponse(resp,
                    restResponse.setStatus(401)
                            .setData("Credentials rejected" ));
            return;
        }

        // тут юзер автентифікується
        // тут треба створити токен для користувача
        //AccessToken token = dataContext.getAccessTokenDao().createToken(userAccess);

        // перевірка - чи є активний токен
        AccessToken token = dataContext.getAccessTokenDao().getActiveToken(userAccess.getUserId());

        if (token == null) {
            // якщо активного токену нема - створюємо новий
            token = dataContext.getAccessTokenDao().createToken(userAccess);
        } else {
            // якщо активний токен є - продовжуємо і оновлюємо його
            token = dataContext.getAccessTokenDao().extendToken(token);
        }

        User user = dataContext.getUserDao().getUserById(userAccess.getUserId());



        // кешируем (.setCacheTime( 600 )) только если все ок
        restResponse
                .setStatus(200)
                .setCacheTime( 600 )
                .setData( new UserAuthViewModel(user, userAccess, token));

        restService.sendResponse(resp, restResponse);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // частково формуємо відповідь клієнту
        RestResponse restResponse =
                new RestResponse()
                        .setResourceUrl("PUT /user")
                        .setMeta(Map.of(
                                "dataType", "object",
                                "read", "GET /user",
                                "update", "PUT /user",
                                "delete", "DELETE /user"
                        ));

        // перевіряємо авторизацію за токеном
        // створюємо заголовок
        String authHeader = req.getHeader("Authorization");
        if(authHeader == null){
            restService.sendResponse(resp,
                    restResponse.setStatus(401)
                            .setData("Authorization header required"));
            return;
        }

        // Bearer з пробілом в кінці!!!
        String authScheme = "Bearer ";
        if(!authHeader.startsWith(authScheme)){
            restService.sendResponse(resp,
                    restResponse.setStatus(401)
                            .setData("Authorization scheme error"));
            return;
        }

        String credentials = authHeader.substring(authScheme.length());

        UserAccess userAccess =  dataContext.getAccessTokenDao().getUserAccess(credentials);
        if(userAccess == null){
            restService.sendResponse(resp,
                    restResponse.setStatus(401)
                            .setData("Token expires or invalid"));
            return;
        }

        User userUpdates;

        try{
            userUpdates = restService.fromBody(req, User.class);
        }
        catch(IOException ex){
            // якщо не змогли розпарсити модель - повертаємо 422
            restService.sendResponse(resp, restResponse
                    .setStatus(422)
                    .setMessage(ex.getMessage())
            );
            return;
        }
        if(userUpdates == null || userUpdates.getUserId() == null){
            restService.sendResponse(resp, restResponse
                    .setStatus(422)
                    .setMessage("Unparseable data or identity undefined")
            );
            return;
        }
        User user = dataContext
                .getUserDao()
                .getUserById(userUpdates.getUserId());
        if(user == null){
            restService.sendResponse(resp, restResponse
                    .setStatus(404)
                    .setMessage("User not found")
            );
            return;
        }

        // тепер після операцій вище - розпарсили id
        // та знайшли юзера - можемо вносити зміни
        if( ! dataContext.getUserDao().update(userUpdates)){
            restService.sendResponse(resp, restResponse
                    .setStatus(500)
                    .setMessage("Server error. See logs")
            );
            return;
        }

        restResponse
                .setStatus(202)
                .setCacheTime( 0 )
                .setData(userUpdates);

        restService.sendResponse(resp, restResponse);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RestResponse restResponse =
                new RestResponse()
                        .setResourceUrl("DELETE /user")
                        .setMeta(Map.of(
                                "dataType", "object",
                                "read", "GET /user",
                                "update", "PUT /user",
                                "delete", "DELETE /user"
                        ));

        // тут назвали параметр id, значить такий же ы використовуэмо
        // на фронті в запиті видалення : request('/user?id=' + user.userId, {
        String userId = req.getParameter("id");  // /user?id=...

        if(userId == null){
            restService.sendResponse(resp, restResponse
                    .setStatus(400)
                    .setData("Missing required id"));
            return;
        }

        UUID userUuid;

        try{
            userUuid = UUID.fromString(userId);
        }
        catch (Exception ignore) {
            restService.sendResponse(resp, restResponse
                    .setStatus(400)
                    .setData("Invalid id format"));
            return;
        }

        User user = dataContext.getUserDao().getUserById(userUuid);
        if(user == null){
            restService.sendResponse(resp, restResponse
                    .setStatus(401)
                    .setData("Unauthorized"));
            return;
        }

        try{
            // get() - значить чекаємо
            dataContext.getUserDao().deleteAsync(user).get();
        }
        catch(InterruptedException | ExecutionException ex){
            logger.log(Level.SEVERE, "deleteAsync fail: {0}", ex.getMessage());
            restService.sendResponse(resp, restResponse
                    .setStatus(500)
                    .setData("Server error. See server`s logs")
            );
            return;
        }


        restResponse
                .setStatus(202)
                .setCacheTime( 0 )
                .setData("Deleted");

        restService.sendResponse(resp, restResponse);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        restService.setCorsHeaders(resp);
    }
}
