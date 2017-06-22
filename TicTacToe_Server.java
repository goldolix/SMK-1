import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import javax.swing.*;
 
public class TicTacToeServer extends JFrame {
 
   private byte board[];
   private boolean xMove;
   private JTextArea output;
   private Player players[];
   private ServerSocket server;
   private int currentPlayer;
 
   public TicTacToeServer()
   {
      super( "Tic-Tac-Toe Server" );
 
      board = new byte[ 9 ];
      xMove = true;
      players = new Player[ 2 ];
      currentPlayer = 0;
  
      // Server Socket wird aufgesetzt
      try {
         server = new ServerSocket( 5000, 2 );
      }
      catch( IOException e ) {
         e.printStackTrace();
         System.exit( 1 );
      }
 
      output = new JTextArea();
      getContentPane().add( output, BorderLayout.CENTER );
      output.setText( "Server wartet auf Verbindung\n" );
 
      setSize( 300, 300 );
    
   }
 
   // Wartet auf auf zwei Spieler um das Spiel zu starten
   public void execute()
   {
      for ( int i = 0; i < players.length; i++ ) {
         try {
            players[ i ] =
               new Player( server.accept(), this, i );
            players[ i ].start();
         }
         catch( IOException e ) {
            e.printStackTrace();
            System.exit( 1 );
         }
      }
 
      // Player X is suspended until Player O connects.
      // Resume player X now.      
      /*
       * Spieler X darf nicht ziehen solange Spieler O verbindet.
       * Spieler X darf weitermachen.
       */
      synchronized ( players[ 0 ] ) {
         players[ 0 ].threadSuspended = false;   
         players[ 0 ].notify();
      }
   
   }
    
   public void display( String s )
   {
      output.append( s + "\n" );
   }
  
   /*
    * Bestimmt ob ein Zug gültig ist.
    * Diese Methode ist synchron da nur ein Zug aufeinmal gemacht werden darf.
    */
   
   public synchronized boolean validMove( int loc,
                                          int player )
   {
      boolean moveDone = false;
 
      while ( player != currentPlayer ) {
         try {
            wait();
         }
         catch( InterruptedException e ) {
            e.printStackTrace();
         }
      }
 
      if ( !isOccupied( loc ) ) {
         board[ loc ] =
            (byte) ( currentPlayer == 0 ? 'X' : 'O' );
         currentPlayer = ( currentPlayer + 1 ) % 2;
         players[ currentPlayer ].otherPlayerMoved( loc );
         notify();    // teilt wartendem Spieler mit weiter zu machen
         return true;
      }
      else
         return false;
   }
 
   public boolean isOccupied( int loc )
   {
      if ( board[ loc ] == 'X' || board [ loc ] == 'O' )
          return true;
      else
          return false;
   }
 
   public boolean gameOver()
   {
      // Hier fehlt noch die Sieg Bedingung, wir kamen hier nicht mehr weiter.
      return false;
   }
 
   public static void main( String args[] )
   {
      TicTacToeServer game = new TicTacToeServer();
 
      game.addWindowListener( new WindowAdapter() {
        public void windowClosing( WindowEvent e )
            {
               System.exit( 0 );
            }
         }
      );
 
      game.execute();
   }
}