package rcs.suport.financial.strategy;

import java.util.ArrayList;

import rcs.suport.financial.partternsCandleStick.BearishEngulfing;
import rcs.suport.financial.partternsCandleStick.BullishEngulfing;

public class Bearish_Bullish_Strategy implements Strategy{

	private BearishEngulfing bearish;
	private BullishEngulfing bullish;
	
	private ArrayList<Double>values;
	
	public Bearish_Bullish_Strategy()
	{
		this.values=new ArrayList<Double>();
		this.bearish= new BearishEngulfing();
		this.bullish= new BullishEngulfing();
		
	}
	
	@Override
	public String makeOrder() 
	{
		String order=null;
		
		int bearish_value=this.bearish.findCandleSticksPatterns().size();
		int bullish_value=this.bullish.findCandleSticksPatterns().size();
		
		if(bearish_value>0 && bullish_value>0) order ="nothing";
		else 
		{
			if(bearish_value>0)order="Sell";
			if(bullish_value>0)order="Buy";
		}
				
		return order;
	}

	@Override
	public void addValue(double value) 
	{
		this.values.add(value);
		
		
		
	}

}
