/**
 * Sameera Bammidi
 * Created On: 12/05/2017
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

public class LCSSNDA
{
	public static void main(String[] args) throws IOException, ParseException
	{

		// Get all insiders from the purchase_LCS_Egonets_NodeAndEdge_Count.csv.
		File iinput = new File ("purchase_LCS_Egonets_NodeAndEdge_Count.csv");

		if(!iinput.exists())
		{
			System.out.println("file not found");
		}

		BufferedReader ibr = new BufferedReader(new FileReader(iinput));

		String iline;
		iline = ibr.readLine();// ignore first
		HashSet<Integer> insiderIdLCSPurchases = new HashSet<Integer>();

		while ((iline = ibr.readLine()) != null)
		{
			String[] tokens = iline.split(",");

			insiderIdLCSPurchases.add(Integer.parseInt(tokens[0]));
		}
		ibr.close();


		// required data structs
		/*
		 * a set of ratios for each insider , these ratios signify normalized profit or loss. 
		 * Keeping track as a set also lets us track no of transactions at different price points
		 *  
		 * */

		// map< key = insider id, value = map2>, where map2 = map <key = <Date,Price>  , value =Ratio> 
		HashMap <Integer, HashMap<TradesAtAValue,Double>> AllTradesAggregated = new HashMap <Integer, HashMap<TradesAtAValue,Double>> () ;

		// key = insider id, value = set of all ratios
		// considered, not needed 
		//HashMap <Integer, Set<Double>> AllSignedNormalizedDAs = new HashMap<Integer, Set<Double>> ();


		/*
		 * For each purchase transaction compute the total purchase price = no.of shares purchased x purchase price.
		 * For each purchase transaction compute dollar volume = closingPrice x stock volume.
		 * For each date, for each insider Compute sdna = total purchase price/dollar volume.
		 * If closingPrice > purchase price the store +sdna else store -sdna. 
		 * Save all of the above to a file purchases_SDNA_Data.csv.
		 * */

		/* Filter and process only those insiders left after LCS thresholds.. 
		 */
		File input = new File ("purchases_trade_Price_SNDA_all_Data.csv");

		if(!input.exists())
		{
			System.out.println("file not found");
		}

		//pwrp.println("ID, Date, insider_id, stock_id, NumberOfShares, PurchasePrice, ClosingPrice, StockVolume");
		BufferedReader br = new BufferedReader(new FileReader(input));
		String line;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); 
		line = br.readLine();// ignore first
		while ((line = br.readLine()) != null)
		{
			//System.out.println(line);
			String[] tokens = line.split(",");
			Integer insiderid = Integer.parseInt(tokens[2]);
			if(insiderIdLCSPurchases.contains(insiderid))
			{
				if(!AllTradesAggregated.containsKey(insiderid))
				{ // if not processed any record with this ID
					AllTradesAggregated.put(insiderid, new HashMap<TradesAtAValue,Double>());
					//AllSignedNormalizedDAs.put(insiderid, new HashSet<Double>());
				}

				// ID, Date, insider_id, stock_id, NumberOfShares, PurchasePrice, ClosingPrice, StockVolume
				Date date = df.parse(tokens[1]);
				java.sql.Date sqlDate = new java.sql.Date(date.getTime());
				// System.out.println(date.toString() +","+sqlDate.toString());
				// System.out.println(tokens[1] +","+sqlDate.toString());
				TradesAtAValue tav = new TradesAtAValue( Double.parseDouble(tokens[5]) ,sqlDate);
				double ratio = Integer.parseInt(tokens[4])*tav.price_point / (Double.parseDouble(tokens[6])*Double.parseDouble(tokens[7]));

				if(!(Double.parseDouble(tokens[6]) > tav.price_point))
				{
					//System.out.println("making it negative... " +tav.toString());

					ratio*=-1;
				}

				if(!AllTradesAggregated.get(insiderid).keySet().contains(tav))
				{
					AllTradesAggregated.get(insiderid).put( tav,ratio);
				}
				else
				{
					double currentRatio = AllTradesAggregated.get(insiderid).get( tav);
					currentRatio += ratio ;

					AllTradesAggregated.get(insiderid).put( tav,currentRatio);	
				}

			}

		}
		br.close();

		// At this point we have all signed normalized ratios for purchases
		// printing them by insider,transaction price,date, ratio
		PrintWriter pwr = new PrintWriter(new File("lcs_snda_Purchase.csv"));
		pwr.println("InsiderId, transactionPrice, Date, Ratio");
		for(Integer id:AllTradesAggregated.keySet() )
		{
			HashMap<TradesAtAValue,Double> val = AllTradesAggregated.get(id); 
			for(TradesAtAValue t : val.keySet())
			{
				pwr.println(id+","+t.price_point+","+t.date+","+val.get(t));
			}
		}
		pwr.close();

		//=======================================================================================================================//
		// Get all insiders from the sale_LCS_Egonets_NodeAndEdge_Count.csv.
		iinput = new File ("sale_LCS_Egonets_NodeAndEdge_Count.csv");

		if(!iinput.exists())
		{
			System.out.println("file not found");
		}

		ibr = new BufferedReader(new FileReader(iinput));

		iline = ibr.readLine();// ignore first
		HashSet<Integer> insiderIdLCSSales = new HashSet<Integer>();

		while ((iline = ibr.readLine()) != null)
		{
			String[] tokens = iline.split(",");

			insiderIdLCSSales.add(Integer.parseInt(tokens[0]));
		}
		ibr.close();

		// map< key = insider id, value = map2>, where map2 = map <key = <Date,Price>  , value =Ratio> 
		HashMap <Integer, HashMap<TradesAtAValue,Double>> AllTradesAggregatedSale = new HashMap <Integer, HashMap<TradesAtAValue,Double>> ();

		/* Filter and process only those insiders left after LCS thresholds.. 
		 */
		File inputSale = new File ("sale_trade_Price_SNDA_all_Data.csv");

		if(!inputSale.exists())
		{
			System.out.println("file not found");
		}

		//pwrp.println("ID, Date, insider_id, stock_id, NumberOfShares, PurchasePrice, ClosingPrice, StockVolume");
		BufferedReader brs = new BufferedReader(new FileReader(inputSale));
		String lines;
		DateFormat dfs = new SimpleDateFormat("yyyy-MM-dd");
		lines = brs.readLine();// ignore first
		while ((lines = brs.readLine()) != null)
		{
			//System.out.println(line);
			String[] tokens = lines.split(",");
			Integer insiderid = Integer.parseInt(tokens[2]);
			if(insiderIdLCSSales.contains(insiderid))
			{
				if(!AllTradesAggregatedSale.containsKey(insiderid))
				{ // if not processed any record with this ID
					AllTradesAggregatedSale.put(insiderid, new HashMap<TradesAtAValue,Double>());
					//AllSignedNormalizedDAs.put(insiderid, new HashSet<Double>());
				}

				// ID, Date, insider_id, stock_id, NumberOfShares, PurchasePrice, ClosingPrice, StockVolume
				Date date = dfs.parse(tokens[1]);
				java.sql.Date sqlDate = new java.sql.Date(date.getTime());
				// System.out.println(date.toString() +","+sqlDate.toString());
				// System.out.println(tokens[1] +","+sqlDate.toString());
				TradesAtAValue tav = new TradesAtAValue( Double.parseDouble(tokens[5]) ,sqlDate) ;
				double ratio = Integer.parseInt(tokens[4])*tav.price_point / (Double.parseDouble(tokens[6])*Double.parseDouble(tokens[7]));

				if(!(Double.parseDouble(tokens[6]) < tav.price_point))
				{
					//System.out.println("making it negative... " +tav.toString());

					ratio*=-1;
				}

				if(!AllTradesAggregatedSale.get(insiderid).keySet().contains(tav))
				{
					AllTradesAggregatedSale.get(insiderid).put( tav,ratio);
				}
				else
				{
					double currentRatio = AllTradesAggregatedSale.get(insiderid).get( tav);
					currentRatio += ratio ;

					AllTradesAggregatedSale.get(insiderid).put( tav,currentRatio);
				}

			}

		}
		brs.close();
		
		// At this point we have all signed normalized ratios for purchases
		// printing them by insider,transaction price,date, ratio
		PrintWriter pwrs = new PrintWriter(new File("lcs_snda_Sale.csv"));
		pwrs.println("InsiderId, transactionPrice, Date, Ratio");
		for(Integer id:AllTradesAggregatedSale.keySet() )
		{
			HashMap<TradesAtAValue,Double> val = AllTradesAggregatedSale.get(id); 
			for(TradesAtAValue t : val.keySet())
			{
				pwrs.println(id+","+t.price_point+","+t.date+","+val.get(t));
			}
		}
		pwrs.close();

	}

}




