//package org.blackdog;
//
//import java.awt.FlowLayout;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.WindowAdapter;
//import java.awt.event.WindowEvent;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.ThreadPoolExecutor;
//import javax.swing.JButton;
//import javax.swing.JFrame;
//import javazoom.jl.decoder.JavaLayerException;
//import javazoom.jl.decoder.JavaLayerUtils;
//import javazoom.jl.player.Player;
//
///**
// *
// * @author alexis
// */
//public class Main extends WindowAdapter implements ActionListener
//{
//    /** bouton start */
//    JButton buttonStart = null;
//    
//    /** bouton stop */
//    JButton buttonStop = null;
//    
//    /** player */
//    Player player = null;
//    
//    /** thread */
//    Thread thread = null;
//    
//    /** Creates a new instance of Main */
//    public Main()
//    {
//        
//        String file = "/mnt/data/music/vrac/calogero - si seulement.mp3";
//        try
//        {   player = new Player(new FileInputStream(file)); }
//        catch (FileNotFoundException ex)
//        {   ex.printStackTrace(); }
//        catch (JavaLayerException ex)
//        {   ex.printStackTrace(); }
//    }
//
//    /**
//     * Invoked when a window has been closed.
//     */
//    public void windowClosed(WindowEvent e)
//    {   System.exit(0); }
//    
//    private void show()
//    {   JFrame frame = new JFrame();
//        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        
//        frame.addWindowListener(this);
//        
//        frame.getContentPane().setLayout(new FlowLayout());
//        
//        buttonStart = new JButton("Start");
//        buttonStart.addActionListener(this);
//        buttonStop  = new JButton("Stop");
//        buttonStop.addActionListener(this);
//        
//        frame.getContentPane().add(buttonStart);
//        frame.getContentPane().add(buttonStop);
//        
//        frame.pack();
//        frame.setVisible(true);
//    }
//    
//    public static void main(String[] args)
//    {
//        System.err.println("####################################");
//        
//        Main m = new Main();
//        m.show();
//        
//        while(true)
//        {   try
//            {   Thread.sleep(2000); }
//            catch (InterruptedException ex)
//            {   ex.printStackTrace(); }
//        }
//        
////        System.err.println("####################################");
//    }
//
//    /**
//     * Invoked when an action occurs.
//     */
//    public void actionPerformed(ActionEvent e)
//    {
//        if ( e.getSource() == this.buttonStart )
//        {   
//            this.thread = new Thread(new Runnable()
//            {
//                public void run()
//                {   try
//                    {   player.play(); }
//                    catch (JavaLayerException ex)
//                    {   ex.printStackTrace(); }
//                }
//            });
//            
//            this.thread.start();
//        }
//        else
//            this.player.close();
//    }
//    
//}
