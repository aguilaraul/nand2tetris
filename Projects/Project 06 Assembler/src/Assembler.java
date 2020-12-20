/**
 * @author Raul Aguilar
 * @date    October 27, 2020
 */
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.PrintWriter;

public class Assembler {
    
    // ALGORITHM:
    // get input file name
    // create output file name and stream

    // create symbol table
    // do first pass to build symbol table (no output yet)
    // do second pass to output translated ASM to HACK code

    // print "done" message to user
    // close output file stream

    public static void main(String[] args) {
        String inputFileName, outputFileName;
        PrintWriter outputFile = null;
        SymbolTable symbolTable = new SymbolTable();

        int romAddress = 0, ramAddress = 16;

        // get input file name from command line or console input
        if(args.length == 1) {
            System.out.println("command line arg = " + args[0]);
            inputFileName = args[0];
        } else {
            Scanner keyboard = new Scanner(System.in);

            System.out.println("Please enter assembly file name you would like to translate");
            System.out.println("Don't forget the .asm etension: ");
            inputFileName = keyboard.nextLine();

            keyboard.close();
        }

        outputFileName = inputFileName.substring(0, inputFileName.lastIndexOf('.')) + ".hack";

        try {
            outputFile = new PrintWriter(new FileOutputStream(outputFileName));
        } catch(FileNotFoundException ex) {
            System.err.println("Could not open output file " + outputFileName);
            System.err.println("Run program again, make sure you have write permissions, etc.");
            System.exit(0);
        }

        firstPass(inputFileName, symbolTable, romAddress, ramAddress);
        secondPass(inputFileName, symbolTable, outputFile, romAddress, ramAddress);

        System.out.println("Finished assembling. Program exiting.");
        outputFile.close();
        System.exit(0);
    }


    /**
     * The first pass through the file finds and stores user-defined variables and labels in the symbol
     * table without writing anything to the output file
     * @param inputFileName the file being read
     * @param symbolTable   the symbol table to store variables and lables (initialzies with predefined symbols)
     * @param romAddress    the current PC rom address of the instruction
     * @param ramAddress    the current ram address to store the variable in
     * @return  symbol table filled-in with variables and labels found in file
     */
    private static SymbolTable firstPass(String inputFileName, SymbolTable symbolTable, int romAddress, int ramAddress) {
        Parser p = new Parser();
        symbolTable.SymbolTable();
        p.Parser(inputFileName);
        while(p.hasMoreCommands()) {
            p.advance();
            if(p.getCommandType() == Command.L_COMMAND) {
                symbolTable.addEntry(p.getSymbol(), romAddress, p.getLineNumber());
            }
            if(p.getCommandType() == Command.A_COMMAND) {
                try {
                    int decimal = Integer.parseInt(p.getSymbol());
                } catch(NumberFormatException notADecimal) {
                    if(!symbolTable.contains(p.getSymbol()) && Character.isLowerCase(p.getSymbol().charAt(0)) ) {
                        symbolTable.addEntry(p.getSymbol(), ramAddress, p.getLineNumber());
                        ramAddress++;
                    }
                }
                
                romAddress++;
            }
            if(p.getCommandType() == Command.C_COMMAND) {
                romAddress++;
            }
        }

        return symbolTable;
    }

    /**
     * Second pass through the file converts each line to binary code, while using the filled-in symbol
     * table from the first pass to convert symbols and labels
     * @param inputFileName the file being read
     * @param symbolTable   the predefined symbol table
     * @param outputFile    the name of the output HACK file
     * @param romAddress    the current PC rom address of the instruction
     * @param ramAddress    the current ram address for user-defined variables
     */
    private static void secondPass(String inputFileName, SymbolTable symbolTable, PrintWriter outputFile, int romAddress, int ramAddress) {
        Parser p = new Parser();
        p.Parser(inputFileName);
        while(p.hasMoreCommands()) {
            p.advance();
            if(p.getCommandType() == Command.C_COMMAND) {
                String instruction = "111" + p.getComp() + p.getDest() + p.getJump() + '\n';
                outputFile.write(instruction);
                romAddress++;
            }
            if(p.getCommandType() == Command.A_COMMAND) {
                try {
                    int decimal = Integer.parseInt(p.getSymbol());
                    String dec = Code.decimalToBinary(decimal) + '\n';
                    outputFile.write(dec);
                    romAddress++;
                } catch(NumberFormatException notADecimal) {
                    if(symbolTable.contains(p.getSymbol())) {
                        String dec = Code.decimalToBinary(symbolTable.getAddress(p.getSymbol())) + '\n';
                        outputFile.write(dec);
                    } else {
                        symbolTable.addEntry(p.getSymbol(), ramAddress, p.getLineNumber());
                        ramAddress++;
                    }
                    romAddress++;
                }
            }
        }
    }
}