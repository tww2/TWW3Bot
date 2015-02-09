import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Main
{
	private static final String DEFAULT_CONFIG_LOCATION = ".\\config.ini";
	
	public static void main(String[] args) {
		
		String configLocation = DEFAULT_CONFIG_LOCATION;
		if(args.length > 0)
		{
			configLocation = args[0];
		}
		
		Properties config = new Properties();
		
		try
		{
			File configFile = new File(configLocation);
			config.load(new FileReader(configFile));
		}catch(IOException e)
		{
			System.err.println("Failed to load configuration file.");
			System.err.println(e);
			return;
		}
		
		TWW3Bot bot = new TWW3Bot(config);
		bot.start();
	}
}
