import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class Player extends Thread {
   private Socket connection;
   private DataInputStream input;
   private DataOutputStream output;
   private TicTacToeServer control;
   private int zahl;
   private char zeichen;
   protected boolean threadSuspended = true;
 
   public Player( Socket s, TicTacToeServer t, int num )
   {
      zeichen = ( num == 0 ? 'X' : 'O' );
 
      connection = s;
       
      try {
         input = new DataInputStream(
                    connection.getInputStream() );
         output = new DataOutputStream(
                    connection.getOutputStream() );
      }
      catch( IOException e ) {
         e.printStackTrace();
         System.exit( 1 );
      }
 
      control = t;
      zahl = num;
   }
 
   //Methode zur Prüfung ob der andere Spieler seinen Zug gemacht hat
   
   public void otherPlayerMoved( int loc )
   {
      try {
         output.writeUTF( "Gegner hat gewaehlt" );
         output.writeInt( loc );
      }
      catch ( IOException e ) { e.printStackTrace(); }
   }
 
   public void run()
   {
      boolean done = false;
 
      try {
         control.display( "Spieler " +
            ( zahl == 0 ? 'X' : 'O' ) + " connected" );
         output.writeChar( zeichen );
         output.writeUTF( "Spieler " +
            ( zahl == 0 ? "X connected\n" :
                            "O connected, please wait\n" ) );
 
         // Wartet auf anderen Spieler
         if ( zeichen == 'X' ) {
            output.writeUTF( "Warten auf anderen Spieler" );
 
            try {
               synchronized( this ) {   
                  while ( threadSuspended )
                     wait();  
               }
            } 
            catch ( InterruptedException e ) {
               e.printStackTrace();
            }
 
            output.writeUTF(
               "Other player connected. Your move." );
         }
 
         // Spiel wird gespielt
         while ( !done ) {
            int location = input.readInt();
            
            //Korrekter Zug
            if ( control.validMove( location, zahl ) ) {
               control.display( "loc: " + location );
               output.writeUTF( "Gueltiger Zug." );
            }//Inkorrekter Zug
            else
               output.writeUTF( "Falscher  Zug, versuchs nochmal" );
 
            if ( control.gameOver() )
               done = true;
         }         
 
         connection.close();
      }
      catch( IOException e ) {
         e.printStackTrace();
         System.exit( 1 );
      }
   }
}                                                
