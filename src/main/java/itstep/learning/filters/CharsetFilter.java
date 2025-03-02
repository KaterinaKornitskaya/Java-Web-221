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

/*
* чим відрізняються UTF-8 та UNICODE?
* UNICODE - це правило, а UTF-8 - це кодування
* Що таке кодування - encoding - це правило, за яким ми
* перетворюємо з одної абетки в іншу - з коду в символ
* charset - це таблиця цих символів
* UNICODE - це набір правил, який говорить як будувати charset
* Якщо ASCI - це просто таблиця символів, то в UNICODE є методи
* Також кожен символ в UNICODE має набір характеристик, які
* приблизно збігаються з регулярними символами.
* Тобто у кожного символа є свої атрибути (які вказують, що наприкоад
* це - символ, це - буква і т.д.)
* UNICODE - це система цих метатегів, цих додаткових відомостей
* про символ. В UNICODE є своя мова запитів
* UTF-8 та UTF-16 - це різні імплементації, це різні charsetи (таблиці)
* UTF-16 - це 16-бітне кодування, має 65тис символів
* UTF-8 - це мультибайт кодування. !!! Погана ідея з UTF-8 читати посимвольно,
* з UTF-8 треба отримати повнястю всю строку, і потім вже її декодувати
* */
