package core.agents.util;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import javax.swing.JOptionPane;

import suport.financial.wallet.Stock;
import suport.util.InfoConversations;
import suport.util.database.mongoDB.pojo.OrdersCreate;
import core.agents.ConversationsID;

public class AgenteA extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private InfoConversations info;
	private AgenteA agentA;
	public void setup() {
		DFAgentDescription agentDescriptor = new DFAgentDescription();
		agentDescriptor.setName(getAID());
		agentA=this;
		
		info= new InfoConversations("RAMON", 0);

		try {
			DFService.register(this, agentDescriptor);
			
			OrdersCreate orderCreate= new OrdersCreate();
			orderCreate.setUserIndetifier("ramon");
			orderCreate.setUserPerfil(0);
			orderCreate.setUserValue(1000);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
			
		addBehaviour(new TickerBehaviour(agentA,5000) {
		
			private static final long serialVersionUID = 1L;
			String hunterName;
			@Override
			protected void onTick() {
				
				try {
					DFAgentDescription dfd = new DFAgentDescription();
					ServiceDescription service = new ServiceDescription();
					service.setType("BolsaDeValores");
					service.setName("Bovespa");

					dfd.addServices(service);
					DFAgentDescription[] result = DFService.search(agentA, dfd);
					if (result != null)
						hunterName = result[0].getName().getLocalName();

					JOptionPane.showMessageDialog(null, "Investidor: Achei o Agente "+hunterName+
							"\n Vou pedir Sugestõs de ações com Risco entre 15% e 30%");
					
					ACLMessage hunterMessage = new ACLMessage(ACLMessage.CFP);
					hunterMessage.addReceiver(result[0].getName());
					hunterMessage.setConversationId(ConversationsID.STOCKS_HUNTER_SUGGESTIONS);
					hunterMessage.setContentObject(agentA.info);
				
					agentA.send(hunterMessage);

					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
//		addBehaviour(new OneShotBehaviour(agentA) 
//		{
//			private static final long serialVersionUID = 1L;
//			String hunterName;
//
//			public void action() {
//				try {
//					DFAgentDescription dfd = new DFAgentDescription();
//					ServiceDescription service = new ServiceDescription();
//					service.setType("BolsaDeValores");
//					service.setName("Bovespa");
//
//					dfd.addServices(service);
//					DFAgentDescription[] result = DFService.search(agentA, dfd);
//					if (result != null)
//						hunterName = result[0].getName().getLocalName();
//
//					JOptionPane.showMessageDialog(null, "Investidor: Achei o Agente "+hunterName+
//							"\n Vou pedir Sugestõs de ações com Risco entre 15% e 30%");
//					
//					ACLMessage hunterMessage = new ACLMessage(ACLMessage.CFP);
//					hunterMessage.addReceiver(result[0].getName());
//					hunterMessage.setConversationId(ConversationsID.STOCKS_HUNTER_SUGGESTIONS);
//					hunterMessage.setContentObject(agentA.info);
//					agentA.send(hunterMessage);
//
//					
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
		
		addBehaviour(new CyclicBehaviour(agentA) {
			
			private static final long serialVersionUID = 1L;
			private ACLMessage msg=null;
			@Override
			public void action() {
				
				msg= agentA.receive();
				if(msg!=null)
				{
					if(msg.getConversationId()==ConversationsID.STOCKS_HUNTER_SUGGESTIONS)
					{
						
						StringBuilder string= new StringBuilder();
						
						try {
							agentA.info = (InfoConversations) msg.getContentObject();
							string.append("Investidor: Quantidade de ações recebidas "+agentA.info.getStockList().size());
							
							for(Stock s:agentA.info.getStockList())
							{
								string.append("\n Ação: "+s.getCodeName() +" Risco Médio: "+s.getStandardDeviation_30()*100+"%");
							}
							JOptionPane.showMessageDialog(null, string);
							
						} catch (UnreadableException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
						
					}
					
				}else block();
				
			}
		});
		
}		
public void takeDown() 
			{
				System.out.println(agentA.getLocalName() + " says: Bye");
				try {
					// Unregister the agent in plataform
					DFAgentDescription dfd = new DFAgentDescription();
					dfd.setName(getAID());
					DFService.deregister(this, dfd);
					// kill experts
					
				} catch (Exception e) {// TODO LOG
					e.printStackTrace();
				}
			}
	

}
