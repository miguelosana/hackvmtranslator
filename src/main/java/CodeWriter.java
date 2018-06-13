import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class CodeWriter {

	private BufferedWriter bw = null;
	private int jumpCount =0;
	private int returnCount=0;
	private String name;
	
	public void setFileName(String fileName) {
		try {
			File outputFile = new  File(fileName);
			FileOutputStream fos = new FileOutputStream(outputFile);
			this.bw = new BufferedWriter(new OutputStreamWriter(fos));
			
		}catch(IOException ex) {
			ex.printStackTrace();
		}

	}
	
	public void setName(String name) {
		
		this.name = name;
	}
	
	public void writeArithmetic(String command) throws IOException{
		switch (command){
		case "add":
			fetchPop();
			writeLine("D=D+M"); //x=m
			pushResult();
			break;
		case "sub":
			fetchPop();
			writeLine("D=M-D"); //x-y
			pushResult();
			break;
		case "eq":
			jumpTemplate("JEQ");
			jumpCount++;
			
			break;
		case "lt":
			jumpTemplate("JLT");
			jumpCount++;
			break;
		case "gt":
			jumpTemplate("JGT");
			jumpCount++;
			break;
		case "neg":
			writeLine("@SP");
			writeLine("AM=M-1");
			writeLine("D=M");
			writeLine("M=-D");
			writeLine("@SP");
			writeLine("AM=M+1");
			break;
		case "and":
			fetchPop();
			writeLine("M=D&M");
			break;
		case "or":
			fetchPop();
			writeLine("M=D|M");
			break;
		case "not":
			writeLine("@SP");
			writeLine("AM=M-1");
			writeLine("D=M");
			writeLine("M=!D");
			writeLine("@SP");
			writeLine("AM=M+1");
			break;

			
			

		}
		
		

			
	}
	
	private void jumpTemplate(String jumpType) throws IOException{
		
			writeLine("@SP");
			writeLine("AM=M-1");
			writeLine("D=M");
			writeLine("A=A-1");
			writeLine("D=M-D");
			writeLine("@FALSE"+Integer.toString(jumpCount));
			writeLine("D;"+jumpType);
			//not equal so continue and set -1
			writeLine("@SP");
			writeLine("A=M-1");
			writeLine("M=0");
			writeLine("@CONTINUE"+Integer.toString(jumpCount));
			writeLine("0;JMP");
			writeLine("(FALSE"+Integer.toString(jumpCount)+")");
			writeLine("@SP");
			writeLine("A=M-1");
			writeLine("M=-1");
			writeLine("@CONTINUE"+Integer.toString(jumpCount));
			writeLine("(CONTINUE"+Integer.toString(jumpCount)+")");
	}
	
	
	private void fetchPop() throws IOException {
		
			writeLine("@SP");
			writeLine("AM=M-1");
			writeLine("D=M");
			writeLine("A=A-1");
	}
	private void pushResult() throws IOException {
		
			writeLine("@SP");
			writeLine("AM=M-1");
			writeLine("M=D");
			writeLine("@SP");
			writeLine("AM=M+1");
	}
	public void writePushPop(Parser.commandType command, String segment, int index, boolean isDirect) throws IOException {
		String segmentLabel = null;
		switch(segment) {
		case "constant":
			segmentLabel = "@SP";
			break;
		case "local":
			segmentLabel = "@LCL";
			break;
		case "argument":
			segmentLabel = "@ARG";
			break;
		case "temp":
			segmentLabel = "@TEMP";
			index +=5;
			break;
		case "static":
			segmentLabel = "@"+name+Integer.toString(index);
			isDirect = true;
			break;
		case "this":
			segmentLabel = "@THIS";
			break;
		case "that": 
			segmentLabel = "@THAT";
			break;
			default:
				break;
	
			
		}

			if(segment.equals("pointer")) {
				isDirect = true;
				if(index == 0 ) {
					segmentLabel = "@THIS";
					
				}else if(index ==1) {
					segmentLabel = "@THAT";
				}
				
			}
		if(command == Parser.commandType.C_PUSH) {

			writeLine("@"+Integer.toString(index));
			writeLine("D=A");
			if(segment.equals("constant")) {
				writeLine("@SP");
				writeLine("A=M");
				writeLine("M=D");
				writeLine("@SP");
				writeLine("M=M+1");
				
			}else {
				writeLine(segmentLabel);
				writeLine("D=M");
			String pointer = (isDirect)? "" : "@" + index + "\nA=D+A\nD=M\n";
				writeLine(pointer);
				writeLine("@SP");
				writeLine("A=M");
				
				writeLine("M=D");
				writeLine("@SP");
				writeLine("M=M+1");
			}
		}else if(command == Parser.commandType.C_POP) {
			String pointer = (isDirect)? "D=A\n" : "D=M\n@" + index + "\nD=D+A\n";

			writeLine(segmentLabel);
			writeLine(pointer);
			writeLine("@R13");
			writeLine("M=D");
			writeLine("@SP");
			writeLine("AM=M-1");
			writeLine("D=M");
			writeLine("@R13");
			writeLine("A=M");
			writeLine("M=D");
					
			
			
			
		}

	}

	public void writeInit() throws IOException {
		writeLine("@256");
		writeLine("D=A");
		writeLine("@SP");
		writeLine("AM=D");
		
		writeCall("Sys.init",0);
	}

	public void writeFunction(String functionName, int argn) throws IOException{
		writeLabel(functionName);
		for(int i=0;i < argn;i++){
			writePushPop(Parser.commandType.C_PUSH, "constant", 0,false);
		}
	}

	public void writeCall(String functionName, int argn) throws IOException {
		String returnAddress = String.format("return-%s-%s",functionName,Integer.toString(returnCount));
		
		pushAddress(returnAddress);
		writeLine("@LCL");
		writeLine("D=M");
		writeLine("@SP");
		writeLine("A=M");
		writeLine("M=D");
		writeLine("@SP");
		writeLine("M=M+1");
		
		
		writeLine("@ARG");
		writeLine("D=M");
		writeLine("@SP");
		writeLine("A=M");
		writeLine("M=D");
		writeLine("@SP");
		writeLine("M=M+1");
		
		
		writeLine("@THIS");
		writeLine("D=M");
		writeLine("@SP");
		writeLine("A=M");
		writeLine("M=D");
		writeLine("@SP");
		writeLine("M=M+1");
		
		writeLine("@THAT");
		writeLine("D=M");
		writeLine("@SP");
		writeLine("A=M");
		writeLine("M=D");
		writeLine("@SP");
		writeLine("M=M+1");

		//reposition arg
		writeLine("@SP");
		writeLine("D=M");
		writeLine("@5");
		writeLine("D=D-A");
		writeLine("@"+argn);
		writeLine("D=D-A");
		writeLine("@ARG");
		writeLine("M=D");

		//set lcl to sp
		writeLine("@SP");
		writeLine("D=M");
		writeLine("@LCL");
		writeLine("M=D");

		writeGoto(functionName,true);
		writeLabel(returnAddress);
		returnCount++;


	}
	public void writeReturn() throws IOException {
		//frame = lcl
		writeLine("@LCL");
		writeLine("D=M");
		writeLine("@R11");
		writeLine("M=D");
		
		
		//ret = FRAME - 5 and pop to arg
		writeLine("@5");
		writeLine("A=D-A");
		writeLine("D=M");
		writeLine("@R12");
		writeLine("M=D");
	
		writePushPop(Parser.commandType.C_POP, "argument",0, false);
		//sp =arg +1;
		writeLine("@ARG");
		writeLine("D=M");
		writeLine("@SP");
		writeLine("M=D+1");
		
		//that =frame -1
		writeLine("@R11");
		writeLine("D=M-1");
		writeLine("AM=D");
		writeLine("D=M");
		writeLine("@THAT");
		writeLine("M=D");
		
		//this = frame -2
		writeLine("@R11");
		writeLine("D=M-1");
		writeLine("AM=D");
		writeLine("D=M");
		writeLine("@THIS");
		writeLine("M=D");
		
		//arg = frame -3
		writeLine("@R11");
		writeLine("D=M-1");
		writeLine("AM=D");
		writeLine("D=M");
		writeLine("@ARG");
		writeLine("M=D");
		
		//lcl= frame-4
		writeLine("@R11");
		writeLine("D=M-1");
		writeLine("AM=D");
		writeLine("D=M");
		writeLine("@LCL");
		writeLine("M=D");
		
		//goto ret
		writeLine("@R12");
		writeLine("A=M");
		writeLine("0;JMP");
		
		
		
		
			
		
	
	}
	public void pushAddress(String label) throws IOException {
		
		writeLine(String.format("@%s",label));
		writeLine("D=A");
		writeSinglePush();
	}

	public void writeSinglePush() throws IOException {
		writeLine("@SP");
		writeLine("A=M");
		writeLine("M=D");
		writeLine("@SP");
		writeLine("M=M+1");

	}
	public void writeLabel(String label) throws IOException {
		writeLine(String.format("(%s)", label));
	}
	
	public void writeGoto(String label,boolean jump) throws IOException {
		writeLine(String.format("@%s", label));
		if(jump){
			writeLine("0;JMP");
		}
		
	}
	public void writeIf(String label) throws IOException {
		fetchPop();
		writeGoto(label,false);
		writeLine("D;JNE");
	}
	
	public void close() throws IOException {

		this.bw.close();
	}
	
	private void writeLine(String line) throws IOException {
		this.bw.write(line);
		this.bw.newLine();
	}
	
}
