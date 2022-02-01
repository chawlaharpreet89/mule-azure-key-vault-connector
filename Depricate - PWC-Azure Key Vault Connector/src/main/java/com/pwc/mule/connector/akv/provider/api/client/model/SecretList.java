package com.pwc.mule.connector.akv.provider.api.client.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class SecretList {
	
	@SerializedName(value = "value")
	  private List secretList;
	
	@SerializedName(value = "nextLink")
	  private String nextLink;
	

	public String getNextLink() {
		return nextLink;
	}

	public void setNextLink(String nextLink) {
		this.nextLink = nextLink;
	}

	public List getSecretList() {
		return secretList;
	}

	public void setSecretList(List secretList) {
		this.secretList = secretList;
	}

	
}
