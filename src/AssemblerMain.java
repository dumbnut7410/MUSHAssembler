import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class AssemblerMain {
	public static String assemble(String code, boolean coe) {

		int codeLineNumber = 0; // assembly line number
		int lineNumber = 0xf000; // machine code address
		String machineCode = "";

		final String[] lines = code.split("\n");

		// Scan through code to find labels
		for (final String line : lines) {
			if (line.hashCode() == 0)
				continue;

			if (line.contains(":")) {
				AssemblerMain.labels.put(line.substring(0, line.length() - 1),
						lineNumber);
				continue;
			}
			lineNumber += 1;
		}
		lineNumber = 0xf000; // machine code address
		// actually assemble the code
		for (final String line : lines) {
			codeLineNumber++;

			final String[] instr = line.split(" |\\\t");// seperate parts

			if (AssemblerMain.opcodes.get(instr[0]) == null)
				continue;

			if (!coe)
				machineCode += String.format("0x%H", lineNumber) + " ";
			machineCode += AssemblerMain.opcodes.get(instr[0]);

			if (!instr[0].equals("li")) {
				for (int i = 1; i < 4; i++) { // each hex digit
					if (instr.length > i) {
						if (AssemblerMain.registers.get(instr[i]) == null)
							System.err.println("Error near line "
									+ codeLineNumber);

						machineCode += AssemblerMain.registers.get(instr[i]);
					} else {
						machineCode += "0";
					}
				}
			} else {
				try {
					if (Integer.parseInt(instr[1]) > 0x0FFF) {
						System.err.println("Error near line " + codeLineNumber);
					}
				} catch (final Exception e) {
					System.err.println("Error near line " + codeLineNumber);
				}
				try {
					machineCode += String.format("%3H",
							Integer.parseInt(instr[1])).replace(" ", "0");
				} catch (final NumberFormatException e) {
					System.out.println("label used on line " + codeLineNumber);
					machineCode += String.format("%3h",
							AssemblerMain.labels.get(instr[1])).substring(1);
				}

			}
			if (coe)
				machineCode += ",";
			machineCode += "\n";
			lineNumber += 1;
		}

		return machineCode;

	}

	public static void main(String[] args) {
		AssemblerMain.setUpMaps();
		boolean coe = false;
		if (args.length < 2) {
			System.err
					.println("invalid number of parameters \"input, output\"");
			return;
		}

		String code = "";
		BufferedReader reader;

		try {
			reader = new BufferedReader(new FileReader(args[0]));
			for (String line; (line = reader.readLine()) != null;) {
				final int index = line.indexOf("//");

				if (index != -1)
					line = line.substring(0, index); // cut out comments

				code += line + "\n";
			}

			reader.close();

		} catch (final IOException e) {
			if (e instanceof FileNotFoundException) {
				System.err.println("File " + args[0] + " not found");
			}
			System.err.println("something wrong");
			return;
		}

		if ((args.length > 2) && args[2].equals("-coe"))
			coe = true;

		String done = AssemblerMain.assemble(code, coe);

		if (coe)
			done = "MEMORY_INITIALIZATION_RADIX=16;\nMEMORY_INITIALIZATION_VECTOR=\n"
					+ "6942,\n" + done.substring(0, done.length() - 2) + "\n;";
		try {
			final BufferedWriter writer = new BufferedWriter(new FileWriter(
					args[1]));
			writer.write(done);

			writer.close();
		} catch (final Exception e) {
			System.err.println("error writing file");
		}
		System.out.println(done);
	}

	public static void setUpMaps() {
		AssemblerMain.opcodes.put("lw", "0");
		AssemblerMain.opcodes.put("sw", "1");
		AssemblerMain.opcodes.put("add", "2");
		AssemblerMain.opcodes.put("j", "3");
		AssemblerMain.opcodes.put("beq", "4");
		AssemblerMain.opcodes.put("sll", "5");
		AssemblerMain.opcodes.put("srl", "6");
		AssemblerMain.opcodes.put("li", "7");
		AssemblerMain.opcodes.put("and", "8");
		AssemblerMain.opcodes.put("or", "9");
		AssemblerMain.opcodes.put("ret", "A");
		AssemblerMain.opcodes.put("sub", "B");
		AssemblerMain.opcodes.put("jal", "C");
		AssemblerMain.opcodes.put("push", "D");
		AssemblerMain.opcodes.put("pop", "E");
		AssemblerMain.opcodes.put("mov", "F");

		AssemblerMain.registers.put("$imm", "0");
		AssemblerMain.registers.put("$ret", "1");
		AssemblerMain.registers.put("$at", "2");
		AssemblerMain.registers.put("$p0", "3");
		AssemblerMain.registers.put("$p1", "4");
		AssemblerMain.registers.put("$t0", "5");
		AssemblerMain.registers.put("$t1", "6");
		AssemblerMain.registers.put("$t2", "7");
		AssemblerMain.registers.put("$t3", "8");
		AssemblerMain.registers.put("$t4", "9");
		AssemblerMain.registers.put("$t5", "A");
		AssemblerMain.registers.put("$t6", "B");
		AssemblerMain.registers.put("$t7", "C");
		AssemblerMain.registers.put("$ra", "D");
		AssemblerMain.registers.put("$s0", "E");
		AssemblerMain.registers.put("$s1", "F");
	}

	public static HashMap<String, String> opcodes = new HashMap<String, String>();

	public static HashMap<String, String> registers = new HashMap<String, String>();
	public static HashMap<String, Integer> labels = new HashMap<String, Integer>();
}
