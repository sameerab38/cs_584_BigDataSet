/**
 * @author Sameera Bammidi
 * 
 * Created on: 12/04/2017
 *
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SNDAAll
{

	public static void main(String[] args) throws SQLException, FileNotFoundException
	{
		// get jdbc connection
		Connection connection = JDBCMySQLConnection.getConnection();

		Statement statement = connection.createStatement();
		// Get the required columns from the database to compute signed normalized dollar amount and store it in a .csv file for all purchase transactions.
		String query = "select i.id, i.Date, i.InsiderId, i.StockId, i.NumberOfShares, i.Price, p.Close, p.Volume "
				+ "from insidertrades2 i, price p where i.StockId = p.IdStock and i.Action = 'Buy' and i.Date = p.Date";
		ResultSet rs = statement.executeQuery(query);
		
		System.out.println("Got the result set for purchase");
		
		PrintWriter pwrp = new PrintWriter(new File("purchases_trade_Price_SNDA_all_Data.csv"));
		pwrp.println("ID, Date, insider_id, stock_id, NumberOfShares, PurchasePrice, ClosingPrice, StockVolume");
		
		int counter = 0;
		
		while(rs.next())
		{
			counter ++;
			int id = rs.getInt("id");
			Date date = rs.getDate("Date");
			int InsiderId = rs.getInt("InsiderId");
			int StockId = rs.getInt("StockId");
			long NumberOfShares = rs.getLong("NumberOfShares");
			double Price = rs.getDouble("Price");
			double Close = rs.getDouble("Close");
			int Volume = rs.getInt("Volume");

			pwrp.println(id+","+date+","+InsiderId+","+StockId+","+NumberOfShares+","+Price+","+Close+","+Volume);
			
			System.out.println("Completed " + counter);
			
		}
		pwrp.close();

		// Get the required columns from the database to compute signed normalized dollar amount and store it in a .csv file for all sale transactions.
		String query2 = "select i.id, i.Date, i.InsiderId, i.StockId, i.NumberOfShares, i.Price, p.Close, p.Volume "
				+ "from insidertrades2 i, price p where i.StockId = p.IdStock and i.Action = 'Sell' and i.Date = p.Date";
		ResultSet rsSale = statement.executeQuery(query2);

		System.out.println("Got the result set for sale");
		
		PrintWriter pwrs = new PrintWriter(new File("sale_trade_Price_SNDA_all_Data.csv"));
		pwrs.println("ID, Date, insider_id, stock_id, NumberOfShares, SalePrice, ClosingPrice, StockVolume");
		
		counter = 0;
		
		while(rsSale.next())
		{
			counter++;
			
			int id = rsSale.getInt("id");
			Date date = rsSale.getDate("Date");
			int InsiderId = rsSale.getInt("InsiderId");
			int StockId = rsSale.getInt("StockId");
			long NumberOfShares = rsSale.getLong("NumberOfShares");
			double Price = rsSale.getDouble("Price");
			double Close = rsSale.getDouble("Close");
			int Volume = rsSale.getInt("Volume");

			pwrs.println(id+","+date+","+InsiderId+","+StockId+","+NumberOfShares+","+Price+","+Close+","+Volume);
			

			System.out.println("Completed " + counter);
			
		}
		pwrs.close();

	}

}
