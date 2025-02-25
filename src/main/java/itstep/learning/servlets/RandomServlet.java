package itstep.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.dal.dao.DataContext;
import itstep.learning.rest.RestResponse;
import itstep.learning.rest.RestService;
import itstep.learning.services.random.RandomService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@Singleton
public class RandomServlet extends HttpServlet {
    private final DataContext dataContext;
    private final RandomService randomService;
    private final RestService restService;


    @Inject
    public RandomServlet(DataContext dataContext, RandomService randomService, RestService restService) {
        this.dataContext = dataContext;
        this.randomService = randomService;
        this.restService = restService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String typeParam = req.getParameter("type");
        String lengthParam = req.getParameter("length");
        int lengthParamToInt = 10;

        RestResponse restResponse = new RestResponse()
                .setResourceUrl("GET /random")
                .setCacheTime( 600 )
                .setMeta(Map.of(
                        "dataType", "object",
                        "read", "GET /random",
                        "update", "PUT /random",
                        "delete", "DELETE /random"
                ))
                .setStatus(200);

        if(lengthParam != null) {
            try{
                lengthParamToInt = Integer.parseInt(lengthParam);
                if(lengthParamToInt <=0 ){
                    restService.sendResponse(resp,
                            restResponse
                                    .setStatus(422)
                                    .setData("Length must more then 0"));
                    return;
                    //throw new NumberFormatException("Length must more then 0");
                }
            }
            catch (NumberFormatException e) {
                restService.sendResponse(resp,
                        restResponse
                                .setStatus(400)
                                .setData("Invalid length parameter"));
                //resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid length parameter");
                return;
            }
        }

        String randomString = "";
            switch (typeParam){
                case "salt":
                    randomString = randomService.randomString(lengthParamToInt);
                    break;
                case "filename":
                    randomString = randomService.randomFileName(lengthParamToInt);
                    break;
                default:
                    restService.sendResponse(resp,
                            restResponse
                                    .setStatus(400)
                                    .setData("Invalid type parameter"));
                    //resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid type parameter");
                    return;
            }


        restResponse
                .setData(randomString);
        restService.sendResponse(resp, restResponse);

    }
}
