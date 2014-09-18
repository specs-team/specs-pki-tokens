package org.specs.pkitokens.client;

import org.apache.commons.cli.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.specs.pkitokens.core.Token;
import org.specs.pkitokens.core.VerificationCertProvider;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.Security;

public class ConsoleClient {

    private String stsAddress;
    private String truststoreFile;
    private String truststorePass;

    public ConsoleClient(String stsAddress, String truststoreFile, String truststorePass) {
        this.stsAddress = stsAddress;
        this.truststoreFile = truststoreFile;
        this.truststorePass = truststorePass;
    }

    public static void main(String[] args) throws Exception {

        Options options = createCliOptions();

        CommandLine commandLine = null;
        try {
            CommandLineParser parser = new GnuParser();
            commandLine = parser.parse(options, args);
        }
        catch (ParseException e) {
            System.out.println("Invalid parameters: " + e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("ant", options, true);
            System.exit(1);
        }

        String stsAddress = commandLine.getOptionValue("sts-address");
        String truststoreFile = commandLine.getOptionValue("truststore-file");
        String truststorePass = commandLine.getOptionValue("truststore-pass");

        new ConsoleClient(stsAddress, truststoreFile, truststorePass).start();
    }

    private void start() throws Exception {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        VerificationCertProvider verifCertProvider =
                new VerificationCertProviderWS(stsAddress, truststoreFile, truststorePass);
        TRLCache trlCache = new TRLCache(stsAddress, truststoreFile, truststorePass);
        PkiTokenRetriever pkiTokenRetriever = new PkiTokenRetriever(stsAddress,
                truststoreFile, truststorePass,
                null, null,
                verifCertProvider);

        PkiTokenValidator pkiTokenValidator = new PkiTokenValidator(verifCertProvider, trlCache);

        printHelp();

        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = consoleReader.readLine();
            String[] parts = line.split(" +");
            if (line.equals("exit")) {
                break;
            }
            else if (line.equals("help")) {
                printHelp();
            }
            else if (parts[0].equals("obtain") && parts.length == 4) {
                obtain(pkiTokenRetriever, parts);
            }
            else if (parts[0].equals("validate") && parts.length == 2) {
                validate(pkiTokenValidator, parts);
            }
            else if (parts[0].equals("isRevoked") && parts.length == 2) {
                checkRevoked(trlCache, parts);
            }
            else if (parts[0].equals("isRevoked") && parts.length == 2) {
                checkRevoked(trlCache, parts);
            }
            else if (parts[0].equals("printTRL") && parts.length == 1) {
                printTRL(trlCache);
            }
            else if (line.equals("")) {
                // do nothing
            }
            else {
                System.out.println("Invalid command. Type 'help' for help.");
            }
        }

        trlCache.stop();
    }

    private void printHelp() {
        System.out.println("Commands:");
        System.out.println("obtain <username> <password> <sla-id>");
        System.out.println("validate <token>");
        System.out.println("isRevoked <token-id>");
        System.out.println("printTRL");
        System.out.println("exit");
        System.out.println();
    }

    private void obtain(PkiTokenRetriever pkiTokenRetriever, String[] args) {

        String username = args[1];
        String password = args[2];
        int slaId = Integer.parseInt(args[3]);

        try {
            Token token = pkiTokenRetriever.obtainToken(username, password, slaId);
            System.out.println("The token has been retrieved successfully:");
            System.out.println(token.getEncodedValue());
        }
        catch (Exception e) {
            System.out.println("obtain failed: " + e.getMessage());
        }
    }

    private void validate(PkiTokenValidator pkiTokenValidator, String[] args) {

        String encodedToken = args[1];
        try {
            Token token = pkiTokenValidator.decodeAndValidate(encodedToken);
            System.out.println("The token is valid.");
            System.out.println("Token content:");
            System.out.println(token.dump());
        }
        catch (Exception e) {
            System.out.println("validate failed: " + e.getMessage());
        }
    }

    private void checkRevoked(TRLCache trlCache, String[] args) {

        String tokenId = args[1];
        try {
            boolean isRevoked = trlCache.isRevoked(tokenId);
            System.out.println("Revoked: " + isRevoked);
        }
        catch (Exception e) {
            System.out.println("isRevoked failed: " + e.getMessage());
        }
    }

    private void printTRL(TRLCache trlCache) {

        try {
            System.out.println(trlCache.getAllRevokedTokens().toString());
        }
        catch (Exception e) {
            System.out.println("printTRL failed: " + e.getMessage());
        }
    }

    private static Options createCliOptions() {
        // create the Options
        Options options = new Options();

        options.addOption(new Option("help", "Print this message"));
        options.addOption(OptionBuilder.withLongOpt("sts-address")
                .withDescription("Security Token Service address")
                .hasArg()
                .withArgName("stsAddress")
                .isRequired(true)
                .create());
        options.addOption(OptionBuilder.withLongOpt("truststore-file")
                .withDescription("Truststore JKS file with CA certificate")
                .hasArg()
                .withArgName("truststoreFile")
                .isRequired(true)
                .create());
        options.addOption(OptionBuilder.withLongOpt("truststore-pass")
                .withDescription("Truststore JKS file password")
                .hasArg()
                .withArgName("truststorePass")
                .isRequired(true)
                .create());

        return options;
    }
}
