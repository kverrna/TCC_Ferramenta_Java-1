package core.agents.util;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import suport.util.database.mongoDB.pojo.OrdersCreate;
import core.agents.ConversationsID;
import core.agents.behaviours.CommunicationBehaviour;
import core.agents.behaviours.CreateExpertsAgents;

public class AgenteA extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CreateExpertsAgents behaviourCreateAgents;
	private CommunicationBehaviour communication;
	private OneShotBehaviour behaviourTest;
	private SimpleBehaviour behaviourTest2;
	
	public void setup() {
		DFAgentDescription agentDescriptor = new DFAgentDescription();
		agentDescriptor.setName(getAID());

		try {
			DFService.register(this, agentDescriptor);
			
			OrdersCreate orderCreate= new OrdersCreate();
			orderCreate.setUserIndetifier("ramon");
			orderCreate.setUserPerfil(0);
			orderCreate.setUserValue(1000);
			
			
			System.out.println("agente "+this.getName());
			behaviourCreateAgents=new CreateExpertsAgents(this, orderCreate);
		
			communication = new CommunicationBehaviour(this);		
			communication.addConversationIdToListen(ConversationsID.CREATE_EXPERTS, behaviourCreateAgents, ACLMessage.INFORM);
			communication.start();
			
			
			
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

}
