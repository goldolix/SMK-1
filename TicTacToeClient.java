import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import javax.swing.*;
 
/*
 * Client Klasse um zwei Spieler zusammen Tic-Tac-Toe Spielen zu lassen
 */
public class TicTacToeClient extends JApplet
                             implements Runnable {
   private JTextField id;
   private JTextArea display;
   private JPanel boardPanel, panel2;
   private Feld board[][], currentFeld;
   private Socket connection;
   private DataInputStream input;
   private DataOutputStream output;
   private Thread outputThread;
   private char myZeichen;
   private boolean myZug;
 
   // Setzt die UI (User-Interface) und das Spielfeld auf
   public void init()
   {
      display = new JTextArea( 4, 30 );
      display.setEditable( false );
      getContentPane().add( new JScrollPane( display ),
                            BorderLayout.SOUTH );
 
      boardPanel = new JPanel();
      GridLayout layout = new GridLayout( 3, 3, 0, 0 );
      boardPanel.setLayout( layout );
 
      board = new Feld[ 3 ][ 3 ];
 
      /*
       * Wenn ein neues Feld erstellt wird ist der Value von 0-8 auf dem Spielfeld.
       * Erste Reihe : 0,1,2 -- Zweite Reihe 3,4,5 -- Dritte Reihe 6,7,8
       */
      for ( int reihe = 0; reihe < board.length; reihe++ )
      {
         for ( int spalte = 0;
                   spalte < board[ reihe ].length; spalte++ ) {
            board[ reihe ][ spalte ] =
               new Feld( ' ', reihe * 3 + spalte );
            board[ reihe ][ spalte ].addMouseListener(
               new FeldListener(
                  this, board[ reihe ][ spalte ] ) );
 
            boardPanel.add( board[ reihe ][ spalte ] );        
         }
      }
 
      id = new JTextField();
      id.setEditable( false );
       
      getContentPane().add( id, BorderLayout.NORTH );
       
      panel2 = new JPanel();
      panel2.add( boardPanel, BorderLayout.CENTER );
      getContentPane().add( panel2, BorderLayout.CENTER );
   }
 
  
   /*
    * Stellt die connection zum Server her.
    * Startet getrennte thread um der applikation das updaten der text area zu erlauben.
    */
   public void start()
   {
      try {
         connection = new Socket(
            InetAddress.getByName( "127.0.0.1" ), 5000 );
         input = new DataInputStream(
                        connection.getInputStream() );
         output = new DataOutputStream(
                        connection.getOutputStream() );
      }
      catch ( IOException e ) {
         e.printStackTrace();         
      }
 
      outputThread = new Thread( this );
      outputThread.start();
   }

   /*
    * Kontrollierte Thread die durchgehende aktualisierung der text area ermöglicht.
    */
   public void run()
   {
	  // Erster Spieler erhält sein Zeichen X oder O
      try {
         myZeichen = input.readChar();
         id.setText( "Du bist Spieler \"" + myZeichen + "\"" );
         myZug = ( myZeichen == 'X' ? true  : false );
      }
      catch ( IOException e ) {
         e.printStackTrace();         
      }
 
      // Erhält Nachrichten und sendet sie zum client
      
      while ( true ) {
         try {
            String s = input.readUTF();
            processMessage( s );
         }
         catch ( IOException e ) {
            e.printStackTrace();         
         }
      }
   }
 
   // Die Nachrichten während des Spiels werden an den Client gesendet
   
   public void processMessage( String s )
   {
      if ( s.equals( "Gueltiger Zug." ) ) {
         display.append( "Gueltiger Zug, bitte warten.\n" );
         currentFeld.setZeichen( myZeichen );
         currentFeld.repaint();
      }
      else if ( s.equals( "Falscher Zug, versuchs nochmal" ) ) {
         display.append( s + "\n" );
         myZug = true;
      }
      else if ( s.equals( "Gegner hat gewaehlt" ) ) {
         try {
            int loc = input.readInt();
  
            board[ loc / 3 ][ loc % 3 ].setZeichen(
                  ( myZeichen == 'X' ? 'O' : 'X' ) );
            board[ loc / 3 ][ loc % 3 ].repaint();
                  
            display.append(
               "Gegner hat gewaehlt. Dein Zug.\n" );
            myZug = true;
         }
         catch ( IOException e ) {
            e.printStackTrace();         
         }
      }
      else
         display.append( s + "\n" );
 
      display.setCaretPosition(
         display.getText().length() );
   }
 
   //Geklicktes Feld wird übermittelt
   
   public void sendClickedFeld( int loc )
   {
      if ( myZug )
         try {
            output.writeInt( loc );
            myZug = false;
         }
         catch ( IOException ie ) {
            ie.printStackTrace();         
         }
   }
 
   public void setCurrentFeld( Feld s )
   {
      currentFeld = s;
   }
}