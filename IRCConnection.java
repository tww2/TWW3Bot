import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class IRCConnection
{
	private Socket ircSocket;
	private String hostName;
	private int port;
	private String userName;
	private String password;
	private Scanner socketInput;
	private PrintStream socketOutput;
	private long floodControl;
	
	public IRCConnection(String hostName, int port, String userName, String password)
	{
		this.hostName = hostName;
		this.port = port;
		this.userName = userName;
		this.password = password;
		this.reconnect();
		this.floodControl = 0;
	}
	
	public void reconnect()
	{
		try
		{
			if(ircSocket != null)
			{
				closeConnection();
			}
		
			ircSocket = new Socket(hostName, port);
			socketInput = new Scanner(ircSocket.getInputStream());
			socketOutput = new PrintStream(ircSocket.getOutputStream());
		
			socketOutput.println("PASS " + password);
			socketOutput.println("NICK " + userName);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void closeConnection()
	{
		try
		{
			if(ircSocket != null)
			{
				socketInput.close();
				socketOutput.close();
				ircSocket.close();
				socketInput = null;
				socketOutput = null;
				ircSocket = null;
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public IRCMessage readMessage()
	{
		IRCMessage ret = null;
		
		//TODO revisit this loop design... seems like there should be a smarter way to do this
		do
		{
			String message = socketInput.nextLine();
			String[] readArray = message.split(":", 3);
			if(readArray[0].equals("PING "))
			{
				socketOutput.println("PONG :" + readArray[1]);
			} else if (readArray.length > 1) {
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
						ret = new IRCMessage(sender, target, mes);
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
		}while (ret == null);
		
		return ret;
	}
	
	public void sendMessage(String target, String message)
	{
		try
		{
			long offset = System.currentTimeMillis() - floodControl;
			if(offset < 5000L)
			{
				Thread.sleep(5000L - offset);
			}
			
			socketOutput.println("PRIVMSG " + target + " :" + message);
			System.out.println("PRIVMSG " + target + " :" + message);
			
			floodControl = System.currentTimeMillis();
			
		} catch (InterruptedException e)
		{
			System.err.println(e);
		}
	}
	
	public void joinChannel(String channel)
	{
		socketOutput.println("JOIN " + channel);
	}
}
