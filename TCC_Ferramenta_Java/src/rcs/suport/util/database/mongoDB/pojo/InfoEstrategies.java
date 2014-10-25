package rcs.suport.util.database.mongoDB.pojo;

import rcs.suport.financial.partternsCandleStick.CandleStick;

public class InfoEstrategies {
	
	private String strategyName;
	private String periodicity;
	private CandleStick buyed;
	private CandleStick selled;
	private double profit;
	
	
	
	public String getStrategyName() {
		return strategyName;
	}
	public void setStrategyName(String strategyName) {
		this.strategyName = strategyName;
	}
	public String getPeriodicity() {
		return periodicity;
	}
	public void setPeriodicity(String periodicity) {
		this.periodicity = periodicity;
	}
	public CandleStick getBuyed() {
		return buyed;
	}
	public void setBuyed(CandleStick buyed) {
		this.buyed = buyed;
	}
	public CandleStick getSelled() {
		return selled;
	}
	public void setSelled(CandleStick selled) {
		this.selled = selled;
	}
	public double getProfit() {
		return profit;
	}
	public void setProfit(double profit) {
		this.profit = profit;
	}

}
