/*
 * (c) 2003-2018 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package com.mulesoft.azure.vault.provider.api;

import static com.mulesoft.azure.vault.provider.api.AzureVaultConfigurationPropertiesExtensionLoadingDelegate.*;
import static org.mule.runtime.api.component.ComponentIdentifier.builder;

import com.microsoft.aad.adal4j.AuthenticationException;
import com.microsoft.azure.keyvault.KeyVaultClient;
import com.microsoft.azure.keyvault.authentication.KeyVaultCredentials;
import org.mule.runtime.api.component.ComponentIdentifier;
import org.mule.runtime.config.api.dsl.model.ConfigurationParameters;
import org.mule.runtime.config.api.dsl.model.ResourceProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProviderFactory;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Builds the provider for a custom-properties-provider:config element.
 *
 * @since 1.0
 */
public class AzureVaultConfigurationPropertiesProviderFactory implements ConfigurationPropertiesProviderFactory {

  public static final String EXTENSION_NAMESPACE =
          EXTENSION_NAME.toLowerCase().replace(" ", "-");
  public static final String AZURE_VALUT_CLIENT__PARAMETER_GROUP_NAME =
          AZUREVALUT_CLIENT_PARAMETER_GROUP.toLowerCase().replace(" ", "-");
  private static final ComponentIdentifier CUSTOM_PROPERTIES_PROVIDER =
          builder().namespace(EXTENSION_NAMESPACE).name(CONFIG_ELEMENT).build();

  private final static Logger LOGGER = LoggerFactory.getLogger(AzureVaultConfigurationPropertiesProviderFactory.class);

  // TODO change to meaningful prefix

  @Override
  public ComponentIdentifier getSupportedComponentIdentifier() {
    return CUSTOM_PROPERTIES_PROVIDER;
  }

  @Override
  public ConfigurationPropertiesProvider createProvider(ConfigurationParameters parameters,
                                                        ResourceProvider externalResourceProvider) {

    String vaultName;

    List<ConfigurationParameters> azureVaultList = parameters
            .getComplexConfigurationParameter(builder()
                    .namespace(EXTENSION_NAMESPACE)
                    .name(AZURE_VALUT_CLIENT__PARAMETER_GROUP_NAME).build());
    ConfigurationParameters parameters1 = azureVaultList.get(0);
    try {
      vaultName = parameters1.getStringParameter("azureVaultName");
    } catch (Exception e) {
      LOGGER.error("vaultName parameter not present");
      throw new RuntimeException("vaultName parameter not present");
    }
    try {
      return new AzureVaultConfigurationPropertiesProvider(getAzureKeyVaultClient(parameters), vaultName);
    } catch (Exception ve) {
      LOGGER.error("Error connecting to Azure Vault Key Service", ve);
      return null;
    }



  }

  private KeyVaultClient getAzureKeyVaultClient(ConfigurationParameters parameters) {
    String applicationClientId = null;
    String applicationSecretKey = null;

    KeyVaultClient keyVaultClient = null;

    List<ConfigurationParameters> azureVaultList = parameters
            .getComplexConfigurationParameter(builder()
                    .namespace(EXTENSION_NAMESPACE)
                    .name(AZURE_VALUT_CLIENT__PARAMETER_GROUP_NAME).build());


    ConfigurationParameters parameters1 = azureVaultList.get(0);

    try {
      applicationClientId = parameters1.getStringParameter("applicationClientId");
    } catch (Exception e) {
      LOGGER.error("azure application client id is not present");
      throw new RuntimeException("azure application client id parameter not present");
    }
    try {
      applicationSecretKey = parameters1.getStringParameter("applicationSecretKey");
    } catch (Exception e) {
      LOGGER.error("application secret key parameter not present");
      throw new RuntimeException("application secret key parameter not present");
    }
    try {
      KeyVaultCredentials kvCred = new AzureVaultConfigurationClientSecretKeyCredential(applicationClientId, applicationSecretKey);
      keyVaultClient = new KeyVaultClient(kvCred);
      return keyVaultClient;
    }catch (AuthenticationException ae){
      LOGGER.error(" Authentication Error Occured while getting the access token" + ae.getMessage());
      throw new AuthenticationException(" Authentication Error Occured while getting the access token" + ae.getMessage());
    }

    catch(Exception e){
      LOGGER.error(" Error Occured while getting the access token" + e.getMessage());
      throw new RuntimeException(" Error Occured while getting the access token" + e.getMessage());
    }



  }

}
