/**
 * File Name: SimilarityScores
 * 
 * Author: Sameera Bammidi
 *Created on: 11/10/2017
 * 
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet ;
import java.sql.Date;

public class SimilarityScores
{
	public static void main(String[] args) throws SQLException, FileNotFoundException
	{
		// get jdbc connection
		Connection connection = JDBCMySQLConnection.getConnection();

		// get stockids (distinct companies)
		HashSet<Integer> stockids = new HashSet<Integer>();
		Statement statement = connection.createStatement();
		String query = "select distinct StockId from insidertrades2"; // get stock ids
		ResultSet rs = statement.executeQuery(query);

		while(rs.next())
		{
			System.out.println(rs.getInt("StockId"));
			stockids.add(rs.getInt("StockId"));
		}
		System.out.println(stockids.size());


		HashMap<Integer, HashSet<Pair>> purchases_company_insiderPair_map = new HashMap<Integer, HashSet<Pair>>(); 
		HashMap<Integer, HashMap<Integer, HashSet<Date>>> purchases_company_insiderTradeDates_map = new HashMap<Integer,HashMap<Integer, HashSet<Date>>>(); 

		HashMap<Integer, HashSet<Pair>> sale_company_insiderPair_map = new HashMap<Integer, HashSet<Pair>>(); 
		HashMap<Integer, HashMap<Integer, HashSet<Date>>> sale_company_insiderTradeDates_map = new HashMap<Integer,HashMap<Integer, HashSet<Date>>>();

		// for each stock id get all the PURCHASE trades and populate key=InsiderID val=Set of all his/her trade dates

		for(Integer stockid : stockids)
		{
			HashMap<Integer, HashSet<Date>>  inid_date_map = new HashMap <Integer, HashSet<Date>>();
			String query2 = "select InsiderId,Date from insidertrades2 where Action = 'Buy'  AND  StockId = " + stockid;
			rs = statement.executeQuery(query2);
			while(rs.next())
			{
				int id = rs.getInt("InsiderId");
				Date d = rs.getDate("Date");
				if(inid_date_map.containsKey(id))
				{
					inid_date_map.get(id).add(d);
				}
				else
				{
					HashSet<Date> hsd = new HashSet<Date>();
					hsd.add(d);
					inid_date_map.put(id, hsd);
				}
			}
			purchases_company_insiderTradeDates_map.put(stockid, inid_date_map);
			// now populate the pairwise similarity score
			// output ONE single file of all similarity scores ( 1/2 of nxn matrix since it will be symmetric (which is also a version of adj matrix))
			// Build graph: nodes = insiderID, edges = similarity, threshold = 0.5 => remove every edge that does not have a similarity > 0.5 ? (confirm with TA)
			HashSet<Pair> allPurchasePairsForCurrentCompany = new HashSet<Pair>();
			ArrayList<Integer> allInsiders = new ArrayList<Integer> ();
			allInsiders.addAll(inid_date_map.keySet());
			for(int i=0;i<(allInsiders.size()-1);i++)
			{
				for(int j=i+1 ; j< allInsiders.size();j++)
				{
					Pair p = new Pair(allInsiders.get(i) , allInsiders.get(j));
					HashSet<Date> d1 = inid_date_map.get(allInsiders.get(i));
					HashSet<Date> d2 = inid_date_map.get(allInsiders.get(j));
					p.setScore(computeDateBasedSimilarity(d1,d2));
					allPurchasePairsForCurrentCompany.add(p);
				}
			}
			purchases_company_insiderPair_map.put(stockid, allPurchasePairsForCurrentCompany);
		}		

		// for each stock id get all the SALE trades and populate key=InsiderID val=Set of all his/her trade dates

		for(Integer stockid : stockids)
		{
			HashMap<Integer, HashSet<Date>>  sale_inid_date_map = new HashMap <Integer, HashSet<Date>>();
			String query3 = "select InsiderId,Date from insidertrades2 where Action = 'Sell'  AND  StockId = " + stockid;
			rs = statement.executeQuery(query3);
			while(rs.next())
			{
				int id = rs.getInt("InsiderId");
				Date d = rs.getDate("Date");
				if(sale_inid_date_map.containsKey(id))
				{
					sale_inid_date_map.get(id).add(d);
				}
				else
				{
					HashSet<Date> hsd = new HashSet<Date>();
					hsd.add(d);
					sale_inid_date_map.put(id, hsd);
				}
			}
			sale_company_insiderTradeDates_map.put(stockid, sale_inid_date_map);
			// now populate the pairwise similarity score
			// output ONE single file of all similarity scores ( 1/2 of nxn matrix since it will be symmetric (which is also a version of adj matrix))
			// Build graph: nodes = insiderID, edges = similarity, threshold = 0.5 => remove every edge that does not have a similarity > 0.5 ? (confirm with TA)
			HashSet<Pair> allSalePairsForCurrentCompany = new HashSet<Pair>();
			ArrayList<Integer> allSaleInsiders = new ArrayList<Integer> ();
			allSaleInsiders.addAll(sale_inid_date_map.keySet());

			for(int i=0;i<(allSaleInsiders.size()-1);i++)
			{
				for(int j=i+1 ; j< allSaleInsiders.size();j++)
				{
					Pair p = new Pair(allSaleInsiders.get(i) , allSaleInsiders.get(j));
					HashSet<Date> d1 = sale_inid_date_map.get(allSaleInsiders.get(i));
					HashSet<Date> d2 = sale_inid_date_map.get(allSaleInsiders.get(j));
					p.setScore(computeDateBasedSimilarity(d1,d2));
					allSalePairsForCurrentCompany.add(p);
				}
			}
			sale_company_insiderPair_map.put(stockid, allSalePairsForCurrentCompany);
		}
		connection.close();

		System.out.println(purchases_company_insiderTradeDates_map.toString());
		System.out.println(sale_company_insiderTradeDates_map.toString());

		// saving purchase similarity scores to a file for plotting histograms
		PrintWriter pwrp = new PrintWriter(new File("purchases_all_simscores.csv"));
		pwrp.println("company_stockID, insider1_id, insider2_id, similarity_score");
		for(Integer stockid: stockids)
		{
			HashSet<Pair> hpr = purchases_company_insiderPair_map.get(stockid);
			for(Pair p : hpr)
			{
				pwrp.println(stockid+","+p.id1+","+p.id2+","+p.getScore());
			}
		}
		pwrp.close();

		// saving sale similarity scores to a file for plotting histograms
		PrintWriter pwrs = new PrintWriter(new File("sale_all_simscores.csv"));
		pwrs.println("company_stockID, insider1_id, insider2_id, similarity_score");
		for(Integer stockid: stockids)
		{
			HashSet<Pair> hsp = sale_company_insiderPair_map.get(stockid);
			for(Pair p : hsp)
			{
				pwrs.println(stockid+","+p.id1+","+p.id2+","+p.getScore());
			}
		}
		pwrs.close();
	}

	private static double computeDateBasedSimilarity(HashSet<Date> d1, HashSet<Date> d2)
	{

		if(d1.size()==0 || d2.size()==0)
			return 0;

		double denominator = d1.size() * d2.size();
		double temp = 0 ;
		for(Date d1d : d1)
		{
			if(d2.contains(d1d))
				temp++;
		}
		if(temp==0)
			return 0;

		double numerator = Math.pow(temp, 2);
		return numerator / denominator;
	}
}

// note to self: using a TreeSet may be a good idea for LCS work

