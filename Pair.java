/**
 * File Name: Pair
 * 
 * Author: Sameera Bammidi
 *Created on: 11/10/2017
 * 
 */
public class Pair {

	int id1 ; 
	int id2 ;
	
	double score = -1; // the similarity score for this pair , initialized with a meaning less value
	
	public Pair(int id1, int id2) {
		super();
		this.id1 = id1;
		this.id2 = id2;
	}

	public Pair(int id1, int id2, double sscore) {
		super();
		this.id1 = id1;
		this.id2 = id2;
		this.score = sscore ;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result1 = 1;
		result1 = prime * result1 + id1;
		result1 = prime * result1 + id2;
		
		int result2 = 1;
		result2 = prime * result2 + id2;
		result2 = prime * result2 + id1;
		
		
		return (result1+result2) / 2;
	} // symmetric hash code so order should not matter => intended for pair
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		// again making sure that the order doesn't matter
		Pair other = (Pair) obj;
		if (id1 == other.id1 && id2 == other.id2)
			return true;
		if (id2 == other.id1 && id2 == other.id1)
			return true;
		
		return false;
	}


	public double getScore() {
		return score;
	}


	public void setScore(double score) {
		this.score = score;
	}	
	
}
