package core.agents.behaviours;

import java.util.ArrayList;

import core.agents.ConversationsID;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import suport.financial.wallet.Wallet;
import suport.util.InfoConversations;
import suport.util.database.mongoDB.dao.WalletDao;
import suport.util.database.mongoDB.pojo.OrdersCreate;

public class CreateExpertsAgents implements ProcedureBehaviour{
	
	/*
	 * Recebe dados do cliente através de mensagem;
		Cria uma Carteira no Banco de Dados;
		Procura um Agente Hunter Local e Solicita Ações;
		Cria Agentes experts e divide as ações entre os agentes;
	 */
	private SequentialBehaviour sequentialBehaviour;
	private OneShotBehaviour createWallet;
	private SimpleBehaviour requestSocks;
	private OneShotBehaviour requestStock;
	private OneShotBehaviour createExpertsAgents;
	private OrdersCreate orderCreate;
	private InfoConversations info;
	
	private WalletDao walletDao;
	
	private Agent agent;
	

	public CreateExpertsAgents(Agent agent,OrdersCreate orderCreate)
	{
		this.agent=agent;
		this.orderCreate=orderCreate;
		this.walletDao= new WalletDao();
		this.info=new InfoConversations(orderCreate.getUserIndetifier(), orderCreate.getUserPerfil());
		
		this.sequentialBehaviour= new SequentialBehaviour(agent)
		{
			
			private static final long serialVersionUID = 1L;

			public int onEnd()
			{
				System.out.println("Comportamento sequencial Concluido");
			
				return 0;
			}
		};
		
		buildBehaviour();
		
		
	}
	@SuppressWarnings("unused")
	private CreateExpertsAgents(){}

	void buildBehaviour()
	{
		this.createWallet= new OneShotBehaviour(this.agent)
		{
			
			private static final long serialVersionUID = 1L;

			@Override
			public void action() 
			{
				System.out.println("Comportamento Criar Carteira!");
				walletDao.insertWallet(new Wallet(orderCreate.getUserIndetifier(), orderCreate.getUserValue(), 0, 0));
				
			}
		};
		this.requestSocks= new SimpleBehaviour(this.agent)
		{
			
			private static final long serialVersionUID = 1L;
			private boolean retorno=false;
			private MessageTemplate filter;
			private ACLMessage msg;
			private String hunterName;
			private ACLMessage hunterMessage;
			@Override
			public void onStart()
			{
				filter = MessageTemplate.MatchConversationId(ConversationsID.STOCKS_HUNTER_SUGGESTIONS);
				hunterMessage=null;
			}
			@Override
			public boolean done() {
				
				return retorno;
			}
			
			@Override
			public void action() 
			{
				
				try {
					
					DFAgentDescription dfd = new DFAgentDescription();
					ServiceDescription service = new ServiceDescription();
					service.setType("StockHunter");
					service.setName("Hunter");

					dfd.addServices(service);
					DFAgentDescription[] result = DFService.search(agent, dfd);
					if (result != null)
						hunterName = result[0].getName().toString();

					hunterMessage = new ACLMessage(ACLMessage.CFP);
				//	hunterMessage.addReceiver(new AID(result[0].getName().getName(),AID.ISLOCALNAME));
					hunterMessage.addReceiver(result[0].getName());
					hunterMessage.setConversationId(ConversationsID.STOCKS_HUNTER_SUGGESTIONS);
					hunterMessage.setContentObject(info);
					
					myAgent.send(hunterMessage);
					hunterMessage=null;

				} catch (Exception e) {// TODO LOG
					e.printStackTrace();
				}
				
				msg=myAgent.receive(filter);
				
				if(msg!=null)
				{
					try {
						
					//	InfoConversations temp=(InfoConversations)msg.getContentObject();
						System.out.println(" Resposta Hunter => "+msg.getContentObject());
//						if(temp.getStockList()!=null &&temp.getStockList().size()>0)
//						{
//							System.out.println("Comportamento Requesitar Ativos");
//							info=temp;
//							retorno=true;
//						}else
//							{
//								myAgent.send(hunterMessage);
//								retorno=false;
//							}
					
					} catch (UnreadableException e) {
						retorno =false;
						e.printStackTrace();
					}

				}else block();
				
				
			}
		};
		this.requestStock=new OneShotBehaviour(agent)
		{
			
			private static final long serialVersionUID = 1L;
			private String hunterName;
			private ACLMessage hunterMessage;
			
			@Override
			public void action() {
				
				try {
					
				
					DFAgentDescription dfd = new DFAgentDescription();
					ServiceDescription service = new ServiceDescription();
					service.setType("StockHunter");
					service.setName("Hunter");

					dfd.addServices(service);
					DFAgentDescription[] result = DFService.search(agent, dfd);
					if (result != null)
					hunterName = result[0].getName().getLocalName();

					hunterMessage = new ACLMessage(ACLMessage.CFP);
					hunterMessage.addReceiver(new AID(hunterName,AID.ISLOCALNAME));
					//hunterMessage.addReceiver(result[0].getName());
					hunterMessage.setConversationId(ConversationsID.STOCKS_HUNTER_SUGGESTIONS);
					hunterMessage.setContentObject(info);
					
					System.out.println("=> Pedido feito ao "+hunterName);
					agent.send(hunterMessage);
			
					

				} catch (Exception e) {// TODO LOG
					e.printStackTrace();
				}
				
			}
		};
		this.createExpertsAgents=new OneShotBehaviour(this.agent)
		{
			
			
			private static final long serialVersionUID = 1L;

			@Override
			public void action() {
				
				System.out.println("Comportamento criar agentes especialistas");
				
			}
		};
		
	}
	/**
	 * Start the sequential Behaviour
	 */
	public void start()
	{
			this.sequentialBehaviour.addSubBehaviour(createWallet);
			//this.sequentialBehaviour.addSubBehaviour(requestSocks);
			this.sequentialBehaviour.addSubBehaviour(requestStock);
			this.sequentialBehaviour.addSubBehaviour(createExpertsAgents);
			
			this.agent.addBehaviour(sequentialBehaviour);
	}
	/**
	 * 
	 * @return return the sequential Behaviour without start it
	 */
	public Behaviour getBehaviour()
	{
		this.sequentialBehaviour.addSubBehaviour(createWallet);
		//this.sequentialBehaviour.addSubBehaviour(requestSocks);
		this.sequentialBehaviour.addSubBehaviour(requestStock);
		this.sequentialBehaviour.addSubBehaviour(createExpertsAgents);
		
		return this.sequentialBehaviour;
	}
	
	

}
