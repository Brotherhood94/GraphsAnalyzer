package statisticalAnalyzer;

import com.martiansoftware.jsap.*;

public class Graph {

    public static void main(String[] args) {

        SimpleJSAP jsap = null;
        try {
            jsap = new SimpleJSAP("java -jar Graphs statisticalAnalyzer.Analyzer", "Analyze your graphs",
                    new Parameter[]{
                            new UnflaggedOption("labels", JSAP.STRING_PARSER, JSAP.REQUIRED,"NodesLabel.csv file path."),
                            new UnflaggedOption("edges", JSAP.STRING_PARSER, JSAP.REQUIRED,"Edges.csv file path."),
                            new UnflaggedOption("output", JSAP.STRING_PARSER, JSAP.REQUIRED,"Output.csv file path."),
                    });
        }catch (JSAPException ex) {
            System.out.println(ex.getCause());
            System.exit(0);
        }


        JSAPResult config = jsap.parse(args);
        if ( jsap.messagePrinted() ) System.exit( 0 );

        Analyzer analyzer = Analyzer.FactoryAnalyzer(config.getString("labels"),config.getString("output"));
        analyzer.readGraphMappedMemory(config.getString("edges"));
    }

}
