
/**
 * @file SimpleFormInsert.java
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/SimpleFormInsert")
public class InsertWhiting extends HttpServlet {
   private static final long serialVersionUID = 1L;

   public InsertWhiting() {
      super();
   }

   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      String assignment = request.getParameter("assignment");
      String className = request.getParameter("className");
      String dueDate = request.getParameter("dueDate");

      Connection connection = null;
      String insertSql = " INSERT INTO MyTableWhitingTE (id, ASSIGNMENT, CLASS, DUEDATE) values (default, ?, ?, ?)";

      try {
         DBConnectionWhiting.getDBConnection(getServletContext());
         connection = DBConnectionWhiting.connection;
         PreparedStatement preparedStmt = connection.prepareStatement(insertSql);
         preparedStmt.setString(1, assignment);
         preparedStmt.setString(2, className);
         preparedStmt.setString(3, dueDate);
         preparedStmt.execute();
         connection.close();
      } catch (Exception e) {
         e.printStackTrace();
      }

      // Set response content type
      response.setContentType("text/html");
      PrintWriter out = response.getWriter();
      String title = "Insert Data to DB table";
      String docType = "<!doctype html public \"-//w3c//dtd html 4.0 " + "transitional//en\">\n";
      out.println(docType + //
            "<html>\n" + //
            "<head><title>" + title + "</title></head>\n" + //
            "<body bgcolor=\"#f0f0f0\">\n" + //
            "<h2 align=\"center\">" + title + "</h2>\n" + //
            "<ul>\n" + //

            "  <li><b>Assignment</b>: " + assignment + "\n" + //
            "  <li><b>Class</b>: " + className + "\n" + //
            "  <li><b>Date Due</b>: " + dueDate + "\n" + //

            "</ul>\n");

      out.println("<a href=/techexercise_webproject/Search_Whiting.html>Search Assignment</a> <br>");
      out.println("</body></html>");
   }

   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      doGet(request, response);
   }

}
