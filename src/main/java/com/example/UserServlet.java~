package com.example;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/user")
public class UserServlet extends HttpServlet {

    // Метод GET: Обробка параметрів, сесій, куків та вивід HTML
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Обробка параметрів запиту (наприклад, ?name=Ivan&age=20)
        String name = request.getParameter("name");
        String age = request.getParameter("age");

        if (name == null || name.isEmpty()) {
            name = "Гість";
        }

        // 2. Робота з сесіями (збереження імені користувача)
        HttpSession session = request.getSession();
        session.setAttribute("userName", name);

        // 3. Робота з куками (збереження останнього візиту)
        Cookie visitCookie = new Cookie("lastVisit", "true");
        visitCookie.setMaxAge(60 * 60 * 24); // 1 день
        response.addCookie(visitCookie);

        // 4. Відправка відповіді у форматі HTML
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<html><head><title>Lab 1 - Servlets</title></head><body>");
        out.println("<h1>Лабораторна робота №1</h1>");
        out.println("<p>Привіт, <b>" + name + "</b>!</p>");

        if (age != null) {
            out.println("<p>Ваш вік: " + age + "</p>");
        }

        out.println("<p>Дані збережено в сесії. ID сесії: " + session.getId() + "</p>");
        out.println("<hr>");
        out.println("<form method='POST' action='user'>");
        out.println("  <input type='text' name='message' placeholder='Введіть повідомлення для JSON'>");
        out.println("  <button type='submit'>Відправити POST (JSON)</button>");
        out.println("</form>");
        out.println("</body></html>");
    }

    // Метод POST: Відправка відповіді у форматі JSON
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Отримання даних з форми
        String message = request.getParameter("message");
        if (message == null) message = "empty";

        // 5. Відправка відповіді у форматі JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        // Формуємо простий JSON вручну (якщо не використовуємо Jackson/Gson)
        out.print("{");
        out.print("\"status\": \"success\",");
        out.print("\"receivedMessage\": \"" + message + "\",");
        out.print("\"info\": \"Це відповідь у форматі JSON\"");
        out.print("}");
        out.flush();
    }
}

