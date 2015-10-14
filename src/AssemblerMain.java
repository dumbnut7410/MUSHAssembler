import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;


public class AssemblerMain {
	public static HashMap<String, String> opcodes = new HashMap<String, String>();
	public static HashMap<String, String> registers = new HashMap<String, String>();
	
	public static String assemble(String code){
		
		int codeLineNumber = 0; //assembly line number
		int lineNumber = 0xf000; //machine code address
		String machineCode = "";
		
		String[] lines = code.split("\n");
		
		//Scan through code to find labels
		for(String line: lines){
			//TODO: implement 
		}
		
		//actually assemble the code
		for(String line: lines){
			codeLineNumber++;
			if(line.hashCode() == 0)
				continue; 
			
			String[] instr = line.split(" |\\\t");//seperate parts
			
			machineCode += String.format("0x%H",lineNumber) + " ";
			machineCode += opcodes.get(instr[0]);
			
			if(!instr[0].equals("li")){
				for(int i = 1; i < 4; i++){ //each hex digit
					if(instr.length > i){
						if(registers.get(instr[i]) == null)
							System.err.println("Error near line " + codeLineNumber);
						
						machineCode += registers.get(instr[i]);
					} else{
						machineCode += "Q";
					}
				}
			} else{
				try{
					if(Integer.parseInt(instr[1]) > 0x0FFF){
						System.err.println("Error near line " + codeLineNumber);
					}
				} catch (Exception e){
					System.err.println("Error near line " + codeLineNumber);
				}
				machineCode += String.format("%H3", instr[1]);
			}
			machineCode += "\n";
			lineNumber += 2;
		}
		
		return machineCode;
		
	}
	
	public static void setUpMaps(){
		opcodes.put("lw", "0");
		opcodes.put("sw", "1");
		opcodes.put("add", "2");
		opcodes.put("j", "3");
		opcodes.put("beq", "4");
		opcodes.put("sll", "5");
		opcodes.put("srl", "6");
		opcodes.put("li", "7");
		opcodes.put("and", "8");
		opcodes.put("or", "9");
		opcodes.put("ret", "A");
		opcodes.put("sub", "B");
		opcodes.put("jal", "C");
		opcodes.put("push", "D");
		opcodes.put("pop", "E");
		opcodes.put("mov", "F");
		
		registers.put("$imm", "0");
		registers.put("$ret", "1");
		registers.put("$at", "2");
		registers.put("$p0", "3");
		registers.put("$p1", "4");
		registers.put("$t0", "5");
		registers.put("$t1", "6");
		registers.put("$t2", "7");
		registers.put("$t3", "8");
		registers.put("$t4", "9");
		registers.put("$t5", "A");
		registers.put("$t6", "B");
		registers.put("$t7", "C");
		registers.put("$ra", "D");
		registers.put("$s0", "E");
		registers.put("$s1", "F");
	}
	public static void main(String[] args) {
		setUpMaps();
		
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
