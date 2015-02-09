import java.io.IOException;
import java.util.Properties;

abstract class IRCBot extends Thread {

	protected Properties config;
	protected String botName;
	private long globalMessageTimeout;
	private long globalLastMessageTime;
	private IRCConnection connection;
	
	public IRCBot(Properties config)
	{
		this.config = config;
		this.globalMessageTimeout = Long.parseLong(config.getProperty(ConfigKeys.GLOBAL_MESSAGE_TIMEOUT));
		this.reconnect();
	}
	
	protected void reconnect()
	{
		String hostName = config.getProperty(ConfigKeys.IRC_SERVER_HOSTNAME);
		int port = Integer.parseInt(config.getProperty(ConfigKeys.IRC_SERVER_PORT));
		this.botName = config.getProperty(ConfigKeys.IRC_USERNAME);
		String password = config.getProperty(ConfigKeys.IRC_PASSWORD);
		this.connection = new IRCConnection(hostName, port, botName, password);
		this.joinChannel(config.getProperty(ConfigKeys.IRC_DEFAULT_CHANNEL));
	}
	
	public void run()
	{
		do
		{
			if(this.connection.isClosed())
			{
				this.reconnect();
			}
			
			try
			{
				processMessage();
			} catch (IOException e)
			{
				System.err.println("Message processing error.");
				System.err.println(e);
			}
		} while(true);
	}
	
	protected void processMessage() throws IOException
	{
		String message = connection.nextMessage();
		String[] readArray = message.split(":", 3);
		if (readArray.length > 1) {
			String[] cmdLine = readArray[1].split(" ");
			if(cmdLine.length > 1)
			{
				String sender = cmdLine[0];
				String command = cmdLine[1];
				
				/********************************************************
				 *														*
				 *						COMMANDS						*
				 *														*
				 ********************************************************/
				if(command.equals("PRIVMSG"))
				{
					if(sender.indexOf('!') >= 0)
					{
						sender = sender.substring(0, sender.indexOf('!'));
					}
					String target = cmdLine[2];
					String mes = "";
					for(int i=2; i<readArray.length; ++i)
					{
						mes += readArray[i];
					}
					
					System.out.println(sender + "@" + target + ": " + mes);
					onMessage(sender, target, mes);
				} else {
					System.out.println(command + " ~~~ " + message);
				}
				
				/******************END OF COMMANDS *********************/
				
			} else {
				System.out.println("NOCOMMAND! ~~~ " + message);
			}
		} else {
			System.out.println("NOCOLON! ~~~ " + message);
		}
	}
	
	abstract protected void onMessage(String sender, String target, String message);
	
	protected void sendMessage(String message)
	{
		//TODO: fix try/catch here - poor design currently
		try
		{
			long offset = System.currentTimeMillis() - globalLastMessageTime;
			if(offset < globalMessageTimeout)
			{
				Thread.sleep(globalMessageTimeout - offset);
			}
			connection.sendMessage(message);
			
			globalLastMessageTime = System.currentTimeMillis();
			
		} catch (Exception e)
		{
			//InterruptedException from sleep or IOException from sendMessage
			//Neither are fatal, print error and continue
			System.err.println(e);
		}
	}
	
	protected void joinChannel(String chan)
	{
		this.sendMessage("JOIN " + chan);
	}
}