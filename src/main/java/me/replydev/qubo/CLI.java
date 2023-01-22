package me.replydev.qubo;

import me.replydev.utils.KeyboardThread;
import me.replydev.utils.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CLI {

	private static QuboInstance quboInstance;

	public static QuboInstance getQuboInstance() 
	{
		return quboInstance;
	}

	static void init(String[] a) 
	{
		printLogo();
		if(!isUTF8Mode()){
			System.out.println("\033[31mThe scanner isn't running in UTF-8 mode!");
			System.out.println("\033[31musage: \"-Dfile.encoding=UTF-8\" -jar [file]");
			System.out.println("\033[31mexample command =>  \033[33m\"\033[0mjava -Dfile.encoding=UTF-8 -jar qubo.jar -ports 1-110.200-205.300-305.400-405.500-505.600-605.666.700-705.777.800-805.900-905.1000-1005.2000-2020.3000-3005.4000-4005.5000-5005.6666.7000-7005.7777.8000-8010.10000-10020.12345.12346.13338.20000-20020.21000-21020.22000-22020.23000-23005.24000-24005.25000-25020.25500-25630.26000-26020.26666.27000-27020.27777.28000-28005.28888.29000-29005.29999.10000-10020.20000-20020.30000-30020.40000-40020.50000-50020.60000-60020 -th 900 -ti 1500 -c 1 -noping -range \033[0m[IP]\033[33m\"");
			System.exit(-1);
		}
		ExecutorService inputService = Executors.newSingleThreadExecutor();
		inputService.execute(new KeyboardThread());
		if (Arrays.equals(new String[] { "-txt" }, a))
			txtRun();
		else
			standardRun(a);
		Log.logln("\nScan terminated \033[33m<=>\033[0m found \033[31m"+Info.serverNotFilteredFound+"\033[0m servers.");
		System.exit(0);
	}

	private static void printLogo()
	{
		System.out.println("\033[31m┌─┐ ┬ ┬┌┐ ┌─┐┌─┐┌─┐┌─┐┌┐┌┌┐┌┌─┐┬─┐\n"
				+ "\033[31m│─┼┐│ │├┴┐│ │└─┐│  ├─┤││││││├┤ ├┬┘\n"
				+"\033[33m└─┘└└─┘└─┘└─┘└─┘└─┘┴ ┴┘└┘┘└┘└─┘┴└─\n");
		System.out.println("\033[31mwww.n0nexist.gq \033[0m<=> \033[31mgithub.com/n0nexist");
	}

	private static void standardRun(String[] a)
	{
		InputData i;
		try 
		{
			i = new InputData(a);
		} 
		catch (Exception e) 
		{
			System.err.println(e.getMessage());
			return;
		}
		Info.debugMode = i.isDebugMode();
		quboInstance = new QuboInstance(i);
		try{
			quboInstance.run();
		}catch (NumberFormatException e){
			quboInstance.inputData.help();
		}
	}

	private static void txtRun() 
	{
		try 
		{
			BufferedReader reader = new BufferedReader(new FileReader("ranges.txt"));
			String s;
			while ((s = reader.readLine()) != null) 
			{
				if (s.isEmpty())
				{					
					continue;
				}
				
				InputData i;
				try 
				{
					i = new InputData(s.split(" "));
				}
				catch (Exception e) 
				{
					System.err.println(e.getCause().getMessage());
					reader.close();
					return;
				}
				
				quboInstance = new QuboInstance(i);
				quboInstance.run();
			}
			reader.close();
		} 
		catch (IOException e) 
		{
			System.err.println("File \"ranges.txt\" not found, create a new one and restart the scanner");
			System.exit(-1);
		}
	}

	private static boolean isUTF8Mode()
	{
		List<String> arguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
		return arguments.contains("-Dfile.encoding=UTF-8");
	}

}