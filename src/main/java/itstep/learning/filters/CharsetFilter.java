package itstep.learning.filters;

import com.google.inject.Singleton;
import jakarta.servlet.*;

import java.io.IOException;

@Singleton
public class CharsetFilter implements Filter {
    private FilterConfig filterConfig;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // init - вместо конструктора, создаютмя объекты фильторов фреймворка
        // не мы их создаем, мы их только декларируем
        // init отрабатывает, когда объект Filter создается
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain next) throws IOException, ServletException {
        // doFilter - аналог Invoke

        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        next.doFilter(req, resp);
    }

    @Override
    public void destroy() {
        this.filterConfig = null;
    }
}

/*
* Фільтри - або сервлетні фільтри - це класи, що грають роль middleware.
* middleware - конвеєр або ланцюг,
* копцепція middleware полягіє в утворенні первинних ланок
* програмного забезпечення, які працюють за ідеєю вставляння в середину (між ланками конвеєра)
*
* */
