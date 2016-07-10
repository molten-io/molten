import org.mule.api.MuleContext;
import org.mule.api.MuleMessage;
import org.mule.api.context.MuleContextBuilder;
import org.mule.api.context.MuleContextFactory;
import org.mule.config.DefaultMuleConfiguration;
import org.mule.config.spring.SpringXmlConfigurationBuilder;
import org.mule.context.DefaultMuleContextBuilder;
import org.mule.context.DefaultMuleContextFactory;
import org.mule.module.client.MuleClient;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class LambdaFunctionHandler implements RequestHandler<String, String> {

	private static final String MULE_FLOW_NAME = "hellolambda.xml";
	private static final String INPUT_ENDPOINT = "vm://in";
	private MuleContext serverContext = null;

	public String handleRequest(String input, Context context) {

		if (serverContext == null)
			try {
				serverContext = initMuleServer(MULE_FLOW_NAME);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		try {
			MuleClient client = new MuleClient(serverContext);
			MuleMessage result = client.send(INPUT_ENDPOINT, input, null);
			return result.getPayloadAsString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}

	private MuleContext initMuleServer(String muleFlowName) throws Exception {

		SpringXmlConfigurationBuilder configBuilder = new SpringXmlConfigurationBuilder(muleFlowName);
		DefaultMuleConfiguration muleConfig = new DefaultMuleConfiguration();
		muleConfig.setWorkingDirectory("/tmp");
		MuleContextBuilder contextBuilder = new DefaultMuleContextBuilder();
		contextBuilder.setMuleConfiguration(muleConfig);
		MuleContextFactory contextFactory = new DefaultMuleContextFactory();
		MuleContext muleContext = contextFactory.createMuleContext(configBuilder, contextBuilder);
		muleContext.start();

		return muleContext;
	}

}
