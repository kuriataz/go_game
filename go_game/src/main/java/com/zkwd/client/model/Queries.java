package com.zkwd.client.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Utility class for various queries to the database.
 */
public class Queries {
  /**
   * Get username from user id.
   * @param uid user id
   * @return username of specified user
   */
  public static String fetchUsername(int uid) {
    if (uid < 0) {
      return "CPU";
    } else {
      try {
        PreparedStatement req = App.getConnection().prepareStatement("""
          SELECT username FROM Users
          WHERE id = ?
        """);
        req.setInt(1, uid);
        ResultSet res = req.executeQuery();
        req.close();
        res.next();

        return res.getString(1);
      } catch (SQLException e) {
        System.out.println(e.getLocalizedMessage());
        return "unknown";
      }
    }
  }
}
