package com.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

@WebServlet("/user/*")
public class UserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // PathVariable (/user/Іван)
        String name = null;
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.length() > 1) {
            name = pathInfo.substring(1);
        }

        // RequestParam (?name=Іван)
        if (name == null) name = request.getParameter("name");
        if (name == null || name.isEmpty()) name = "Гість";

        String age = request.getParameter("age");

        // Сесія — зберігаємо дані
        HttpSession session = request.getSession();
        session.setAttribute("userName", name);
        session.setAttribute("visitTime", System.currentTimeMillis());

        // Кука
        Cookie visitCookie = new Cookie("lastVisit", name);
        visitCookie.setMaxAge(60 * 60 * 24);
        response.addCookie(visitCookie);

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<html><head><title>Журнал оцінок</title></head><body>");
        out.println("<h1>Журнал оцінок студентів</h1>");
        out.println("<p>Привіт, <b>" + name + "</b>!</p>");
        if (age != null) out.println("<p>Вік: " + age + "</p>");
        out.println("<p>ID сесії: " + session.getId() + "</p>");
        out.println("<p>Збережено в сесії: " + session.getAttribute("userName") + "</p>");

        // Перевірка куки
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("lastVisit".equals(c.getName())) {
                    out.println("<p>Кука lastVisit: " + c.getValue() + "</p>");
                }
            }
        }

        out.println("<h3>Список студентів:</h3>");
        out.println("<table border='1'><tr><th>ID</th><th>Ім'я</th><th>Група</th><th>Email</th></tr>");

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM students")) {
            while (rs.next()) {
                out.println("<tr><td>" + rs.getInt("id") + "</td>");
                out.println("<td>" + rs.getString("name") + "</td>");
                out.println("<td>" + rs.getString("group_name") + "</td>");
                out.println("<td>" + rs.getString("email") + "</td></tr>");
            }
        } catch (SQLException e) {
            out.println("<tr><td colspan='4'>Помилка: " + e.getMessage() + "</td></tr>");
        }

        out.println("</table><hr>");
        out.println("<h3>Додати студента:</h3>");
        out.println("<form method='POST' action='/servlet-app/user'>");
        out.println("Ім'я: <input type='text' name='name'><br>");
        out.println("Група: <input type='text' name='group_name'><br>");
        out.println("Email: <input type='text' name='email'><br>");
        out.println("<button type='submit'>Додати</button></form>");
        out.println("<br><a href='/servlet-app/grades'>Переглянути оцінки</a>");
        out.println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String groupName = request.getParameter("group_name");
        String email = request.getParameter("email");

        String status;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO students (name, group_name, email) VALUES (?, ?, ?)")) {
            ps.setString(1, name);
            ps.setString(2, groupName);
            ps.setString(3, email);
            ps.executeUpdate();
            status = "success";
        } catch (SQLException e) {
            status = "error: " + e.getMessage();
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print("{\"status\":\"" + status + "\",\"name\":\"" + name + "\",\"group\":\"" + groupName + "\"}");
        out.flush();
    }
}
