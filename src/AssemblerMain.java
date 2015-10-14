import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class AssemblerMain {
	
	public static String assemble(String code){
		
		int lineNumber = 0xf000;
		String machineCode = "";
		
		String[] lines = code.split("\n");
		
		for(String line: lines){
			machineCode += String.format("%H",lineNumber) + " " + line.replace("li", "7") + "\n";
			lineNumber += 2;
		}
		
		return machineCode;
		
	}
	public static void main(String[] args) {
		if(args.length != 2){
			System.err.println("invalid number of parameters \"input, output\"");
			return;
		}
		
		String code = "";
		BufferedReader reader;
		
		try {
			reader = new BufferedReader(new FileReader(args[0]));
			for(String line; (line = reader.readLine())!= null;){
				int index = line.indexOf("//");
				
				if(index != -1)
					line = line.substring(0, index); //cut out comments
				
				code += line + "\n";
			}
			
			reader.close();
			
		} catch (IOException e) {
			if(e instanceof FileNotFoundException){
				System.err.println("File " + args[0] + " not found");
			}
			System.err.println("something wrong");
			return;
		}
		
		System.out.println(assemble(code));
	}
}
