package core.agents.behaviours;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import suport.financial.wallet.Stock;
import suport.util.database.mongoDB.dao.StockDao;
import core.agents.ConversationsID;
import core.agents.suport.WalletManagerAuxiliary;


public class UserAuthorization extends Behaviour {

	private static final long serialVersionUID = 1L;
	
	private StockDao stockDao;
	private ArrayList<Stock> userAuthorizations;
	
	private String userName;
	private Map<String, ArrayList<Stock>> infoExperts;
	private WalletManagerAuxiliary walletManagerAuxiliary;
	
	public UserAuthorization(String userName,Map<String, ArrayList<Stock>>infoExperts,WalletManagerAuxiliary walletManagerAuxiliary)
	{
		this.stockDao = new StockDao();
		this.userName=userName;
		this.infoExperts=infoExperts;
		this.walletManagerAuxiliary=walletManagerAuxiliary;
		this.walletManagerAuxiliary.putInfoExperts(this.infoExperts);
		
//		JOptionPane.showMessageDialog(null, "<Comportamento> walletManagerAuxiliary="+this.walletManagerAuxiliary.getQuota()
//				+"\n InfoExperts "+this.infoExperts+"-"+this.infoExperts.size());
		
	}

	@Override
	public void action() {
		try {
				this.userAuthorizations = this.stockDao.getStocksSuggestionWithUserAuthorized(userName);
									
			if (this.userAuthorizations != null && this.userAuthorizations.size() > 0)
			{
//				JOptionPane.showMessageDialog(null, "<Comportamento> User:"+userName+" objeto "+this.userAuthorizations
//						+" \n primeiro objeto  sugestion "+this.userAuthorizations.get(0).getSuggestion());

				ACLMessage msg = null;
				String expertName = "";
				double value = 0;
				for (Stock s : this.userAuthorizations) 
				{// TODO LOG
				switch (s.getSuggestion()) {
					case ConversationsID.BUY_APPROVED:
					{
						// Looking for Expert Name
						for (Entry<String, ArrayList<Stock>> e : this.infoExperts.entrySet()) 
						{
							expertName = e.getKey();
							for (Stock stock : e.getValue()) 
							{
								if (stock.getCodeName().equalsIgnoreCase(s.getCodeName()))
								{
									value = this.walletManagerAuxiliary.approveOrderBuy(expertName);
									if (value > 0) 
									{
										msg = new ACLMessage(ACLMessage.AGREE);
										msg.setConversationId(ConversationsID.EXPERT_ORDER_BUY);
										msg.setContent(s.getCodeName() + "_" + value);
										msg.addReceiver(new AID(expertName,AID.ISLOCALNAME));
										// TODO LOG
										System.out.println( "Manager : Ordem de compra autorizada para "+ expertName);
										myAgent.send(msg);
										stock.setSuggestion(0);
										this.stockDao.updateStock(stock);
										JOptionPane.showMessageDialog(null, "Compra aprovada msg enviada para :"+expertName);
									}
									break;
								}

							}

						}
					}
						break;
					case ConversationsID.BUY_REFUSED: {
						// Looking for Expert Name
						for (Entry<String, ArrayList<Stock>> e : this.infoExperts.entrySet()) 
						{
							expertName = e.getKey();
							for (Stock stock : e.getValue())
							{
								if (stock.getCodeName().equalsIgnoreCase(s.getCodeName()))
								{
									msg = new ACLMessage(ACLMessage.REFUSE);
									msg.setConversationId(ConversationsID.EXPERT_ORDER_BUY);
									msg.setContent(s.getCodeName());
									msg.addReceiver(new AID(expertName, AID.ISLOCALNAME));

									myAgent.send(msg);
									msg=null;
									stock.setSuggestion(0);
									this.stockDao.updateStock(stock);
								}
									break;
							}
							
						}
						
					}break;
					
					case ConversationsID.SELL_APPROVED: {
						// Looking for Expert Name
						for (Entry<String, ArrayList<Stock>> e : this.infoExperts
								.entrySet()) {
							expertName = e.getKey();
							for (Stock stock : e.getValue()) 
							{
								if (stock.getCodeName().equalsIgnoreCase(s.getCodeName()))
								{
									msg = new ACLMessage(ACLMessage.AGREE);
									msg.setConversationId(ConversationsID.EXPERT_ORDER_SELL);
									msg.setContent(s.getCodeName());
									msg.addReceiver(new AID(expertName, AID.ISLOCALNAME));

									myAgent.send(msg);
									msg=null;
									stock.setSuggestion(0);
									this.stockDao.updateStock(stock);
									JOptionPane.showMessageDialog(null, "Venda aprovada msg enviada para :"+expertName);
								}
									break;
							}
							
						}
						
					}
						break;
					case ConversationsID.SELL_REFUSED: {
						// Looking for Expert Name
						for (Entry<String, ArrayList<Stock>> e : this.infoExperts.entrySet()) 
						{
							expertName = e.getKey();
							for (Stock stock : e.getValue()) 
							{
								if (stock.getCodeName().equalsIgnoreCase(s.getCodeName()))
								{
									msg = new ACLMessage(ACLMessage.REFUSE);
									msg.setConversationId(ConversationsID.EXPERT_ORDER_SELL);
									msg.setContent(s.getCodeName());
									msg.addReceiver(new AID(expertName, AID.ISLOCALNAME));

									myAgent.send(msg);
									msg=null;
									stock.setSuggestion(0);
									this.stockDao.updateStock(stock);
								}
									break;
							}
							
						}
						
					}break;
					default:
						break;
					}
					
				
				}//fim For
			}
		} catch (Exception e) {// TODO LOG
			e.printStackTrace();
		}
	}

	@Override
	public boolean done() {
		if (this.userAuthorizations.size() == 0 || this.userAuthorizations==null) {// TODO LOG
			System.out.println("Comportamento de escuta encerrado");
			
			return true;
		} else
			return false;
	}
	

}
