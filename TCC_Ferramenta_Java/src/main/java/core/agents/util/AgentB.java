package core.agents.util;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import core.agents.ConversationsID;

public class AgentB extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void setup() {
		DFAgentDescription agentDescriptor = new DFAgentDescription();
		agentDescriptor.setName(getAID());

		try {
			DFService.register(this, agentDescriptor);
			
			System.out.println("agente "+this.getName());
			
			addBehaviour(new WakerBehaviour(this,2000) {
				private static final long serialVersionUID = 1L;
				

				@Override
				public void onStart()
				{
				
				}
				@Override
				public void onWake()
				{
					try
					{
						ACLMessage mensagem = new ACLMessage(ACLMessage.INFORM);
						mensagem.addReceiver(new AID("agenteA", AID.ISLOCALNAME));
						mensagem.setConversationId(ConversationsID.CREATE_EXPERTS);

						System.out.println("Pedido  Feito");
																		
						myAgent.send(mensagem);
					
					}catch (Exception e)
					{
						// TODO: handle exception
					}
				}
				
			});
			
			
			

		} catch (FIPAException e) {

			e.printStackTrace();
		}
	}

	public void takeDown() 
	{
		System.out.println(this.getLocalName() + " says: Bye");
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

//		public Comportamento(Agent agent, int time) {
//			// TODO Auto-generated constructor stub
//		}
//		public void onTicker() {
//			try {
//
//				ACLMessage mensagem = new ACLMessage(ACLMessage.CFP);
//				mensagem.addReceiver(new AID("simulator", AID.ISLOCALNAME));
//				mensagem.setConversationId(ConversationsID.SIMULATION_REQUEST);
//				mensagem.setContent("BAZA3.SA");
//
//				if (!aceitaProposta)
//					myAgent.send(mensagem);
//
//				MessageTemplate template = new MessageTemplate(
//						(MatchExpression) MessageTemplate
//								.MatchPerformative(ACLMessage.PROPOSE));
//				ACLMessage reciver = myAgent.receive(template);
//
//				if (reciver != null) {
//					System.out.println("Recebi " + contador + " proposta(s) ");
//					System.out.println("==>" + reciver.getContent());
//
//					if (contador == 4) {
//						ACLMessage resposta = reciver.createReply();
//						resposta.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
//						finishBehavior = true;
//					} else {
//						ACLMessage resposta = reciver.createReply();
//						resposta.setPerformative(ACLMessage.REJECT_PROPOSAL);
//					}
//
//					contador++;
//
//				}
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//		}
	
	
}
