package dev.jorel.commandapi;

import java.util.List;

public interface Config {

	public boolean hasVerboseOutput();
	
	public boolean hasSilentLogs();
	
	public boolean shouldUseLatestNMSVersion();
	
	public String getMissingImplementationMessage();

	public boolean willCreateDispatcherFile();
	
	public boolean shouldSkipSenderProxy(String commandName);
	
	public List<String> getCommandsToConvert();
}
