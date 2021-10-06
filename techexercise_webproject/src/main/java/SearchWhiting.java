import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/SimpleFormSearch")
public class SearchWhiting extends HttpServlet {
   private static final long serialVersionUID = 1L;

   public SearchWhiting() {
      super();
   }

   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	  String assignment = request.getParameter("assignment");
	  String className = request.getParameter("className");
	  String dueDate = request.getParameter("dueDate");
      search(assignment, className, dueDate, response);
   }

   void search(String assignment, String className, String dueDate, HttpServletResponse response) throws IOException {
      response.setContentType("text/html");
      PrintWriter out = response.getWriter();
      String title = "Database Result";
      String docType = "<!doctype html public \"-//w3c//dtd html 4.0 " + //
            "transitional//en\">\n"; //
      out.println(docType + //
            "<html>\n" + //
            "<head><title>" + title + "</title></head>\n" + //
            "<body bgcolor=\"#f0f0f0\">\n" + //
            "<h1 align=\"center\">" + title + "</h1>\n");

      Connection connection = null;
      PreparedStatement preparedStatement = null;
      try {
         DBConnectionWhiting.getDBConnection(getServletContext());
         connection = DBConnectionWhiting.connection;

         //assignment is the only one not empty
         if(!assignment.isEmpty() && className.isEmpty() && dueDate.isEmpty())
         {
        	 String selectSQL = "SELECT * FROM MyTableWhitingTE WHERE ASSIGNMENT LIKE ?";
             String theAssignment = assignment + "%";
             preparedStatement = connection.prepareStatement(selectSQL);
             preparedStatement.setString(1, theAssignment);
         }
         //class name is the only one not empty
         else if(assignment.isEmpty() && !className.isEmpty() && dueDate.isEmpty())
         {
        	 String selectSQL = "SELECT * FROM MyTableWhitingTE WHERE CLASS LIKE ?";
             String theClass = className + "%";
             preparedStatement = connection.prepareStatement(selectSQL);
             preparedStatement.setString(1, theClass);
         }
         //dueDate is the only one not empty
         else if(assignment.isEmpty() && className.isEmpty() && !dueDate.isEmpty())
         {
        	 String selectSQL = "SELECT * FROM MyTableWhitingTE WHERE DUEDATE LIKE ?";
             String theDueDate = dueDate + "%";
             preparedStatement = connection.prepareStatement(selectSQL);
             preparedStatement.setString(1, theDueDate);
         }
         //assignment and class name are not empty and due date is
         else if(!assignment.isEmpty() && !className.isEmpty() && dueDate.isEmpty())
         {
        	 String selectSQL = "SELECT * FROM MyTableWhitingTE WHERE ASSIGNMENT LIKE ? AND CLASS LIKE ?";
        	 String theAssignment = assignment + "%";
        	 String theClass = className + "%";
             preparedStatement = connection.prepareStatement(selectSQL);
             preparedStatement.setString(1, theAssignment);
             preparedStatement.setString(2, theClass);
         }
         //assignment and due date are not empty and class name is
         else if(!assignment.isEmpty() && className.isEmpty() && !dueDate.isEmpty())
         {
        	 String selectSQL = "SELECT * FROM MyTableWhitingTE WHERE ASSIGNMENT LIKE ? AND DUEDATE LIKE ?";
        	 String theAssignment = assignment + "%";
        	 String theDueDate = dueDate + "%";
             preparedStatement = connection.prepareStatement(selectSQL);
             preparedStatement.setString(1, theAssignment);
             preparedStatement.setString(2, theDueDate);
         }
         //class name and due date are not empty and assignment is
         else if(assignment.isEmpty() && !className.isEmpty() && !dueDate.isEmpty())
         {
        	 String selectSQL = "SELECT * FROM MyTableWhitingTE WHERE CLASS LIKE ? AND DUEDATE LIKE ?";
        	 String theClass = className + "%";
        	 String theDueDate = dueDate + "%";
             preparedStatement = connection.prepareStatement(selectSQL);
             preparedStatement.setString(1, theClass);
             preparedStatement.setString(2, theDueDate);
         }
         //all of them are empty
         else if (assignment.isEmpty() && className.isEmpty() && dueDate.isEmpty()){
        	 String selectSQL = "SELECT * FROM MyTableWhitingTE";
             preparedStatement = connection.prepareStatement(selectSQL);
         }
         //none of them are empty
         else
         {
        	 String selectSQL = "SELECT * FROM MyTableWhitingTE WHERE ASSIGNMENT LIKE ? AND CLASS LIKE ? AND DUEDATE LIKE ?";
        	 String theAssignment = assignment + "%";
        	 String theClass = className + "%";
        	 String theDueDate = dueDate + "%";
             preparedStatement = connection.prepareStatement(selectSQL);
             preparedStatement.setString(1, theAssignment);
             preparedStatement.setString(2, theClass);
             preparedStatement.setString(3, theDueDate);
         }
         ResultSet rs = preparedStatement.executeQuery();

         while (rs.next()) {
            int id = rs.getInt("id");
            String assignmentParsed = rs.getString("assignment").trim();
            String classParsed = rs.getString("class").trim();
            String dueDateParsed = rs.getString("duedate").trim();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
            Date dateToCompare=sdf.parse(dueDateParsed); 
            Date today = Calendar.getInstance().getTime();
            long differenceInMill = dateToCompare.getTime() - today.getTime();
            long daysTillDue = TimeUnit.DAYS.convert(differenceInMill, TimeUnit.MILLISECONDS);

            if ((assignment.isEmpty() && className.isEmpty() && dueDate.isEmpty()) || assignmentParsed.contains(assignment) || classParsed.contains(className) || dueDateParsed.contains(dueDate)) {
               out.println("ID: " + id + ", ");
               out.println("Assignment: " + assignmentParsed + ", ");
               out.println("Class: " + classParsed + ", ");
               out.println("Due Date: " + dueDateParsed + ", ");
               if(daysTillDue >= 0)
               {
            	   out.println("Days Until Due: " + daysTillDue + "<br>");
               }
               else 
               {
            	   out.println("Due " + Math.abs(daysTillDue) + " days ago" + "<br>");
               }
            }
         }
         out.println("<a href=/techexercise_webproject/Search_Whiting.html>Search Assignments</a> <br>");
         out.println("</body></html>");
         rs.close();
         preparedStatement.close();
         connection.close();
      } catch (SQLException se) {
         se.printStackTrace();
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         try {
            if (preparedStatement != null)
               preparedStatement.close();
         } catch (SQLException se2) {
         }
         try {
            if (connection != null)
               connection.close();
         } catch (SQLException se) {
            se.printStackTrace();
         }
      }
   }

   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      doGet(request, response);
   }

}
