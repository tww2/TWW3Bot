public class Main
{
	private static final String DEFAULT_CONFIG_LOCATION = ".\\config.ini";
	public static void main(String[] args) {
		
		String configLocation = DEFAULT_CONFIG_LOCATION;
		if(args.length > 0)
		{
			configLocation = args[0];
		}

		Config settings = new Config(configLocation);

		IRCConnection abc = new IRCConnection(settings.ircHostName, settings.ircPort, settings.userName, settings.password);

		abc.joinChannel(settings.defaultChannel);
		while(true)
		{
			IRCMessage mes = abc.readMessage();
		}
	}
}
