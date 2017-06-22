import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

class Feld extends JPanel {
   private char zeichen;
   private int location;
 
   public Feld( char m, int loc)
   {
      zeichen = m;
      location = loc;
      setSize ( 30, 30 );
       
      setVisible(true);
   }
 
   public Dimension getPreferredSize() { 
      return ( new Dimension( 30, 30 ) );
   }
 
   public Dimension getMinimumSize() {
      return ( getPreferredSize() );
   }
 
   public void setZeichen( char c ) { zeichen = c; }
 
   public int getFeldLocation() { return location; }
 
   public void paintComponent( Graphics g )
   {
      super.paintComponent( g );
      g.drawRect( 0, 0, 29, 29 );
      g.drawString( String.valueOf( zeichen ), 11, 20 );   
   }
}
 
class FeldListener extends MouseAdapter {
   private TicTacToeClient applet;
   private Feld Feld;
 
   public FeldListener( TicTacToeClient t, Feld s )
   {
      applet = t;
      Feld = s;
   }
 
   public void mouseReleased( MouseEvent e )
   {
      applet.setCurrentFeld( Feld );
      applet.sendClickedFeld( Feld.getFeldLocation() );
   }
}

