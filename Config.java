import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Config
{
	//TODO write getter methods and make members private
	private File configFile;
	private HashMap<String, String> fileLocations;
	public String defaultChannel;
	public String ircHostName;
	public int ircPort;
	public String userName;
	public String password;
	
	public Config(String configFileLocation)
	{
		this.configFile = new File(configFileLocation);
		this.fileLocations = new HashMap<String, String>();
		parseINI();
	}

	public void parseINI()
	{
		//TODO integrate some library code or do this more elegantly to support modified ini files
		try
		{
			Scanner iniReader = new Scanner(configFile);

			iniReader.nextLine(); //[files]
			String[] kv = iniReader.nextLine().split("="); //approvedquotes
			fileLocations.put(kv[0], kv[1]);
			kv = iniReader.nextLine().split("="); //blacklist
			fileLocations.put(kv[0], kv[1]);
			kv = iniReader.nextLine().split("="); //quotefile
			fileLocations.put(kv[0], kv[1]);
			kv = iniReader.nextLine().split("="); //simplecommands
			fileLocations.put(kv[0], kv[1]);

			iniReader.nextLine(); //newline
			iniReader.nextLine(); //[irc]

			kv = iniReader.nextLine().split("="); //defaultchannel
			defaultChannel = kv[1];
			kv = iniReader.nextLine().split("="); //hostname
			ircHostName = kv[1];
			kv = iniReader.nextLine().split("="); //port
			ircPort = Integer.parseInt(kv[1]);

			iniReader.nextLine(); //newline
			iniReader.nextLine(); //[user]

			kv = iniReader.nextLine().split("="); //username
			userName = kv[1];
			kv = iniReader.nextLine().split("="); //password
			password = kv[1];

		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public String getFileLocation(String key)
	{
		return fileLocations.get(key);
	}
}
