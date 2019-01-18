package com.hclspace.nelsonpoc.restclient;

public class TestRestClient {
	
	public static void main(String args[]) {
		KieServerRestClient client = new KieServerRestClient();
		client.initialize();
		client.executeCommands();
	}

}
