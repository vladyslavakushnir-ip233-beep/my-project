package com.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

@WebServlet("/grades")
public class GradeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<html><head><title>Оцінки</title></head><body>");
        out.println("<h1>Оцінки студентів</h1>");
        out.println("<table border='1'>");
        out.println("<tr><th>Студент</th><th>Група</th><th>Предмет</th><th>Оцінка</th></tr>");

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT s.name, s.group_name, g.subject, g.grade " +
                 "FROM grades g JOIN students s ON g.student_id = s.id " +
                 "ORDER BY s.name")) {
            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getString("name") + "</td>");
                out.println("<td>" + rs.getString("group_name") + "</td>");
                out.println("<td>" + rs.getString("subject") + "</td>");
                out.println("<td>" + rs.getInt("grade") + "</td>");
                out.println("</tr>");
            }
        } catch (SQLException e) {
            out.println("<tr><td colspan='4'>Помилка: " + e.getMessage() + "</td></tr>");
        }

        out.println("</table><hr>");
        out.println("<h3>Додати оцінку:</h3>");
        out.println("<form method='POST' action='/servlet-app/grades'>");
        out.println("ID студента: <input type='number' name='student_id'><br>");
        out.println("Предмет: <input type='text' name='subject'><br>");
        out.println("Оцінка: <input type='number' name='grade' min='1' max='100'><br>");
        out.println("<button type='submit'>Додати</button>");
        out.println("</form>");
        out.println("<br><a href='/servlet-app/user'>Назад до студентів</a>");
        out.println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int studentId = Integer.parseInt(request.getParameter("student_id"));
        String subject = request.getParameter("subject");
        int grade = Integer.parseInt(request.getParameter("grade"));

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO grades (student_id, subject, grade) VALUES (?, ?, ?)")) {
            ps.setInt(1, studentId);
            ps.setString(2, subject);
            ps.setInt(3, grade);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        response.sendRedirect("/servlet-app/grades");
    }
}
