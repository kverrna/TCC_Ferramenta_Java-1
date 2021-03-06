package suport.financial.partternsCandleStickTest;

import java.util.Date;

import junit.framework.Assert;

import suport.financial.partternsCandleStick.CandleStick;

public class CandlestickTest {

	private CandleStick candlestick;

	public void setUp() throws Exception {
		candlestick = null;
	}

	public void testCandleStickDoubleDoubleDoubleDoubleDoubleDate() {
		candlestick = new CandleStick(1.0, 1.0, 1.0, 1.0, 1.0, new Date());
		Assert.assertNotNull(candlestick);
	}

	public void testCandleStickStringStringStringStringStringDate() {

		candlestick = new CandleStick("1", "1", "1", "1", "1", new Date());
		Assert.assertNotNull(candlestick);
	}

	public void testGetOpen() {
		candlestick = new CandleStick(1.0, 1.0, 1.0, 1.0, 1.0, new Date());
		Assert.assertEquals(1.0, candlestick.getOpen(), 0.0);

		candlestick = new CandleStick("1", "1", "1", "1", "1", new Date());
		Assert.assertEquals(1.0, candlestick.getOpen(), 0.0);

	}

	public void testGetHigh() {
		candlestick = new CandleStick(1.0, 1.0, 1.0, 1.0, 1.0, new Date());
		Assert.assertEquals(1.0, candlestick.getHigh(), 0.0);

		candlestick = new CandleStick("1", "1", "1", "1", "1", new Date());
		Assert.assertEquals(1.0, candlestick.getHigh(), 0.0);
	}

	public void testGetLow() {

		candlestick = new CandleStick(1.0, 1.0, 1.0, 1.0, 1.0, new Date());
		Assert.assertEquals(1.0, candlestick.getLow(), 0.0);

		candlestick = new CandleStick("1", "1", "1", "1", "1", new Date());
		Assert.assertEquals(1.0, candlestick.getLow(), 0.0);
	}

	public void testGetClose() {

		candlestick = new CandleStick(1.0, 1.0, 1.0, 1.0, 1.0, new Date());
		Assert.assertEquals(1.0, candlestick.getClose(), 0.0);

		candlestick = new CandleStick("1.0", "1.0", "1.0", "1.0", "1.0",
				new Date());
		Assert.assertEquals(1.0, candlestick.getClose(), .0);
	}

	public void testGetVolume() {

		candlestick = new CandleStick(1.0, 1.0, 1.0, 1.0, 1.0, new Date());
		Assert.assertEquals(1.0, candlestick.getVolume(), 0.0);

		candlestick = new CandleStick("1", "1", "1", "1", "1", new Date());
		Assert.assertEquals(1.0, candlestick.getVolume(), 0.0);
	}

	public void testGetDate() {

		candlestick = new CandleStick(1.0, 1.0, 1.0, 1.0, 1.0, new Date());
		Assert.assertEquals(new Date(), candlestick.getDate());

		candlestick = new CandleStick("1", "1", "1", "1", "1", new Date());
		Assert.assertEquals(new Date(), candlestick.getDate());
	}

	public void testGetInformation() {
		candlestick = new CandleStick(1.0, 1.0, 1.0, 1.0, 1.0, new Date());
		Assert.assertNotNull(candlestick.getInformation());

	}

}
