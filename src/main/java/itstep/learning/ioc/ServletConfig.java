package itstep.learning.ioc;

import com.google.inject.servlet.ServletModule;
import itstep.learning.servlets.HomeServlet;
import itstep.learning.servlets.TimeServlet;

public class ServletConfig extends ServletModule {
    @Override
    protected void configureServlets() {
        // !! для усіх сервлетів у проекті
        // прибираємо анотацію @WebServlet,
        // додаємо анотацію @Singletone (home.google.inject)
        serve("/home").with(HomeServlet.class);
        serve("/time").with(TimeServlet.class);

    }
}
