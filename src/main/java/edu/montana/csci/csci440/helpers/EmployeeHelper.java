package edu.montana.csci.csci440.helpers;

import edu.montana.csci.csci440.model.Employee;
import edu.montana.csci.csci440.util.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EmployeeHelper {
    public static String makeEmployeeTree() {
        // TODO, change this to use a single query operation to get all employees
        Employee employee = new Employee(); // root employee
        Map<Long, List<Employee>> employeeMap = new HashMap<>();
        try (Connection conn = DB.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM employees"
             )) {
            //stmt.setLong(1, this.getEmployeeId());


            ResultSet results = stmt.executeQuery();
            results.next();

            //ROOT
            employee = new Employee(results);
            List<Employee> rootlist = new LinkedList<>();
            employeeMap.put(results.getLong("EmployeeID"), rootlist);

            while (results.next()) {
                //Make one for ourself
                List<Employee> resultList = new LinkedList<>();
                employeeMap.put(results.getLong("EmployeeID"), resultList);

                //Update references

                Employee underling = new Employee(results);
                long bossid = results.getLong("ReportsTo");
                //if(bossid == null) {
                    employeeMap.get(bossid).add(underling);
                //}
                //resultList.add(new Employee(results));


            }
            //return resultList;
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
        // and use this data structure to maintain reference information needed to build the tree structure

        return "<ul>" + makeTree(employee, employeeMap) + "</ul>";
    }

    // TODO - currently this method just usese the employee.getReports() function, which
    //  issues a query.  Change that to use the employeeMap variable instead
    public static String makeTree(Employee employee, Map<Long, List<Employee>> employeeMap) {
        String list = "<li><a href='/employees" + employee.getEmployeeId() + "'>"
                + employee.getEmail() + "</a><ul>";
        List<Employee> reports = employeeMap.get(employee.getEmployeeId());
        for (Employee report : reports) {
            list += makeTree(report, employeeMap);
        }
        return list + "</ul></li>";
    }
}
