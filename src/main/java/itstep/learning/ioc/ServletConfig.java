package itstep.learning.ioc;

import com.google.inject.servlet.ServletModule;
import itstep.learning.servlets.HomeServlet;
import itstep.learning.servlets.RandomServlet;
import itstep.learning.servlets.TimeServlet;
import itstep.learning.servlets.UserServlet;

public class ServletConfig extends ServletModule {
    @Override
    protected void configureServlets() {
        // !! для усіх сервлетів у проекті
        // прибираємо анотацію @WebServlet,
        // додаємо анотацію @Singletone (home.google.inject)
        serve("/home").with(HomeServlet.class);
        serve("/time").with(TimeServlet.class);
        serve("/user").with(UserServlet.class);
        serve("/random").with(RandomServlet.class);

    }
}
