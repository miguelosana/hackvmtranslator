import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class Parser {
	private BufferedReader bufferedReader	= null;
	private String line = null;
	public String[] currentCommand;
	
	public enum commandType { C_ARITHMETIC, C_PUSH, C_POP, C_LABEL, C_GOTO, C_IF, C_FUNCTION, C_RETURN, C_CALL}
	Parser(File inputFile){
		try {
			FileReader fileReader = new FileReader(inputFile);
			this.bufferedReader = new BufferedReader(fileReader);
			

		}catch(IOException ex) {
			ex.printStackTrace();
		}

	}
	
	public boolean hasMoreCommands() throws IOException {
		this.line = this.bufferedReader.readLine();
		if(this.line == null) {
			return false;
		}
		return true;
	}
	
	public boolean advance() throws IOException {
		String tempCommand = this.line.replaceAll("//.*$",  "").trim();
		if(tempCommand.equals("") && this.hasMoreCommands() == true) {
		return this.advance();
		}
		currentCommand = tempCommand.split(" ");
		return true;
	}
	
	public commandType commandType() {
		
		commandType result=null;
		switch(currentCommand[0]) {
		case "add":
			result= commandType.C_ARITHMETIC;
			break;
		case "sub":
		result= commandType.C_ARITHMETIC;
			break;
		case "neg":
			result= commandType.C_ARITHMETIC;
			break;
		case "eq":
			result= commandType.C_ARITHMETIC;
			break;
		case "gt":
			result = commandType.C_ARITHMETIC;
			break;
		case "lt":
			result = commandType.C_ARITHMETIC;
			break;
		case "and":
			result = commandType.C_ARITHMETIC;
			break;
		case "or":
			result = commandType.C_ARITHMETIC;
			break;
		case "not":
			result = commandType.C_ARITHMETIC;
			break;
		case "push":
			result = commandType.C_PUSH;
			break;
		case "pop":
			result = commandType.C_POP;
			break;
		case "label":
			result = commandType.C_LABEL;
			break;
		case "goto":
			result = commandType.C_GOTO;
			break;
		case "if-goto":
			result = commandType.C_IF;
			break;

		
		}
		return result;
	}
	
	public String arg1() {
		if(commandType() == commandType.C_ARITHMETIC) {
		return currentCommand[0];
		}else {
			return currentCommand[1];
		}
	}
	
	public Integer arg2() {
		return Integer.parseInt(currentCommand[2]);
	}
	
	public void close() {

		try {
			this.bufferedReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
