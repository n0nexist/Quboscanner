package me.replydev.qubo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import me.replydev.mcping.net.Check;
import me.replydev.utils.Log;

public class QuboInstance 
{

	public final InputData inputData;
	private String ip; // current ip
	private int port; // current port
	private boolean stop;
	public final AtomicInteger currentThreads;
	private final int[] COMMON_PORTS = { 25, 80, 443, 20, 21, 22, 23, 143, 3306, 3389, 53, 67, 68, 110 };
	private long serverCount = 0;
	ExecutorService checkService;

	public QuboInstance(InputData inputData) 
	{
		this.inputData = inputData;
		this.currentThreads = new AtomicInteger();
		stop = false;

		if(this.inputData.isDebugMode()){
			Log.logln("Debug mode enabled");
		}
	}

	public void run() 
	{
		try
		{
			checkServersExecutor();
		} 
		catch (InterruptedException e) 
		{
			checkService.shutdownNow();
			GoodBye.quit(-7);
		}
		

	}

	private void checkServersExecutor() throws InterruptedException,NumberFormatException {
		checkService = Executors.newFixedThreadPool(inputData.getThreads());
		Log.logln("Finding serverz...");


		while (inputData.getIpList().hasNext()) 
		{
			ip = inputData.getIpList().getNext();
			try 
			{
				InetAddress address = InetAddress.getByName(ip);
				if (inputData.isSkipCommonPorts() && isLikelyBroadcast(address))
					continue;
			} catch (UnknownHostException ignored) {}

			while (inputData.getPortrange().hasNext())
			{
				if (stop) 
				{
					checkService.shutdown();
					checkService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
					return;
				}
				
				port = inputData.getPortrange().get();
				if (isCommonPort(port)) 
				{
					inputData.getPortrange().next();
					continue;
				}
				
				if (currentThreads.get() < inputData.getThreads()) 
				{
					currentThreads.incrementAndGet();
					checkService.execute(
							new Check(ip, port, inputData.getTimeout(), inputData.getCount(),
									this, inputData.getVersion(), inputData.getMotd(), inputData.getMinPlayer()));
					inputData.getPortrange().next(); // va al successivo
					serverCount++;
				}
			}
			inputData.getPortrange().reload();
		}
		checkService.shutdown();
		checkService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
	}

	public String getCurrent() 
	{
		return "\033[0m"+ip + ":" + port + " - \033[33m" + String.format("%.2f", getPercentage()) + "%";
	}

	public double getPercentage() 
	{
		long totalservers = inputData.getIpList().getCount();
		long totalports = inputData.getPortrange().size();
		double max = totalservers * totalports;
		return serverCount * 100 / max;
	}

	public long getServerCount() {
		return serverCount;
	}

	public int getThreads() 
	{
		return currentThreads.get();
	}

	public void stop() 
	{
		this.stop = true;
	}

	private boolean isCommonPort(int port) 
	{
		if (!inputData.isSkipCommonPorts()) 
		{			
			return false;
		}
		for (int i : COMMON_PORTS) 
		{
			if (i == port) {				
				return true;
			}
		}
		return false;
	}

	private boolean isLikelyBroadcast(InetAddress address) 
	{
		byte[] bytes = address.getAddress();
		return bytes[bytes.length - 1] == 0 || bytes[bytes.length - 1] == (byte) 0xFF;
	}

}
