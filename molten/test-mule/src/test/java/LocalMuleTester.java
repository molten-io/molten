import org.mule.api.MuleContext;
import org.mule.api.MuleMessage;
import org.mule.api.context.MuleContextBuilder;
import org.mule.api.context.MuleContextFactory;
import org.mule.config.DefaultMuleConfiguration;
import org.mule.config.spring.SpringXmlConfigurationBuilder;
import org.mule.context.DefaultMuleContextBuilder;
import org.mule.context.DefaultMuleContextFactory;
import org.mule.module.client.MuleClient;

public class LocalMuleTester {

	private static final String INPUT_ENDPOINT = "vm://in";
	private static final String MULE_FLOW_NAME = "hellolambda.xml";
	
	public static void main(String[] args) throws Exception {

		LocalMuleTester tester = new LocalMuleTester();
		MuleContext serverContext = tester.initMuleServer(MULE_FLOW_NAME);
		MuleClient client = new MuleClient(serverContext);
		MuleMessage result = client.send(INPUT_ENDPOINT, "TEST", null);
		
		System.out.print(result.getPayloadAsString());
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
