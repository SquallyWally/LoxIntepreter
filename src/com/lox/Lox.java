package com.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


public class Lox {
    static boolean hadError = false;


    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: LoxIntepreter [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    //reads and executes the file, since Lox is a scripting language
    private static void runFile(String path) throws IOException {

        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        //Ensure when there will be no code executed that has a known error
        if (hadError) System.exit(65);
    }


    //run the interpreter in Interactive mode
    //Launch LoxIntepreter without any arugments and execute one line at a time
    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (; ; ) { //Empty arguments
            System.out.println("> ");
            String line = reader.readLine();
            if (line == null) break;
            run(line);
            hadError = false; //reset flag, so the user should not kill the session when he/she makes a mistake
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        //Print tokens
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    //Error handling
    static void error(int line, String message) {
        report(line, "", message);
    }

    //Points out where the error is
    private static void report(int line, String where, String message) {
        String bericht = String.format("[line %s] Error %s:%s", line, where, message);
        System.err.println(bericht);
        hadError = true;
    }

}
