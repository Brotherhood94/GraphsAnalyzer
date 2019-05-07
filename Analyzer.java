package statisticalAnalyzer;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.ints.Int2IntAVLTreeMap;
import it.unimi.dsi.logging.ProgressLogger;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.stat.SummaryStats;
import it.unimi.dsi.webgraph.ArrayListMutableGraph;
import it.unimi.dsi.webgraph.ImmutableGraph;
import it.unimi.dsi.webgraph.NodeIterator;
import it.unimi.dsi.webgraph.Transform;
import it.unimi.dsi.webgraph.algo.StronglyConnectedComponents;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Scanner;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Analyzer {

    private final static Logger LOGGER = LoggerFactory.getLogger(Analyzer.class);
    private static Analyzer analyzer = null;

    //private final String labelsPath;
    private final CharSequence hashtag = "#", edgeHeader = "Source", nodeHeader = "Id";
    private final String regex = "[,\\s+]";   //da cambiare
    private final Scanner scanner;

    private final File file;

    private Int2ObjectMap<InterestingNode> interestingNodes;
    private Int2IntOpenHashMap idMapping;

    private Int2IntAVLTreeMap treeSet;

    private ImmutableGraph normalG;
    private ImmutableGraph invertedG;
    private ImmutableGraph symmG;

    private GlobalDirectedGraph gdg;
    private GlobalUndirectedGraph gug;

    private boolean append = false;

    private Analyzer(String outputPath){
        scanner = new Scanner(System.in);
        file = new File(outputPath);
        if(file.exists()){
            LOGGER.info("Specified OutpuFile already exists in this path "+outputPath+".\nWould you overwrite it? [Y/n]");
            if(scanner.next().equals("Y"))
                LOGGER.warn("Overwriting...");
            else{
                LOGGER.warn("Operation Aborted.");
                System.exit(0);
            }
        }
        this.gdg = new GlobalDirectedGraph();
        this.gug = new GlobalUndirectedGraph();
    }

    public static Analyzer FactoryAnalyzer(String labelsPath, String outputPath){
        if(analyzer==null){
            analyzer  = new Analyzer(outputPath);
            if(analyzer.readLabels(labelsPath)==false)
                System.exit(0);
        }
        return analyzer;
    }

    private boolean readLabels(String labelsPath){
        String line[], act;
        treeSet = new Int2IntAVLTreeMap();
        treeSet.defaultReturnValue(-1);
        try{
            LineIterator it = FileUtils.lineIterator(new File(labelsPath));
            while( it.hasNext()){
                act = it.nextLine();
                if(act.contains(hashtag) || act.contains(nodeHeader) || act.trim().equals(""))
                    continue;
                line = act.split(regex);
                treeSet.putIfAbsent((Integer.valueOf(line[0].trim())).intValue(), -1 );
            }
        }catch (FileNotFoundException ex) {
            LOGGER.error("File "+labelsPath+" not found!");
            return false;
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
            return false;
        }
        this.SettingOutputFile();
        return true;
    }

    private void SettingOutputFile(){
        String _INFO = ";;;;;;GLOBAL SYMMETRIZED GRAPH;;;;;;;;GLOBAL ORIGINAL GRAPH;;;;";
        for(int ID : treeSet.keySet())
            _INFO+=";;;SYMMETRIZED;;;;"+ID+";;;;ORIGINAL;;;;;";
        _INFO+="\n";
        _INFO+="ID;Nodes;Edges;;Diameter;Avg Distance;Reachable Nodes;N°Strongly Connected;Mean Degree;Variance Degree;;Diameter;Avg Distance;Reachable Nodes;N°Strongly Connected;Mean Degree;Variance OutDegree;Variance InDegree;";
        for(int ID : treeSet.keySet())
            _INFO+=";Presence;MaxDistance;Avg Distance;Reached Node;Degree;Variance Degree;;MaxDistance;Avg Distance;Reached Toward;Reached Backward;Out Degree; In Degree;Variance OutDegree; Variance InDegree;";
        _INFO+="\n";
        this.writeCSV(_INFO);
    }

    public boolean readGraphMappedMemory(String graphsPath){
        String line[], act="";
        char c = '\0';
        int source, target, mappedSource, mappedTarget,graphID = -1;
        InterestingNode temp;
        LinkedHashSet<IntArrayList> matrix = new LinkedHashSet<>();
        idMapping = new Int2IntOpenHashMap();
        idMapping.defaultReturnValue(-1);
        interestingNodes = new Int2ObjectOpenHashMap();
        try {
            FileChannel fileChannel = new RandomAccessFile(new File(graphsPath), "r").getChannel();
            MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
            int i;
            for(i = 0; i < buffer.limit(); i++){
                if( (c = (char)buffer.get(i))=='\n')
                    break;
                act+=c;
            }
            buffer.position(i+1);
            try{
                graphID = Integer.valueOf(act.trim());
            }catch(NumberFormatException e){
                LOGGER.error("Csv file first line must be the statisticalAnalyzer.Graph's ID.");
                return false;
            }
            while(buffer.hasRemaining()){
                act="";
                while((c = (char)buffer.get())!='\n')
                    act+=c;
                if(act.contains(hashtag) || act.contains(edgeHeader) || act.trim().equals(""))
                    continue;
                line = act.split(regex);
                if(line.length==1){
                    startAnalysis(graphID,matrix);
                    reset(matrix);
                    graphID = Integer.valueOf(line[0].trim());
                    continue;
                }
                source = Integer.valueOf(line[0].trim());
                target = Integer.valueOf(line[1].trim());

                idMapping.putIfAbsent(source, idMapping.size());
                mappedSource = idMapping.get(source);
                if(treeSet.containsKey(source)){
                    interestingNodes.putIfAbsent(mappedSource, (temp = new InterestingNode(source)));
                    treeSet.replace(source, -1, mappedSource);
                    temp.setPresence();
                }

                idMapping.putIfAbsent(target, idMapping.size());
                mappedTarget = idMapping.get(target);
                if(treeSet.containsKey(target)){
                    interestingNodes.putIfAbsent(mappedTarget, (temp = new InterestingNode(target)));
                    treeSet.replace(target, -1, mappedTarget);
                    temp.setPresence();
                }
                matrix.add(new IntArrayList(new int[]{mappedSource,mappedTarget}));
                //LOGGER.info("Added Edge ("+mappedSource+","+mappedTarget+")");
            }
        }catch (FileNotFoundException ex) {
            LOGGER.error("File "+graphsPath+" not found!");
            return false;
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
            return false;
        }
        startAnalysis(graphID,matrix);
        return true;
    }

    public void getGraphStats(){
        int out, in;
        InterestingNode temp = null;
        NodeIterator iter = normalG.nodeIterator();
        SummaryStats normal = new SummaryStats(), inverse = new SummaryStats(), symmetrized = new SummaryStats();
        SummaryStats totN = new SummaryStats(), totS = new SummaryStats();
        while(iter.hasNext()){
            int node = iter.nextInt();
            gdg.setInfDistance(breadthFirst(node, this.normalG, totN, false));
            gug.setInfDistance(breadthFirst(node, this.symmG, totS, true));
            normal.add((out = this.normalG.outdegree(node)) );
            inverse.add( (in = this.invertedG.outdegree(node) ));
            symmetrized.add(out+in);
            if( (temp = interestingNodes.get(node)) != null)
                temp.setDegree(out, in);
        }


        gdg.setDiameter((int) totN.max());
        gug.setDiameter((int) totS.max());

        gdg.setAvgDistance((float) totN.mean());
        gug.setAvgDistance((float) totS.mean());

        gdg.setReachableNodes();    //Da chiedere se bisogna togliere i cappi, guarda questo set in gdg
        gug.setReachableNodes();

        setStronglyConnected(normalG, gdg);
        setStronglyConnected(symmG, gug);

        gdg.setMeanDegree((float) normal.mean());
        gdg.setVarianceDegree((float) normal.variance(), (float) inverse.variance());

        gug.setMeanDegree((float) symmetrized.mean());
        gug.setVarianceDegree((float) symmetrized.variance(), 0);

        for(InterestingNode supp : interestingNodes.values()){
            supp.setVarianceDegree(gdg.getMeanDegree());
            supp.setVarianceDegreeS(gug.getMeanDegree());
        }
    }

    private int breadthFirst(int ID, ImmutableGraph graph, SummaryStats tot, boolean symm){
        InterestingNode temp;
        DoubleArrayList list;
        list = MyBFS.BFS(graph,ID);
        tot.addAll(list);
        if( (temp = interestingNodes.get(ID)) != null){
            SummaryStats act = new SummaryStats();
            act.addAll(list);
            temp.setAvgDistance(act.mean(), symm);
            temp.setMaxDistance((int) act.max(), symm);
            if(!symm)
                temp.setReached(list.size(), MyBFS.BFS(this.invertedG, ID).size());
            else
                temp.setReached(list.size());
        }
        return GlobalData.getNumNodes()-list.size();
    }

    private void setStronglyConnected(ImmutableGraph graph, GlobalData gd){
        StronglyConnectedComponents scc = StronglyConnectedComponents.compute(graph, true, null);
        int[] size = scc.computeSizes();
        scc.sortBySize(size);
        gd.setStronglyConn(size[size.length-1]);
    }


    private void startAnalysis(int graphID,LinkedHashSet<IntArrayList>  matrix){
        if(matrix.isEmpty())
            LOGGER.info("No edges found");
        Iterator<IntArrayList> iter = matrix.iterator();
        int[][] edges = new int[matrix.size()][2];
        int i = 0;
        while(iter.hasNext()){
            edges[i] = iter.next().toIntArray();
            i++;
        }
        this.normalG = new ArrayListMutableGraph(idMapping.size(),edges).immutableView();
        this.invertedG = Transform.transpose(normalG, new ProgressLogger());
        this.symmG = Transform.symmetrize(normalG,invertedG, new ProgressLogger());
        LOGGER.info("Graphs correctly created");
        GlobalData.setNumNodes(normalG.numNodes());
        GlobalData.setNumEdges(normalG.numArcs());
        this.getGraphStats();
        String _DATA = graphID+","+GlobalData.getNumNodes()+","+GlobalData.getNumEdges()+";;"+gug.getData()+";"+gdg.getData()+";"+this.getDataInterestingNodes()+"\n";
        writeCSV(_DATA);
    }

    private void reset(LinkedHashSet<IntArrayList>  matrix){
        this.gdg = new GlobalDirectedGraph();
        this.gug = new GlobalUndirectedGraph();
        idMapping.clear();
        interestingNodes.clear();
        matrix.clear();
    }

    private String getDataInterestingNodes(){
        String _DATA = "";
        InterestingNode temp;
        for(int i : treeSet.keySet()){
            if( (temp = interestingNodes.get(idMapping.get(i))) != null )
                _DATA += temp.getInfos();
            else
                _DATA += new InterestingNode(i).getInfos();
        }
        return _DATA;
    }

    private void writeCSV(String _DATA){
        try {
            FileUtils.writeStringToFile(file, _DATA, append);
            if(!append)
                append = true;
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
        }
    }


}
