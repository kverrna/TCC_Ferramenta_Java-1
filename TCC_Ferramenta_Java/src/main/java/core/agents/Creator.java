package core.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import jade.wrapper.AgentController;
import jade.wrapper.PlatformController;

import java.io.IOException;

import suport.util.Log;
import suport.util.database.mongoDB.dao.OrdersCreateDao;
import suport.util.database.mongoDB.dao.UserInfoDao;
import suport.util.database.mongoDB.pojo.OrdersCreate;

public class Creator extends Agent {

	private static final long serialVersionUID = 1L;
	private OrdersCreateDao orderCreateDao;
	private OrdersCreate newOrderCreate;
	private UserInfoDao userInfoDao;
	private String userLogged;
	private Log log;
	private Creator creator;
	private Logger loggetJade;
	protected void setup() 
	{
		
		orderCreateDao = new OrdersCreateDao();
		creator=this;
		try {
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			DFService.register(this, dfd);
			
     			log= new Log(this.getName()); 
				log.info("Registro Paginas Amarelas");
				log.info("Iniciando comportamento de escuta do Grails");
			
			addBehaviour(new ListenGrails( (long) 10));

		} catch (Exception e) { 
			e.printStackTrace();
			log.error("Msg:"+e.getMessage()+"Causa:"+e.getCause());
			
		}
	}

	protected void takedown() {
		try {//TODO
			// Unregister the agent in plataform
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			DFService.deregister(this, dfd);

			log.info("Desregistrando das paginas amarelas");
		} catch (Exception e) 
		{
			e.printStackTrace();
			log.error("Msg:"+e.getMessage()+"Causa:"+e.getCause());
		}
	}

	private class ListenGrails extends TickerBehaviour {
		

		public ListenGrails( Long period) {
			super(creator, period);
			
			
			log.debug("Comportamento de escuta Iniciado");

		}

		private static final long serialVersionUID = 1L;

		@Override
		protected void onTick() { 
			userInfoDao = new UserInfoDao();
			newOrderCreate = orderCreateDao.getNewOrderCreate();
			userLogged = userInfoDao.userLogged();

			
			
			if (!(newOrderCreate == null)) {
				createManagerForUser(
						"Manager_" + newOrderCreate.getUserIndetifier(),
						newOrderCreate.getUserPerfil(),
						newOrderCreate.getUserValue());
				log.info("Novo usuario criado:"+newOrderCreate.getUserIndetifier()+" - "+newOrderCreate.getUserPerfil()+" - "+newOrderCreate.getUserValue());
			}
			if (!(userLogged == null)) {
				
				log.info("Usuario Logado :"+userLogged);
				log.debug("Iniciar comportamento OneShort para avisar Gestor responsavel");
				addBehaviour(new OneShotBehaviour(creator) 
				{
					private static final long serialVersionUID = 1L;
					@Override
					public void action() {//TODO
						creator.log.debug("Comportamento OneShor iniciado");
						ACLMessage message = new ACLMessage(ACLMessage.INFORM);
						message.setLanguage(ConversationsID.LANGUAGE);
						message.setPerformative(ACLMessage.INFORM);
						message.setConversationId(ConversationsID.USER_LOGGED);
						message.setContent(userLogged);
						message.addReceiver(new AID("Manager_" + userLogged,
								AID.ISLOCALNAME));
					
						myAgent.send(message);
						//TODO
						creator.log.info("Gestor :Manager_"+userLogged+" Alertado usuario logado");
						//System.out.println("Gestor :Manager_"+userLogged+" Alertado usuario logado");
					}
				});
			}
		}

		/**
		 * This method create a team for each users.
		 * 
		 * @param nameAgentManager
		 *            : It's a manager's name formated for Manager_+ <Use-login>
		 * @param userPerfilType
		 *            : It's the profile type of user. Can be
		 *            "Conservador","Moderado" and "Corajoso"
		 * @param userValue
		 *            : It's the user's value for investiments
		 */
		public void createManagerForUser(final String nameAgentManager,
				int userPerfilType, double userValue) 
		{
			PlatformController container = getContainerController();
			try {//TODO
				log.info("Criando Gertor:"+nameAgentManager);
				// Xms128m
				Object[] argument;
				argument = new Object[1];
				argument[0] = "Xms1024m";

				AgentController agentController = container.createNewAgent(
						nameAgentManager, "core.agents.Manager", argument);
				agentController.start();

			} catch (Exception e) {
				e.printStackTrace();//TODO
				log.error("Msg:"+e.getMessage()+"Causa:"+e.getCause());
			}
			addBehaviour(new OneShotBehaviour() {
				private static final long serialVersionUID = 1L;

				@Override
				public void action() {
					try {
						ACLMessage message = new ACLMessage(ACLMessage.INFORM);
						message.addReceiver(new AID(nameAgentManager,
								AID.ISLOCALNAME));
						message.setLanguage(ConversationsID.LANGUAGE);
						
						message.setConversationId(ConversationsID.CREATE_EXPERTS);
						message.setContentObject(newOrderCreate);
						myAgent.send(message);
						//TODO
						creator.log.info("Solicitando criacao dos Experts do gestor:"+nameAgentManager);

					} catch (IOException e) {
						e.printStackTrace();//TODO
						log.error("Msg:"+e.getMessage()+"Causa:"+e.getCause());
					}
				}
			});
		}
	}

	public void dropManagerForUser(String userIdentifier) {
	}
}
