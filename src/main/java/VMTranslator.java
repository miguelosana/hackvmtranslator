import java.io.File;

public class VMTranslator {

	public static void main(String[] args) {
		try {
			File  file = new File(args[0]);
			Parser parser = new Parser(file);
			CodeWriter codeWriter = new CodeWriter();
			codeWriter.setFileName(file.getAbsolutePath().replace(".vm", ".asm"));
			
			
			while(parser.hasMoreCommands()) {
				while(parser.advance() ==false) {}
				Parser.commandType commandType = parser.commandType();
				if(parser.commandType() == Parser.commandType.C_ARITHMETIC) {
					codeWriter.writeArithmetic(parser.arg1());
				}else if(parser.commandType() == Parser.commandType.C_POP || parser.commandType() == Parser.commandType.C_PUSH) {
					codeWriter.writePushPop(parser.commandType(), parser.arg1(), parser.arg2());
						
				}else{
					System.out.println("error");
					
				}
			}
			codeWriter.close();
			parser.close();
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
