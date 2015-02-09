import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class IRCConnection implements Closeable
{
	private Socket ircSocket;
	private String hostName;
	private int port;
	private String userName;
	private String password;
	private Scanner socketInput;
	private PrintStream socketOutput;
	private boolean closed;
	
	public IRCConnection(String hostName, int port, String userName, String password)
	{
		this.hostName = hostName;
		this.port = port;
		this.userName = userName;
		this.password = password;
		this.connect();
	}
	
	private void connect()
	{
		try
		{
			System.err.println(this);
			ircSocket = new Socket(hostName, port);
			socketInput = new Scanner(ircSocket.getInputStream());
			socketOutput = new PrintStream(ircSocket.getOutputStream());
		
			socketOutput.println("PASS " + password);
			socketOutput.println("NICK " + userName);
		} catch (IOException e)
		{
			e.printStackTrace();
			this.close();
		}
	}
	
	public void close()
	{
		if(this.isClosed())
		{
			//no-op already closed
			return;
		}
		
		try
		{
			if(ircSocket != null)
			{
				ircSocket.close();
				this.closed = true;
			}
		} catch (IOException e)
		{
			System.err.printf("Error closing %1s\n", this.toString());
			e.printStackTrace();
		}
	}
	
	public boolean isClosed()
	{
		return this.closed;
	}
	
	public String nextMessage() throws IOException
	{
		if(this.isClosed() || ircSocket.isInputShutdown())
		{
			this.close();
			throw new IOException(String.format("Tried to read from %1s, but it is closed.", this.toString()));
		}
		
		//Returns only non-ping messages from the server
		try
		{
			while(true)
			{
				String message = socketInput.nextLine();
				//System.err.println(message);
				String[] readArray = message.split(":", 2);
				if(readArray[0].equals("PING "))
				{
					this.sendMessage("PONG :" + readArray[1]);
				} else 
				{
					return message;
				}
			}
		}catch (NoSuchElementException e)
		{
			this.close();
			throw new IOException(String.format("Tried to read from %1s, but it is closed.", this.toString()), e);
		}
	}
	
	public void sendMessage(String message) throws IOException
	{
		if(this.isClosed() || ircSocket.isOutputShutdown())
		{
			this.close();
			throw new IOException(String.format("Tried to write to %1s, but it is closed.", this.toString()));
		}

		socketOutput.println(message);
		System.out.println(message);
	}
	
	@Override
	public String toString()
	{
		return String.format("IRCConnection to %1s on port %2s with account %3s.", hostName, port, userName);
	}
}
