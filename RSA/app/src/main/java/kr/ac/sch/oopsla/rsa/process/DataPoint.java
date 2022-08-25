package kr.ac.sch.oopsla.rsa.process;

public class DataPoint {
	
	double value;
	double time;
	
	boolean containsvalue = false;
	boolean containstime = false;
	
	DataPoint()
	{
		//creates and empty data point;
	}
	
	DataPoint(double invalue)
	{
		value = invalue;
		containsvalue = true;
	}
	
	DataPoint(double invalue, double intime)
	{
		value = invalue;
		time = intime;
		containsvalue = true;
		containstime = true;
	}
	
	double getValue()
	{
		if(containsvalue)
			return value;
		else
			return 0;
	}
	
	double getTime()
	{
		if(containstime)
			return time;
		else
			return 0;
	}
	
	void setValue(double invalue)
	{
		value = invalue;
		containsvalue = true;
	}
	
	void setTime(double intime)
	{
		time = intime;
		containstime = true;
	}
	
	boolean hasValue()
	{
		return containsvalue;
	}
	
	boolean hasTime()
	{
		return containstime;
	}
	
}
