import java.io.IOException;

import jade.core.AID;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.util.ExtendedProperties;
import jade.util.leap.Properties;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.lang.Thread;

//shut down each time to have one platform/main container running. 
//agent: entity/program that has a behaviour
public class Enchere extends Agent {
	
	public String products[] = {"Potatoes","Tomatoes","Socks"};
	public Integer budgets[][] = {{80000,10000,40000},{70000,9000,50000},{50000,7000,30000}};
	public Integer prices[] = {0,0,0};
	public boolean ag_arrets[] = {false,false,false};
	public int iteration = 0;
	public int numberAgents = 3;
	public int[] max = new int[3];
	public int reserve = 15000;
	public long startTime;
	public long elapsedTime;
	public long endTime;
	public long time = 100;//in seconds
	public boolean ended = false;
	public int sleepTime = 1000;
	
	//method 1 : 
	public static void main(String[] args){
				Properties prop = new ExtendedProperties();
				Properties a = new ExtendedProperties();
					// demander la fenêtre de contrôle
				prop.setProperty(Profile.GUI, "true");
				a.setProperty(Profile.GUI, "true");
					// nommer les agents
				a.setProperty(Profile.AGENTS,"Vendeur:Enchere;A1:Enchere;A2:Enchere;A3:Enchere;Intermediaire:Enchere");
					// créer les profiles pour les conteneurs
				ProfileImpl profMain = new ProfileImpl(prop);
				ProfileImpl AgentContainer = new ProfileImpl(a);
					// lancer le conteneur principal
				jade.core.Runtime rt = jade.core.Runtime.instance();
				rt.createMainContainer(profMain);
					// lancer le conteneur d'agent
				rt.createAgentContainer(AgentContainer);
		
	}
		
		protected void setup() {
			
			if(this.getLocalName().equals("Vendeur")){
	            addBehaviour(new B0());
	        }
				
				if(this.getLocalName().equals("Vendeur")){
		            addBehaviour(new B1());
		        }
				if(this.getLocalName().equals("A2")){
		            addBehaviour(new B2());
		        }
		        if(this.getLocalName().equals("A1")){
		            addBehaviour(new B2());
		        }
		        if(this.getLocalName().equals("A3")){
		            addBehaviour(new B2());
		        } 
		        if(this.getLocalName().equals("Intermediaire")){
		            addBehaviour(new B3());
		        }
		        
				
	        
	        
		}
		
		//declare classes in same class or outside the class
				//oneshot: do action one time (program "action" method)
				//cyclic : do action an infinite amount of time (program "action" method)
				//generic : program done and action
		public class B0 extends OneShotBehaviour {
					
					ACLMessage m;
					public void action (){
						System.out.println("\nAUCTION for "+ products[iteration]+" has STARTED!!!!!!");
						startTime = System.nanoTime();
					}
		}
			
		public class B1 extends Behaviour{
			public void action(){
				
				
				
				ACLMessage m2 = new ACLMessage(ACLMessage.INFORM);
				
				//caculate time
				endTime = System.nanoTime();
				elapsedTime = endTime - startTime;
				System.out.println("Time is : "+(elapsedTime/100000000)+" secs\n");
				
				if(elapsedTime/100000000 >= time) {
					ended = true;
				}
				
					//envoie prix
				if(ended==false) {
					try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
					try {
						max[2]=iteration;
						m2.setContentObject(max);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					};
					m2.addReceiver(new AID("A1",AID.ISLOCALNAME));
					m2.addReceiver(new AID("A2",AID.ISLOCALNAME));
					m2.addReceiver(new AID("A3",AID.ISLOCALNAME));
					send(m2);
					
					//bloqué
					//receive max de Intermediaire

						
						
					ACLMessage m1 = this.myAgent.blockingReceive();//attendre msg de Intermediaire
					
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					int[] max_received = null;
					try {
						max_received = (int[])m1.getContentObject();
						max_received[2]=iteration;
					} catch (UnreadableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					max = max_received;
					
					System.out.println("B1 Max is : "+max_received[0]+ "\n");
					System.out.println("B1 NEW MAX is : "+max[0]+ "\n");
				}
				
				
				
				
				
				
				
				
				//like the semaphore P, the agent is blocked here until it receives another message
				//enlever pour vendeur
				//this.myAgent.blockingReceive();
			}
			//execute action until done condition is verified
			public boolean done() {
				//message is not empty so behavior success
				if(ended) {
					System.out.println("THE END");
					System.out.println(Arrays.toString(max));
					if(max[0]<reserve){
						System.out.println("NO ONE WON THE BID!!! THEY ALL LOST :((");
					}else {
						if(max[1]==0) {
							System.out.println("A1 HAS WON THE BID :D");
						}
						if(max[1]==1) {
							System.out.println("A2 HAS WON THE BID :D");
						}
						if(max[1]==2) {
							System.out.println("A3 HAS WON THE BID :D");
						}
					}
					
					//reset time and bids and starting price
					iteration++;
					System.out.println("\n\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
					if(iteration<products.length) System.out.println("\n\nAUCTION for "+ products[iteration]+" has STARTED!!!!!!");
						startTime = System.nanoTime();
						max[0] = 0;
						ended = false;
				        for(int j=0; j<numberAgents;j++) {
				        	prices[j]=0;
				        }
				
				}
				
				
				return (iteration==products.length);
			}
			
		}
    
		
		
		public class B2 extends CyclicBehaviour {
			
			ACLMessage m2;
			public void action (){
				int iter = 0;
				
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
					
					//receive msg de Vendeur
					m2 = this.myAgent.blockingReceive();
					
					
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					int[] price_min = null;
					try {
						price_min = (int[])m2.getContentObject();
						iter = price_min[2];
					} catch (UnreadableException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					System.out.println (this.getAgent().getLocalName()+" Min price is: " + price_min[0]);
					
					Random random = new Random();
					
					//send price to Intermediaire
					int index = 0;
					if(this.getAgent().getLocalName().equals("A1")) {
						index = 0;
					}
					if(this.getAgent().getLocalName().equals("A2")) {
						index = 1;
					}
					if(this.getAgent().getLocalName().equals("A3")) {
						index = 2;
					}
					
					if(price_min[0] >= budgets[index][iter] ) {
						System.out.println (this.getAgent().getLocalName()+" STOPPED");
						ACLMessage m3 = new ACLMessage(ACLMessage.INFORM);
						try {
							int [] T = {-1,index};
							m3.setContentObject(T);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						m3.addReceiver(new AID("Intermediaire",AID.ISLOCALNAME));
						send(m3);
					}
					else {
						int price = price_min[0] + (random.nextInt(budgets[index][iter]-price_min[0]+1))/4;//to not bid too much i divided the added bid by 4
						prices[index] = price; //current bid
						ACLMessage m3 = new ACLMessage(ACLMessage.INFORM);
						try {
							int [] T = {price,index};
							m3.setContentObject(T);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println (this.getAgent().getLocalName()+" Advanced : " + price+ " Budget is: "+ budgets[index][iter]+ "\n");
						m3.addReceiver(new AID("Intermediaire",AID.ISLOCALNAME));
						send(m3);
					}
					
				
			}
		}
		
		
		public class B3 extends CyclicBehaviour {
			ACLMessage m3;
			public void action (){
				
				
				//receive msg
				//receive the 3 max
				//1st-----------------------------------------------------
				m3 = this.myAgent.blockingReceive();

				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				int[] max1 = null;
				try {
					max1 = (int[])m3.getContentObject();
				} catch (UnreadableException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(max1[0] == -1 && max1[1] == 0) {
					ag_arrets[0] = true;
					max1[0] = prices[0];
				}
				if(max1[0] == -1 && max1[1] == 1) {
					ag_arrets[1] = true;
					max1[0] = prices[1];
				}
				if(max1[0] == -1 && max1[1] == 2) {
					ag_arrets[2] = true;
					max1[0] = prices[2];
				}
				
				//2nd-----------------------------------------------------
				m3 = this.myAgent.blockingReceive();
				
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				int max2[] = null;
				try {
					max2 = (int[])m3.getContentObject();
				} catch (UnreadableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(max2[0] == -1 && max2[1] == 0) {
					ag_arrets[0] = true;
					max2[0] = prices[0];
				}
				if(max2[0] == -1 && max2[1] == 1) {
					ag_arrets[1] = true;
					max2[0] = prices[1];
				}
				if(max2[0] == -1 && max2[1] == 2) {
					ag_arrets[2] = true;
					max2[0] = prices[2];
				}
				//3rd-----------------------------------------------------
				m3 = this.myAgent.blockingReceive();

				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				int[] max3 = null;
				try {
					max3 = (int[])m3.getContentObject();
				} catch (UnreadableException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(max3[0] == -1 && max3[1] == 0) {
					ag_arrets[0] = true;
					max3[0] = prices[0];
				}
				if(max3[0] == -1 && max3[1] == 1) {
					ag_arrets[1] = true;
					max3[0] = prices[1];
				}
				if(max3[0] == -1 && max3[1] == 2) {
					ag_arrets[2] = true;
					max3[0] = prices[2];
				}
				
				//get the max
				int index = 0;
				//int max_received = Math.max(max1[0], max2[0]);
				//max
				int[] maxT = {max1[0],max2[0],max3[0]};
				int max_received = maxT[0];
				for (int i = 1; i<maxT.length; i++) {
					if(maxT[i]>max_received) {
						max_received=maxT[i];
					}
				}
				
				if(max_received == max1[0]) index = max1[1];
				if(max_received == max2[0]) index = max2[1];
				if(max_received == max3[0]) index = max3[1];
				int [] T = {max_received,index,0};
				
				
				System.out.println(this.getAgent().getLocalName()+" B3 max is : "+max_received+ " ,Index: "+index+ "\n");
				
				
				//send max to Vendeur
				ACLMessage m1 = new ACLMessage(ACLMessage.INFORM);
				try {
					m1.setContentObject(T);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				m1.addReceiver(new AID("Vendeur",AID.ISLOCALNAME));
				send(m1);
				
			}
			
		}
	
	
	
	
}
