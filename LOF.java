/**
 * Java implementation of Local Outlier Factor algorithm by [Markus M. Breunig](http://www.dbs.ifi.lmu.de/Publikationen/Papers/LOF.pdf). 
 * The implementation accepts a collection `double[]` arrays, where each array of doubles corresponds to an instance.  
 *
 * Modified: Sameera Bammidi, Nov 26th, 2017.
 * In the main method, I wrote the implementation to compute LOF and total outlier score for purchase and sale network
 * with similarity score and LCS based approaches.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.IntStream;

public class LOF
{

	public static enum Distance
	{
		ABS_RELATIVE, EUCLIDIAN;
	}

	/** The training instances  */
	private Collection<double[]> trainInstances;
	private int numAttributes, numInstances;

	/** The distances among instances. */
	private double [][] distTable;

	/** Indices of the sorted distance */
	private int [][] distSorted;

	/** The minimum values for training instances */
	private double [] minTrain;

	/** The maximum values training instances */
	private double [] maxTrain;

	private Distance distanceMeasure;

	/**
	 * @author Sameera Bammidi
	 * Created On: 11/26/2017
	 */
	public static void main(String[] args) throws IOException
	{		
		int kNN = 5;
		System.out.println("======================================================PurchaseSimilarity======================================================");
		ArrayList<double[]> data = new ArrayList<double[]>();
		/*data.add(new double[]{0, 0});
		data.add(new double[]{0, 1});
		data.add(new double[]{1, 0});
		data.add(new double[]{1, 1});
		data.add(new double[]{1, 2});
		data.add(new double[]{2, 1});
		data.add(new double[]{2, 2});
		data.add(new double[]{2, 0});
		data.add(new double[]{2, 0});
		data.add(new double[]{2, 0});
		data.add(new double[]{2, 0});*/ 

		BufferedReader fr =  new BufferedReader(new FileReader(new File("purchase_sim_outlierscores.csv")));
		String line = fr.readLine(); // read and ignore first line
		while((line = fr.readLine()) != null)
		{			
			String[] tokens = line.split(",");
			data.add(new double[]{Math.log10(Integer.parseInt(tokens[2])), Math.log10(Integer.parseInt(tokens[3]))});
		}
		fr.close();
		LOF model = new LOF(data);

		System.out.println("LOF values on training examples");
		double[] scores = model.getTrainingScores(kNN);
		
		System.out.println("Got the socres");
			
		for(int i = 0; i < scores.length; i++)
		{
			System.out.println(Arrays.toString(data.get(i)) + "\t" + scores[i]);
		}

		System.out.println("Writing to file");
		
		PrintWriter pwr = new PrintWriter(new File("purchase_sim_outlierscores_withLOF.csv"));
		fr =  new BufferedReader(new FileReader(new File("purchase_sim_outlierscores.csv")));
		line = fr.readLine(); // read and ignore first line
		int i = 0;
		pwr.println("No., InsiderId, NodeCount, EdgeCount, OutlierScore, LOF, TotalOutlierScore");
		while((line = fr.readLine()) != null)
		{
			String[] lineTokens = line.split(",");
			double score = scores[i++];
			double tos = Double.parseDouble(lineTokens[4]) + score;
			pwr.println(line+","+score+","+ tos);
		}
		fr.close();
		pwr.close();

		System.out.println("======================================================SaleSimilarity======================================================");

		ArrayList<double[]> dataSaleSim = new ArrayList<double[]>();

		BufferedReader frss =  new BufferedReader(new FileReader(new File("sale_sim_outlierscores.csv")));
		line = frss.readLine(); // read and ignore first line
		while((line = frss.readLine()) != null)
		{			
			String[] tokens = line.split(",");
			dataSaleSim.add(new double[]{Math.log10(Integer.parseInt(tokens[2])), Math.log10(Integer.parseInt(tokens[3]))});
		}
		frss.close();
		LOF modelSaleSim = new LOF(dataSaleSim);

		System.out.println("LOF values on training examples");
		double[] scoresSaleSim = modelSaleSim.getTrainingScores(kNN);
		for(int is = 0; is < scoresSaleSim.length; is++)
		{
			System.out.println(Arrays.toString(dataSaleSim.get(is)) + "\t" + scoresSaleSim[is]);
		}

		PrintWriter pwrss = new PrintWriter(new File("sale_sim_outlierscores_withLOF.csv"));
		frss =  new BufferedReader(new FileReader(new File("sale_sim_outlierscores.csv")));
		line = frss.readLine(); // read and ignore first line
		i = 0;
		pwrss.println("No., InsiderId, NodeCount, EdgeCount, OutlierScore, LOF, TotalOutlierScore");
		while((line = frss.readLine()) != null)
		{
			String[] lineTokens = line.split(",");
			double score = scoresSaleSim[i++];
			double tos = Double.parseDouble(lineTokens[4]) + score;
			pwrss.println(line+","+score+","+tos);
		}
		frss.close();
		pwrss.close();

		System.out.println("======================================================PurchaseLCS======================================================");
		ArrayList<double[]> dataPurchaseLCS = new ArrayList<double[]>();

		BufferedReader frpl =  new BufferedReader(new FileReader(new File("purchase_LCS_outlierscores.csv")));
		line = frpl.readLine(); // read and ignore first line
		while((line = frpl.readLine()) != null)
		{			
			String[] tokens = line.split(",");
			dataPurchaseLCS.add(new double[]{Math.log10(Integer.parseInt(tokens[2])), Math.log10(Integer.parseInt(tokens[3]))});
		}
		frpl.close();
		LOF modelPurchaseLCS = new LOF(dataPurchaseLCS);

		System.out.println("LOF values on training examples");
		double[] scoresPurchaseLCS = modelPurchaseLCS.getTrainingScores(kNN);
		for(int is = 0; is < scoresPurchaseLCS.length; is++)
		{
			System.out.println(Arrays.toString(dataPurchaseLCS.get(is)) + "\t" + scoresPurchaseLCS[is]);
		}

		PrintWriter pwrpl = new PrintWriter(new File("purchase_LCS_outlierscores_withLOF.csv"));
		frpl =  new BufferedReader(new FileReader(new File("purchase_LCS_outlierscores.csv")));
		line = frpl.readLine(); // read and ignore first line
		i = 0;
		pwrpl.println("No., InsiderId, NodeCount, EdgeCount, OutlierScore, LOF, TotalOutlierScore");
		while((line = frpl.readLine()) != null)
		{
			String[] lineTokens = line.split(",");
			double score = scoresPurchaseLCS[i++];
			double tos = Double.parseDouble(lineTokens[4]) + score;
			pwrpl.println(line+","+score+","+tos);
		}
		frpl.close();
		pwrpl.close();

		System.out.println("======================================================SaleLCS======================================================");
		ArrayList<double[]> dataSaleLCS = new ArrayList<double[]>();

		BufferedReader frsl =  new BufferedReader(new FileReader(new File("sale_LCS_outlierscores.csv")));
		line = frsl.readLine(); // read and ignore first line
		while((line = frsl.readLine()) != null)
		{			
			String[] tokens = line.split(",");
			dataSaleLCS.add(new double[]{Math.log10(Integer.parseInt(tokens[2])), Math.log10(Integer.parseInt(tokens[3]))});
		}
		frsl.close();
		LOF modelSaleLCS = new LOF(dataSaleLCS);

		System.out.println("LOF values on training examples");
		double[] scoresSaleLCS = modelSaleLCS.getTrainingScores(kNN);
		for(int is = 0; is < scoresSaleLCS.length; is++)
		{
			System.out.println(Arrays.toString(dataSaleLCS.get(is)) + "\t" + scoresSaleLCS[is]);
		}

		PrintWriter pwrsl = new PrintWriter(new File("sale_LCS_outlierscores_withLOF.csv"));
		frsl =  new BufferedReader(new FileReader(new File("sale_LCS_outlierscores.csv")));
		line = frsl.readLine(); // read and ignore first line
		i = 0;
		pwrsl.println("No., InsiderId, NodeCount, EdgeCount, OutlierScore, LOF, TotalOutlierScore");
		while((line = frsl.readLine()) != null)
		{
			String[] lineTokens = line.split(",");
			double score = scoresSaleLCS[i++];
			double tos = Double.parseDouble(lineTokens[4]) + score;
			pwrsl.println(line+","+score+","+tos);
		}
		frsl.close();
		pwrsl.close();
	}
	public LOF(Collection<double[]> trainCollection)
	{
		this(trainCollection, Distance.EUCLIDIAN);
	}

	/**
	 * @param trainCollection
	 */
	public LOF(Collection<double[]> trainCollection, Distance distanceMeasure)
	{		
		// get training data dimensions
		numInstances = trainCollection.size();
		this.distanceMeasure = distanceMeasure;

		double[] first = trainCollection.iterator().next();
		numAttributes = first.length;

		trainInstances = trainCollection;

		// get the bounds for numeric attributes of training instances:
		minTrain = new double[numAttributes];
		maxTrain = new double [numAttributes];

		for (int i = 0; i < numAttributes; i++) {

			minTrain[i] = Double.POSITIVE_INFINITY;
			maxTrain[i] = Double.NEGATIVE_INFINITY;

			for(double[] instance : trainInstances){

				if(instance[i] < minTrain[i])
					minTrain[i] = instance[i];

				if(instance[i] > maxTrain[i])
					maxTrain[i] = instance[i];
			}
		}



		// fill the table with distances among training instances
		distTable = new double[numInstances + 1][numInstances + 1];
		distSorted = new int[numInstances + 1][numInstances + 1];

		int i = 0, j = 0;
		for(double[] instance1 :trainInstances){
			j = 0;
			for(double[] instance2 : trainInstances){
				distTable[i][j] = getDistance(instance1, instance2);
				j++;
			}
			if(i == j)
				distTable[i][j] = -1;
			i++;
		}
	}


	/**
	 * Returns neighbors for the new example.
	 * @param testInstance
	 * @param kNN
	 * @return
	 */
	public ArrayList<double[]> getNeighbors(double[] testInstance, int kNN){

		calcuateDistanceToTest(testInstance);

		// get the number of nearest neighbors for the current test instance:
		int numNN = getNNCount(kNN, numInstances);

		int[] nnIndex = new int[numNN];
		for (int i = 1; i <= numNN; i++) {
			nnIndex[i-1] = distSorted[numInstances][i];
		}

		// loop over training data
		ArrayList<double[]> res = new ArrayList<double[]>(numNN);
		int idx = 0;
		for(double[] instance : trainInstances){
			// check if instance is among neighbors
			for(int j = 0; j < nnIndex.length; j++){
				if(nnIndex[j] == idx){
					res.add(instance);
					break;
				}
			}
			idx++;
		}

		return res;
	}

	/**
	 * Returns LOF score for new example.
	 * @param testInstance
	 * @param kNN
	 * @return
	 */
	public double getScore(double[] testInstance, int kNN){

		calcuateDistanceToTest(testInstance);

		return getLofIdx(numInstances, kNN);	
	}

	/**
	 * Returns LOF scores for training examples.
	 * @param kNN
	 * @return
	 */
	public double[] getTrainingScores(int kNN)
	{
		int j = 0;
		
		// update the table with distances among training instances and a fake test instance
		for(int i = 0; i < numInstances; i++)
		{
			distTable[i][numInstances] = Double.MAX_VALUE;
			distSorted[i] = sortedIndices(distTable[i]);
			distTable[numInstances][i] = Double.MAX_VALUE;
			
			System.out.println("Got here 1 " + i);
			
		}	

		double[] res = new double[numInstances];
		for(int idx = 0; idx < numInstances; idx++)
		{
			res[idx] = getLofIdx(idx, kNN);
			
			System.out.println("Got here 2 " + j++);
			
		}
		return res;
	}

	private double getLofIdx(int index, int kNN){

		// get the number of nearest neighbors for the current test instance:
		System.out.println("before getNNCount call... ");
		int numNN = getNNCount(kNN, index);
		System.out.println("after getNNCount call... ");
		System.out.println("got numNN "+numNN+" so this is how many times it is going to iterate");
		// get LOF for the current test instance:
		double lof = 0.0;
		for (int i = 1; i <= numNN; i++) {
			double lrdi = getLocalReachDensity(kNN, index);
			lof += (lrdi == 0) ? 0 : getLocalReachDensity(kNN, distSorted[index][i]) / lrdi;
		}
		lof /= numNN;
		System.out.println("Finally got lof, numNN "+lof+","+numNN);
		return lof;	
	}

	private void calcuateDistanceToTest(double[] testInstance){
		// update the table with distances among training instances and the current test instance:
		int i = 0;
		for(double[] trainInstance : trainInstances){
			distTable[i][numInstances] = getDistance(trainInstance, testInstance);
			distTable[numInstances][i] = distTable[i][numInstances];
			i++;
		}
		distTable[numInstances][numInstances] = -1;

		// sort the distances
		for (i = 0; i < numInstances + 1; i++) {
			distSorted[i] = sortedIndices(distTable[i]);
		}			
	}

	private double getDistance(double[] first, double[] second) {

		// calculate absolute relative distance
		double distance = 0;

		switch(distanceMeasure){

		case ABS_RELATIVE:
			for (int i = 0; i < this.numAttributes; i++) {  
				distance += Math.abs(first[i] - second[i]) / (maxTrain[i] - minTrain[i]);
			}

		case EUCLIDIAN:
			for (int i = 0; i < this.numAttributes; i++) {  
				distance += Math.pow(first[i] - second[i], 2);
			}
			distance = Math.sqrt(distance);

		default:
			break;

		}

		return distance;
	}

	private double getReachDistance(int kNN, int firstIndex, int secondIndex) {

		// max({distance to k-th nn of second}, distance(first, second))

		double reachDist = distTable[firstIndex][secondIndex];

		int numNN = getNNCount(kNN, secondIndex);

		if (distTable[secondIndex][distSorted[secondIndex][numNN]] > reachDist)
			reachDist = distTable[secondIndex][distSorted[secondIndex][numNN]];

		return reachDist;		
	}

	private int getNNCount(int kNN, int instIndex) {

		//System.out.println("in getNNCount");
		int numNN = kNN;

		// if there are more neighbors with the same distance, take them too
		for (int i = kNN; i < distTable.length - 1; i++) {
			if (distTable[instIndex][distSorted[instIndex][i]] == distTable[instIndex][distSorted[instIndex][i+1]])
				numNN++;
			else
				break;
		}
		//System.out.println("returned from getNNCount.. "+numNN);
		return numNN;
	}

	private double getLocalReachDensity(int kNN, int instIndex) {

		// get the number of nearest neighbors:
		int numNN = getNNCount(kNN, instIndex);

		double lrd = 0;

		for (int i = 1; i <= numNN; i++) {
			lrd += getReachDistance(kNN, instIndex, distSorted[instIndex][i]);
		}
		lrd = (lrd == 0) ? 0 : numNN / lrd;

		return lrd;
	}	




	private int[] sortedIndices(double[] array){
		int[] sortedIndices = IntStream.range(0, array.length)
				.boxed().sorted((i, j) -> (int)(1000*(array[i] - array[j])))
				.mapToInt(ele -> ele).toArray();
		return sortedIndices;
	}
	
}
