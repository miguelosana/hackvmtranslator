import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class CodeWriter {

	private BufferedWriter bw = null;
	private int jumpCount =0;
	private String name;
	
	public void setFileName(String fileName) {
		try {
			File outputFile = new  File(fileName);
			name = outputFile.getName().replace(".asm", "");
			FileOutputStream fos = new FileOutputStream(outputFile);
			this.bw = new BufferedWriter(new OutputStreamWriter(fos));
			
		}catch(IOException ex) {
			ex.printStackTrace();
		}

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
	public void writePushPop(Parser.commandType command, String segment, int index) throws IOException {
		String segmentLabel = null;
		boolean isDirect = false;
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
				writeLine("AM=M+1");
				
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
