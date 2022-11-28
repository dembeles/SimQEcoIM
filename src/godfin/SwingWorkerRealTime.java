package godfin;

/**
 * Creates a simple real-time chart
 */

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

/**
 * Creates a real-time chart using SwingWorker
 */
public class SwingWorkerRealTime {

  MySwingWorker mySwingWorker;
  SwingWrapper<XYChart> sw;
  XYChart chart;

  //public static void main(String[] args) throws Exception {

  //  SwingWorkerRealTime swingWorkerRealTime = new SwingWorkerRealTime();
  //  swingWorkerRealTime.go();
 // }
  void go() {

    // Create Chart
    chart = QuickChart.getChart("Realtime System Power Consumption", "Time", "Power", "randomWalk", new double[] { 0 }, new double[] { 0 });
    chart.getStyler().setLegendVisible(false);
    chart.getStyler().setXAxisTicksVisible(true);
    chart.getStyler().setAxisTicksLineVisible(true);
    chart.getStyler().setToolTipBackgroundColor(Color.green);
    chart.getStyler().setPlotTicksMarksVisible(true);
    
    

    // Show it
    sw = new SwingWrapper<XYChart>(chart);
    sw.setTitle("PowerLine");
    JFrame frame = sw.displayChart();
    mySwingWorker = new MySwingWorker();
    mySwingWorker.execute();
    javax.swing.SwingUtilities.invokeLater(
    ()->frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE)
);

  frame.addWindowListener(new java.awt.event.WindowAdapter() {
    @Override
    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
        if (JOptionPane.showConfirmDialog(frame, 
            "Are you sure you want to close this window?", "Close Window?", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
            //System.exit(0);
            mySwingWorker.cancel(true);
        }
    }
});
    
    
  }

  //Container getComponent() {
  //  return sw.displayChart().getContentPane();
  //  }

  private class MySwingWorker extends SwingWorker<Boolean, double[]> {

    LinkedList<Double> fifo = new LinkedList<Double>();

    public MySwingWorker() {

      fifo.add(0.0);
      //fifo.add(MainFen.B0);
    }

    @Override
    protected Boolean doInBackground() throws Exception {

      while (!isCancelled()) {
            // Get Povalue from WattsUpro
         if(!MainFenMDB.app) fifo.add(fifo.get(fifo.size() - 1) + Math.random() - .5);
         else fifo.add(Double.parseDouble(WattsUpWorker.Prtime));
            
        if (fifo.size() > 500) {
          fifo.removeFirst();
        }

        double[] array = new double[fifo.size()];
        for (int i = 0; i < fifo.size(); i++) {
          array[i] = fifo.get(i);
        }
        publish(array);

        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          // eat it. caught when interrupt is called
          System.out.println("MySwingWorker shut down.");
        }

      }

      return true;
    }

    @Override
    protected void process(List<double[]> chunks) {

      System.out.println("number of chunks: " + chunks.size());

      double[] mostRecentDataSet = chunks.get(chunks.size() - 1);

      chart.updateXYSeries("randomWalk", null, mostRecentDataSet, null);
      sw.repaintChart();

      long start = System.currentTimeMillis();
      long duration = System.currentTimeMillis() - start;
      try {
        Thread.sleep(1000 - duration); // 40 ms ==> 25fps
        // Thread.sleep(400 - duration); // 40 ms ==> 2.5fps
      } catch (InterruptedException e) {
      }

    }
  }
}