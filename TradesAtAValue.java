/**
 * @author Sameera Bammidi
 * Created On: 12/05/217
 *
 */

import java.sql.Date;

public class TradesAtAValue { // to be used in such a way that one object of this class type is associate per each unique <Date,Price> for an Insider's set
/*
 * All transactions on a given date at a given price point and a given type (purchase or sale)
 * 
 */
 
double price_point ; // unique along with date
Date date ;
public TradesAtAValue(double price_point, Date date) {
	super();
	this.price_point = price_point;
	this.date = date;
}
@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((date == null) ? 0 : date.hashCode());
	long temp;
	temp = Double.doubleToLongBits(price_point);
	result = prime * result + (int) (temp ^ (temp >>> 32));
	return result;
}
@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	TradesAtAValue other = (TradesAtAValue) obj;
	if (date == null) {
		if (other.date != null)
			return false;
	} else if (!date.equals(other.date))
		return false;
	if (Double.doubleToLongBits(price_point) != Double.doubleToLongBits(other.price_point))
		return false;
	return true;
}
@Override
public String toString() {
	return "TradesAtAValue [price_point=" + price_point + ", date=" + date + "]";
} 



}
