package com.hclspace.nelsonpoc.restclient;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.KieServiceResponse.ResponseType;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.RuleServicesClient;

import com.hclspace.nelsonpoc.Message;


public class KieServerRestClient {

  private static final String URL = "http://localhost:8180/kie-server/services/rest/server";
  private static final String USER = "kieserver";
  private static final String PASSWORD = "kieserver1!";

  private static final MarshallingFormat FORMAT = MarshallingFormat.JSON;

  private static KieServicesConfiguration conf;
  private static KieServicesClient kieServicesClient;
  // Rules client
  private static RuleServicesClient ruleClient;
  

  public static void initialize() {
    conf = KieServicesFactory.newRestConfiguration(URL, USER, PASSWORD);

    //If you use custom classes, such as Obj.class, add them to the configuration.
    Set<Class<?>> extraClassList = new HashSet<Class<?>>();
    extraClassList.add(Message.class);
    conf.addExtraClasses(extraClassList);

    conf.setMarshallingFormat(FORMAT);
    kieServicesClient = KieServicesFactory.newKieServicesClient(conf);
	ruleClient = kieServicesClient.getServicesClient(RuleServicesClient.class);

  }
  
	public static RuleServicesClient getRuleServiceClient(){
		if(ruleClient==null) throw new RuntimeException("Service Client NOT initialized");
		return ruleClient;
	}
	  
	public static void executeCommands() {

	  String containerId = "nelsonpoc_1.0.0";
	  System.out.println("== Sending commands to the server ==");
	  RuleServicesClient rulesClient = getRuleServiceClient();
	  KieCommands commandsFactory = KieServices.Factory.get().getCommands();

	  Message factToInsert = new Message();
	  factToInsert.setMessage("This is Absolutely Fantastic...");
	  factToInsert.setStatus(Message.HELLO);
//	  Command<?> insert = commandsFactory.newInsert("Some String OBJ");
	  Command<?> insert = commandsFactory.newInsert(factToInsert);
	  Command<?> fireAllRules = commandsFactory.newFireAllRules();
	  Command<?> batchCommand = commandsFactory.newBatchExecution(Arrays.asList(insert, fireAllRules));

	  ServiceResponse<String> executeResponse = rulesClient.executeCommands(containerId, batchCommand);

	  if(executeResponse.getType() == ResponseType.SUCCESS) {
		System.out.println("Commands executed with success! Response: ");
		System.out.println(executeResponse.getResult());
	  } else {
		System.out.println("Error executing rules. Message: ");
		System.out.println(executeResponse.getMsg());
	  }
	}  
}