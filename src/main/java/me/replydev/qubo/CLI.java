package me.replydev.qubo;

import me.replydev.utils.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Path;
import java.util.List;

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
			GoodBye.quit(-1);
		}

		/* controlla se ci sono almeno -range o -rangefile */
		boolean range = false;
		boolean rangefile = false;
		String fname = "new_scan";
		int c = -1;
		for (String b : a){
			c++;
			if (b.contains("-rangefile")){
				rangefile = true;
				fname = a[c+1];
			}else if (b.contains("-range")){
				range = true;
				fname = a[c+1];
			}
		}
		if (rangefile==false && range==false){
			System.out.println("\033[31mYou must provide either the argument \033[0m-range\033[31m or \033[0m-rangefile");
			GoodBye.quit(-4);
		}
		fname+=".txt";

		/* controlla se è presente -dir o --directory */
		boolean directory = false;
		String dirpath = "qubo_outputs";
		c = -1;
		for (String b : a){
			c++;
			if (b.contains("-dir")||b.contains("--directory")){
				directory = true;
				dirpath = a[c+1];
			}
		}
		String dirfound = "found";
		if (!directory){
			dirfound = "not found";
		}
		System.out.println("\033[0m-dir\033[31m option "+dirfound+", using \033[0m"+dirpath+"\033[31m as output file");


		/* controlla se è presente -fname o --filename */
		boolean foundfname = false;
		c = -1;
		for (String b : a){
			c++;
			if (b.contains("-fname")||b.contains("--filename")){
				foundfname = true;
				fname = a[c+1];
			}
		}
        String found_fname = "found";
		if (!foundfname){
			found_fname = "not found";
		}
		System.out.println("\033[0m-fname\033[31m option "+found_fname+", using \033[0m"+fname+"\033[31m as output file");


		/* operazioni con la classe Filez */
		Filez.init(dirpath);
		Path p = Filez.create_file(fname);
		Filez.setQuboOutputs(p);
		Filez.writeToFile(p, "[custom Quboscanner made by www.n0nexist.gq]");

		/* controlla se dobbiamo usare -range o -rangefile */
		c = -1;
		boolean found = false;
		for (String b : a){
			c++;
			if (b.contains("-rangefile")){
				try{
					System.out.println("\033[31mStarting with \033[0m"+a[c+1]+" \033[31mas range file");
					txtRun(a[c+1],a);
					found = true;	
				}catch(ArrayIndexOutOfBoundsException e){
					System.out.println("\033[31musage\033[0m => \033[33m-rangefile \033[0m(\033[33mfile.txt\033[0m)");
					GoodBye.quit(3);
				}
			}
		}
		if (!found){
			System.out.println("\033[0m-rangefile\033[31m option not found, starting a \033[0mnormal\033[31m scan");
			standardRun(a);
		}

		/* comunica all'utente quanti server abbiamo trovato e termina l'esecuzione */
		Log.logln("\nScan terminated \033[33m<=>\033[0m found \033[31m"+Info.serverNotFilteredFound+"\033[0m servers.");
		Filez.writeToFile(Filez.getCurrentPath(), "[found "+Info.serverNotFilteredFound+" servers in total]");
		System.out.println("\033[0;0m");
		GoodBye.quit(0);
	}

	private static void printLogo()
	{
		/* logo del programma */
		System.out.println("\033[31m┌─┐ ┬ ┬┌┐ ┌─┐┌─┐┌─┐┌─┐┌┐┌┌┐┌┌─┐┬─┐\n"
				+ "\033[31m│─┼┐│ │├┴┐│ │└─┐│  ├─┤││││││├┤ ├┬┘\n"
				+"\033[33m└─┘└└─┘└─┘└─┘└─┘└─┘┴ ┴┘└┘┘└┘└─┘┴└─\n"
				+"\033[91mversione \033[0m=>\033[32m "+Info.version);
		System.out.println("\033[31mwww.n0nexist.gq \033[0m<=> \033[31mgithub.com/n0nexist");
	}

	private static void standardRun(String[] a)
	{
		/* run standard con -range */
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

	public static String[] replaceInArray(String[] arr, String to, String with) {
		String[] newArr = new String[arr.length];
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].equals(to)) {
				newArr[i] = with;
			} else {
				newArr[i] = arr[i];
			}
		}
		return newArr;
	}

	private static void txtRun(String filename,String[] a) 
	{
		/* run con -filerange */
		try 
		{
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String s;
			while ((s = reader.readLine()) != null) 
			{
				if (s.isEmpty())
				{					
					continue;
				}
				
				try {

					/* rimpiazziamo l'opzione -rangefile negli argomenti con "-range [ilrange]" */
					String[] temp = replaceInArray(a, "-rangefile", "-range");
					temp = replaceInArray(temp, filename, s);


					System.out.println("\033[33mStarting new scan \033[0m=> \033[33m"+s);


					/* lanciamo una nuova istanza di qubo con il range corrente */
					QuboInstance qq = new QuboInstance(new InputData(temp));
					quboInstance = qq;
					qq.run();

				} catch (Exception e) {
					System.out.println("\033mERROR");
					e.printStackTrace();
					GoodBye.quit(-5);
				}
			}
			reader.close();
		} 
		catch (IOException e) 
		{
			System.err.println("\033[33mFile \033[0m\""+filename+"\"\033[0m not found");
			GoodBye.quit(-1);
		}
	}

	private static boolean isUTF8Mode()
	{
		List<String> arguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
		return arguments.contains("-Dfile.encoding=UTF-8");
	}

}