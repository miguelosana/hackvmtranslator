import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class VMTranslator {

	public static void main(String[] args) {
		try {
			File  file = new File(args[0]);
			CodeWriter codeWriter = new CodeWriter();
			String outputFileName = null;
			
			if(file.isDirectory()) {
				outputFileName = file.getAbsolutePath() + "/" +file.getName()+".asm";
			}else {
				outputFileName = file.getAbsolutePath().replace(".vm", ".asm");
			}
			codeWriter.setFileName(outputFileName);
						codeWriter.writeInit();
			if(!file.isDirectory()) {
				parseFile(file, codeWriter);
			}else {
				for(final File fileEntry: file.listFiles()) {
					if(!fileEntry.isDirectory() && fileEntry.getName().endsWith(".vm")) {
						parseFile(fileEntry, codeWriter);
					}
				}
			}
			codeWriter.close();

			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	public static void parseFile(File file,CodeWriter codeWriter) throws IOException {
		
			Parser parser = new Parser(file);
			while(parser.hasMoreCommands()) {
				while(parser.advance() ==false) {}
				Parser.commandType commandType = parser.commandType();
				if(parser.commandType() == Parser.commandType.C_ARITHMETIC) {
					codeWriter.writeArithmetic(parser.arg1());
				}else if(parser.commandType() == Parser.commandType.C_POP || parser.commandType() == Parser.commandType.C_PUSH) {
					codeWriter.writePushPop(parser.commandType(), parser.arg1(), parser.arg2(),false);
						
				}else if(parser.commandType() == Parser.commandType.C_LABEL){
					codeWriter.writeLabel(parser.arg1());	
				}else if(parser.commandType() == Parser.commandType.C_GOTO){
					codeWriter.writeGoto(parser.arg1(), true);
				}else if(parser.commandType() == Parser.commandType.C_IF){
					codeWriter.writeIf(parser.arg1());
				}else if(parser.commandType() == Parser.commandType.C_FUNCTION) {
					codeWriter.writeFunction(parser.arg1(), parser.arg2());
				}else if(parser.commandType() == Parser.commandType.C_CALL) {
						codeWriter.writeCall(parser.arg1(), parser.arg2());
				}else if(parser.commandType() == Parser.commandType.C_RETURN) {
					codeWriter.writeReturn();
				}
				else {
					
					System.out.println("Error");
					System.out.println(parser.getCurrentCommand());
				}

			}
			parser.close();
	}
}
