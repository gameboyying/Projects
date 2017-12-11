import java.io.FileWriter;
import java.net.URISyntaxException;
import java.util.Scanner;

/**
 * Created by yinningliu on 3/25/17.
 */
public class TSPtoKML {

    public String toKML(SinglyLinkedList aList, Vertex[] aVertices, SinglyLinkedList oList, Vertex[] oVertices){

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        sb.append("<kml xmlns=\"http://earth.google.com/kml/2.2\">\n");
        sb.append("<Document>\n");
        sb.append("<name>Pittsburgh TSP</name>\n");
        sb.append("<description>TSP on Crime</description>\n");
        sb.append("<Style id=\"style6\">\n");
        sb.append("<LineStyle>\n");
        sb.append("<color>73FF0000</color>\n");
        sb.append("<width>5</width>\n");
        sb.append("</LineStyle>\n");
        sb.append("</Style>\n");
        sb.append("<Style id=\"style5\">\n");
        sb.append("<LineStyle>\n");
        sb.append("<color>507800F0</color>\n");
        sb.append("<width>5</width>\n");
        sb.append("</LineStyle>\n");
        sb.append("</Style>\n");
        sb.append("<Placemark>\n");
        sb.append("<name>TSP Path</name>\n");
        sb.append("<description>TSP Path</description>\n");
        sb.append("<styleUrl>#style6</styleUrl>\n");
        sb.append("<LineString>\n");
        sb.append("<tessellate>1</tessellate>\n");
        sb.append("<coordinates>\n");

        // create Approximate TSP
        SingleNode dummyHead = aList.getHead();
        while(dummyHead!=null){
            String temp = aVertices[dummyHead.getC()].getValue();
            String[] tempData = temp.split(",");
            double y = Double.valueOf(tempData[8]) + 0.001;
            double x = Double.valueOf(tempData[7]) + 0.001;
            sb.append( y + "," + x + ",0.000000\n");
            dummyHead  = dummyHead.getNext();
        }

        sb.append("</coordinates>\n");
        sb.append("</LineString>\n");
        sb.append("</Placemark>\n");
        sb.append("<Placemark>\n");
        sb.append("<name>Optimal Path</name>\n");
        sb.append("<description>Optimal Path</description>\n");
        sb.append("<styleUrl>#style5</styleUrl>\n");
        sb.append("<LineString>\n");
        sb.append("<tessellate>1</tessellate>\n");
        sb.append("<coordinates>\n");

        // create optimalTSP
        dummyHead = oList.getHead();
        while(dummyHead!=null){
            String temp = oVertices[dummyHead.getC()].getValue();
            String[] tempData = temp.split(",");
            sb.append(tempData[8] + "," + tempData[7] + ",0.000000\n");
            dummyHead  = dummyHead.getNext();
        }

        sb.append("</coordinates>\n");
        sb.append("</LineString>\n");
        sb.append("</Placemark>\n");
        sb.append("</Document>\n");
        sb.append("</kml>\n");

        return sb.toString();
    }

    public static void main(String[] args) throws java.io.IOException,URISyntaxException {

        System.out.println("Enter Start Index");

        //input start index
        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine();
        System.out.println("Enter End Index");

        //input end index
        scanner = new Scanner(System.in);
        String e = scanner.nextLine();

        // you must input start and end index
        if(s.equals("")||e.equals("")){
            System.out.println("please enter both start index and end index");
            System.out.println("please restart this program");
            return;
        }

        int start = Integer.valueOf(s);
        int end = Integer.valueOf(e);

        if(start>end){
            System.out.println("You have input a wrong start index or end index");
            return;
        }

        ApproximateTSP approximateTSP = new ApproximateTSP();

        // load data into graph
        approximateTSP.loadData(start, end);

        // create our MST and calculate TSP and getMiles
        approximateTSP.buildMST();

        // get optimalTSP
        OptimalTSP optimalTSP = new OptimalTSP();

        optimalTSP.loadData(start, end);

        optimalTSP.findMinimalPath();

        TSPtoKML loc = new TSPtoKML();

        String output =loc.toKML(approximateTSP.getTSPTour(),approximateTSP.getGraph().getCrimeRecords(),optimalTSP.getBestList(),optimalTSP.getGraph().getCrimeRecords());
        if(output!=null && !output.equals("")) {
            FileWriter fw = new FileWriter("PGHCrimes.KML");
            fw.write(output);
            fw.close();
        }
    }
}
