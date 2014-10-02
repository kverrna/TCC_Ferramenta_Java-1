package rcs.core.agents;



import jade.core.AID;
import jade.core.Agent;
import jade.core.Service;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.df;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.wrapper.AgentController;
import jade.wrapper.PlatformController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Iterator;

import rcs.suport.financial.wallet.Stock;
import rcs.suport.util.database.mongoDB.pojo.OrdersCreate;



public class Manager  extends Agent{

private static final long serialVersionUID = 1L;
private OrdersCreate user;	
private Map<String,ArrayList<Stock>> infoExperts;
private Manager manager;
protected void setup()
	{
		/*
		 * Create the hashMap with informations : FullName and an list of Stock Names
		 */
		infoExperts=new HashMap<String,ArrayList<Stock>>();
		manager=this;
		try{
			//create the agent description of ifself
			DFAgentDescription dfd=new DFAgentDescription();
			dfd.setName(getAID());
			DFService.register(this, dfd);
			
			System.out.println("I'm live... My name is "+this.getLocalName());
			
			//creating the experts 
			
			addBehaviour(new CyclicBehaviour(manager) {
				
				@Override
				public void action() {
					
					try
					{
						ACLMessage message= myAgent.receive();
						if(message!=null)
						{
							user=(OrdersCreate)message.getContentObject();
							initWork(manager, 1,user.getUserValue(), "Expert_"+user.getUserIndetifier());
						
							
						}else block();
						
					}catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			});
			
			//Test Yellow pages 
			
	
			addBehaviour(new OneShotBehaviour(manager) {
				String hunterName;
				@Override
				public void action() {
							
					try
					{
						DFAgentDescription dfd= new DFAgentDescription();
						
						//Service 
						ServiceDescription service= new ServiceDescription();
						service.setType("Hunt");
						service.setName("Hunting");
						
						dfd.addServices(service);
						
						//request service on yellow pages 
						DFAgentDescription[] result = DFService.search(manager, dfd);
						
						for(int i=0; i< result.length;i++)
						{
							String out =result[i].getName().getLocalName()+" Prove";
							
							Iterator<ServiceDescription> iter=result[i].getAllServices(); 
							while(iter.hasNext())
							{
								ServiceDescription sd=(ServiceDescription)iter.next();
								out+=" "+sd.getName();
							}
							System.out.println(out);
							
							hunterName=result[i].getName().getLocalName();
						}
						
						ACLMessage hunterMessage=new ACLMessage(ACLMessage.INFORM);
						hunterMessage.addReceiver(new AID(hunterName, AID.ISLOCALNAME));
						hunterMessage.setLanguage("English");
						hunterMessage.setOntology("Hello");
						hunterMessage.setContent("Hello my friend");
						
						myAgent.send(hunterMessage);
						
						
					}catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			});
	/*		
			addBehaviour(new CyclicBehaviour(this) {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void action() {
					
				ACLMessage message= myAgent.receive();
				if(message!=null)
				{

					try {
						//Create the expert agent
						 user=(OrdersCreate)message.getContentObject();
						createExpertAgents("Expert_"+user.getUserIndetifier(), user.getUserValue(),0);
						
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				}else block();
			  //Agents Expert are live now 
				
				
				
				
				//Testando capacidade em enviar arrayList
				ACLMessage messageExpert;
				ArrayList<String> arrayList=new ArrayList<String>();
				arrayList.add("Ramon");
				arrayList.add("Cruz");
				arrayList.add("Silva");
				
				System.out.println("Map Size : "+infoExperts.size());
				for(Entry<String, ArrayList<Stock>>expert : infoExperts.entrySet())
				{
					System.out.println("Manager Sending message");
					System.out.println(expert.getKey());
					
					try {
						messageExpert= new ACLMessage(ACLMessage.INFORM);
						messageExpert.addReceiver(new AID(expert.getKey(),AID.ISLOCALNAME));
						messageExpert.setOntology("control");
						messageExpert.setLanguage("English");
						messageExpert.setContentObject(arrayList);
						myAgent.send(messageExpert);
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				//Fim teste de envio de ArrayList 
				
					
				}
			});
			
			*/
			
		}catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	protected void takeDown()
	{
		System.out.println(this.getLocalName()+" says: Bye");
		try
		{
		 //Unregister the agent in plataform 
			DFAgentDescription dfd=new DFAgentDescription();
			dfd.setName(getAID());
			DFService.deregister(this, dfd);
			
			//kill experts 
			dropExpertAgent();
			
		}catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
//Methods for manager the experts team
private void createExpertAgents(String name,double value,int userPerfil)
{
	PlatformController container=getContainerController();
	AgentController  agentController;
	int qtdExperts=0;
	
	//calculate the numbers of experts agents 
	switch (userPerfil) {
	case 0://Corajoso
		qtdExperts=2;
	break;
	case 1://Moderado
		qtdExperts=3;
	break;
	case 2://Conservador
		qtdExperts=7;
	break;

	default:
		break;
	}
	
	try {
		for(int i=0;i<qtdExperts;i++)
		{
			agentController=container.createNewAgent(name+"["+(i+1)+"]", "rcs.core.agents.Expert", null);
			agentController.start();
			infoExperts.put(name+"["+(i+1)+"]" , null);
		}
	} 
	catch (Exception e) 
	{
		e.printStackTrace();
	}
	
}
private void dropExpertAgent ()
{
	PlatformController container=getContainerController();
	try
	{
		for(Entry<String,ArrayList<Stock>>expertAgent:infoExperts.entrySet())
		{
			System.out.println(expertAgent.getKey()+"killed");
			AgentController agentController=container.getAgent(expertAgent.getKey());
			agentController.kill();
		}
		
	}catch(Exception e)
	{
		e.printStackTrace();
	}
}

private void initWork(Agent agent,int userPerfil,double userValue,String name)
{
	Map<String,Stock> stocks;
	ArrayList<Stock> listStocks;
	PlatformController container=getContainerController();
	AgentController  agentController;
	//Contact an hunter 
	//Yellow pages
	
	
	
	//Create the experts agents
	try
	{
		switch (userPerfil) {
		case 0://Corajoso
		{
			for(int i=0;i<2;i++)
			{
				agentController=container.createNewAgent(name+"["+(i+1)+"]", "rcs.core.agents.Expert", null);
				agentController.start();
				infoExperts.put(name+"["+(i+1)+"]" , null);
			}
		}break;
		case 1://Moderado
		{
			for(int i=0;i<3;i++)
			{
				agentController=container.createNewAgent(name+"["+(i+1)+"]", "rcs.core.agents.Expert", null);
				agentController.start();
				infoExperts.put(name+"["+(i+1)+"]" , null);
			}
		}break;
		case 2://Conservador
		{
			for(int i=0;i<7;i++)
			{
				agentController=container.createNewAgent(name+"["+(i+1)+"]", "rcs.core.agents.Expert", null);
				agentController.start();
				infoExperts.put(name+"["+(i+1)+"]" , null);
			}
		}break;

		default:
		{
			for(int i=0;i<1;i++)
			{
				agentController=container.createNewAgent(name+"["+(i+1)+"]", "rcs.core.agents.Expert", null);
				agentController.start();
				infoExperts.put(name+"["+(i+1)+"]" , null);
			}
		}
			break;
		}
	}catch(Exception e)
	{
		e.printStackTrace();
	}
	
	
	
}


}