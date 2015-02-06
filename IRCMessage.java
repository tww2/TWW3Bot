public class IRCMessage
{
	public String sender;
	public String target;
	public String message;

	public IRCMessage(String sender, String target, String message)
	{
		this.sender = sender;
		this.target = target;
		this.message = message;
	}
}
